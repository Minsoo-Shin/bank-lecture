# 03. Redis 초기 셋팅
---

## Spring Data Redis의 역할

1. 클라이언트 추상화 계층 제공
    - Redis와 통신할 수 있는 여러 라이브러리 (Lettuce, Jedis 등)들을 일관된 방식으로 Redis를 사용할 수 있도록 표준 인터페이스를 제공해줍니다. (
      `RedisConnectionFactory`, `RedisTemplate`)
    - 내부 클라이언트를 쉽게 교체할 수 있음
2. 클라이언트 직렬화 및 역직렬화 지원
    - Serializer 기능 제공 (객체 <-> 바이트 배열)
    - `StringRedisSerializer`나 `Jackson2JsonRedisSerializer` 등
3. 데이터 저장소 매핑
    - Key-Value 저장소인 Redis를 마치 객체 지향 데이터베이스처럼 사용하여, 개발자가 Redis의 원시 명령(SET, HSET) 대신, 메서드 호출(save(), findById())을 통해 작업하게
      하는 것입니다.

    ``` kotlin
    import org.springframework.data.annotation.Id
    import org.springframework.data.redis.core.RedisHash // RedisHash 어노테이션 사용
    
    @RedisHash("users") // Redis Key의 Prefix를 'users:'로 지정합니다.
    data class User(
        @Id // 이 필드를 Key의 고유 ID로 사용합니다. (예: users:1001)
        val id: Long,
        val username: String,
        val email: String,
        val createdAt: Long = System.currentTimeMillis()
    )
   
   // RedisUserRepository
   import org.springframework.data.repository.CrudRepository

    interface UserRepository : CrudRepository<User, Long> {
    // Spring이 이 메서드 이름을 분석하여 쿼리를 자동으로 생성합니다.
    fun findByUsername(username: String): User?
    }
    ```

---

## Redisson 역할

Spring Boot의 spring-boot-starter-data-redis는 가볍고 빠른 성능의 Lettuce를
기본 클라이언트로 내장하여 일반적인 데이터 캐싱과 CRUD 작업에 최적화되어 있으나,
분산 락과 같은 고도화된 동시성 제어 기능은 직접 구현해야 하는 한계가 있습니다. 따라서 스핀 락(Spin Lock) 방식의 Redis 부하 문제를 해결하고
락 만료 자동 연장(Watchdog)과 같은 안정적인 기능을 확보하기 위해 Redisson 라이브러리(redisson-spring-boot-starter)를 별도로 의존성 주입하며, 실무에서는 일반 데이터 조회는
**Lettuce(RedisTemplate)**로, 분산 락 처리는 **Redisson(RedissonClient)**으로 역할을 분리하여 각 라이브러리의 장점을 극대화하는 방식을 권장합니다.
---

## 설정 클래스

1. LettuceConnectionFactory & redisTemplate: Spring Data Redis를 통해 일반적인 캐싱 및 데이터 CRUD 작업을 수행합니다.
2. redissonClient: Redisson 라이브러리를 통해 분산 잠금과 같은 고급 기능을 사용합니다.

```kotlin
package com.example.config

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.time.Duration

@Configuration
class RedisConfig {


    @Bean
    fun redisConnectionFactory(
        @Value("\${database.redis.host}") host: String,
        @Value("\${database.redis.port}") port: Int,
        @Value("\${database.redis.password:}") password: String?,
        @Value("\${database.redis.database:${0}}") database: Int,
        @Value("\${database.redis.timeout:${10000}}") timeout: Long,
    ): LettuceConnectionFactory {
        val config = RedisStandaloneConfiguration(host, port).apply {
            password?.let { setPassword(it) }
            setDatabase(database)
        }

        val clientConfig = LettuceClientConfiguration.builder()
            .commandTimeout(Duration.ofSeconds(timeout))
            .build()


        return LettuceConnectionFactory(config, clientConfig)
    }

    @Bean
    @Primary
    fun redisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, String> {
        val template = RedisTemplate<String, String>()

        template.connectionFactory = connectionFactory
        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = Jackson2JsonRedisSerializer(String::class.java)
        template.hashKeySerializer = StringRedisSerializer()
        template.hashValueSerializer = Jackson2JsonRedisSerializer(String::class.java)
        template.afterPropertiesSet()

        return template
    }


    @Bean
    fun redissonClient(
        @Value("\${database.redisson.host}") host: String,
        @Value("\${database.redisson.timeout}") timeout: Int,
        @Value("\${database.redisson.password:${null}}") password: String?,
    ): RedissonClient {
        val config = Config()
        // 보통 single server 사용함. 거의 문제 터지는 상황이 없다고 함.
        val singleServerConfig = config.useSingleServer()
            .setAddress(host)
            .setTimeout(timeout)

        if (!password.isNullOrBlank()) {
            singleServerConfig.setPassword(password)
        }

        return Redisson.create(config).also {
            println("redisson create success")
        }
    }
}
```
