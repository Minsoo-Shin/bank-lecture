# 01. 초기 셋팅 (Project Initialization)

> **Commit**: `9c70a6d`
> **주제**: Gradle 설정, Spring Boot 기본 구조, Kotlin 설정

이 문서는 프로젝트의 초기 셋팅 단계에서 사용된 기술과 설정 파일들의 역할을 상세히 설명합니다.

## 1. Gradle & Kotlin DSL (`build.gradle.kts`)

이 프로젝트는 빌드 도구로 **Gradle**을 사용하며, 설정 스크립트 언어로 Groovy 대신 **Kotlin DSL**(`build.gradle.kts`)을 사용합니다.

### 1.1. 주요 특징 (Key Characteristics)

- **언어 (Language)**: 기존의 `build.gradle` 파일이 Groovy를 사용하는 것과 달리, **Kotlin** 프로그래밍 언어를 사용합니다.
- **장점 (Benefits)**: Kotlin DSL은 다음과 같은 강력한 이점을 제공합니다.
    - **정적 타입 검사 (Static Type Checking)**: 컴파일 시점에 오류를 미리 발견할 수 있습니다.
    - **향상된 IDE 지원**: IntelliJ IDEA 등에서 더 나은 코드 자동 완성(Code Completion), 구문 강조(Syntax Highlighting), 코드 탐색(Navigation)
      기능을 제공합니다.
- **목적 (Purpose)**: 소스 코드 컴파일, 테스트 실행, 애플리케이션 패키징, 라이브러리 의존성 관리와 같은 빌드 작업들을 **자동화**하는 것이 핵심 목표입니다.

### 1.2. 주요 구성 요소 (Common Sections)

일반적인 `build.gradle.kts` 파일은 다음과 같은 핵심 블록들로 구성됩니다.

| 섹션 (Section) | 설명 (Description) |
| :--- | :--- |
| **plugins** | 프로젝트에 필요한 Gradle 플러그인(Android, Kotlin, Spring Boot 등)을 적용합니다. 빌드의 핵심 기능을 정의합니다. |
| **repositories** | Gradle이 의존성 라이브러리를 찾을 저장소를 지정합니다. (예: `google()`, `mavenCentral()`) |
| **dependencies** | 프로젝트가 컴파일되고 실행되는 데 필요한 외부 라이브러리나 모듈을 선언합니다. |

### 1.3. Plugins 설정

```kotlin
plugins {
    id("org.springframework.boot") version "3.2.3"
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.spring") version "1.8.0"
    id("io.spring.dependency-management") version "1.0.15.RELEASE"
}
```

- **`org.springframework.boot`**: Spring Boot 애플리케이션을 실행 가능한 JAR로 패키징하고, 의존성을 관리해주는 핵심 플러그인입니다.
- **`kotlin("jvm")`**: Kotlin 코드를 JVM 바이트코드로 컴파일하기 위한 플러그인입니다.
- **`kotlin("plugin.spring")`**: Spring의 클래스들은 기본적으로 상속이 불가능한 `final` 클래스인데, 이 플러그인은 필요한 클래스(예: `@Configuration`,
  `@Transactional`이 붙은 클래스)를 자동으로 `open` 상태로 만들어줍니다. (Kotlin의 `all-open` 플러그인 래퍼)

### 1.4. Repositories 설정

```kotlin
repositories {
    mavenCentral()
}
```

- **`mavenCentral()`**: 가장 널리 사용되는 Maven 중앙 저장소에서 라이브러리를 다운로드하도록 설정합니다.

### 1.5. 의존성 관리 (Dependencies)

```kotlin
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web:3.2.3")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.2.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    testImplementation(kotlin("test"))
}
```

- **`implementation`**: 컴파일 및 런타임에 필요한 라이브러리입니다.
- **`testImplementation`**: 테스트 코드 실행 시에만 필요한 라이브러리입니다.

## 2. Gradle Wrapper (`gradlew`)

프로젝트 루트에 포함된 `gradlew` (Unix/Mac) 및 `gradlew.bat` (Windows) 스크립트입니다.

- **역할**: 개발자의 로컬 환경에 Gradle이 설치되어 있지 않아도, 프로젝트에 지정된 특정 버전의 Gradle을 자동으로 다운로드하여 빌드를 수행하게 해줍니다.
- **장점**: 팀원 간의 Gradle 버전 불일치로 인한 빌드 오류를 방지합니다.
- **사용법**:
  ```bash
  ./gradlew build  # 프로젝트 빌드
  ./gradlew bootRun # 애플리케이션 실행
  ```

## 3. Spring Boot Application 구조 (`Main.kt`)

```kotlin
@SpringBootApplication
class Application

fun main(args: Array<String>) {
    println("시작")
    SpringApplication.run(Application::class.java, *args)
}
```

### 3.1. `@SpringBootApplication`

이 어노테이션은 다음 세 가지 주요 어노테이션을 포함합니다:

1. **`@Configuration`**: 빈(Bean) 설정을 위한 클래스임을 명시합니다.
2. **`@EnableAutoConfiguration`**: 클래스 패스에 있는 라이브러리들을 기반으로 Spring Boot가 합리적인 기본 설정을 자동으로 적용합니다.
3. **`@ComponentScan`**: 현재 패키지 하위의 `@Component`, `@Service`, `@Repository` 등을 스캔하여 빈으로 등록합니다.

### 3.2. `main` 함수

- Kotlin의 최상위 함수(Top-level function)로 정의되어 있습니다.
- `SpringApplication.run()`을 호출하여 내장 톰캣(Tomcat) 웹 서버를 띄우고 스프링 컨테이너를 초기화합니다.
- `*args`: 스프레드 연산자(Spread Operator)로, 배열을 가변 인자(Vararg)로 풀어줍니다.

## 4. 학습 포인트 (Key Takeaways)

이 챕터에서 중점적으로 이해해야 할 핵심 개념들입니다.

### 4.1. Kotlin DSL의 장점

- **내용**: 기존 Groovy 기반 설정 대신 Kotlin 언어를 사용하여 빌드 스크립트를 작성합니다.
- **Why?**: Groovy는 동적 타이핑 언어라 IDE의 지원(자동 완성, 오류 검출)이 제한적이지만, Kotlin DSL은 **정적 타이핑**을 지원하므로 컴파일 타임에 오류를 잡을 수 있고 IDE의 강력한
  자동 완성 기능을 활용할 수 있어 생산성이 높습니다.

### 4.2. Gradle Wrapper의 중요성

- **내용**: 프로젝트 내에 Gradle 실행 환경을 포함시키는 방식입니다.
- **Why?**: 개발자 A는 Gradle 7.0, 개발자 B는 Gradle 8.0을 사용한다면 빌드 결과가 달라질 수 있습니다. Wrapper를 사용하면 프로젝트가 **지정된 버전의 Gradle을 강제로 사용
  **하게 하여, 어떤 환경(로컬, CI/CD 서버)에서도 동일한 빌드 결과를 보장합니다.

### 4.3. Kotlin과 Spring의 호환성 (`plugin.spring`)

- **내용**: Kotlin의 `all-open` 플러그인을 Spring에 맞게 래핑한 플러그인입니다.
- **Why?**: Spring의 핵심 기능인 AOP(트랜잭션 관리 등)와 CGLIB 프록시는 **상속(Inheritance)**을 기반으로 동작합니다. 하지만 Kotlin의 클래스와 메서드는 기본적으로 *
  *`final`**(상속 불가)입니다. 개발자가 일일이 `open` 키워드를 붙이는 번거로움을 없애기 위해, 이 플러그인이 컴파일 시점에 Spring 관련 어노테이션이 붙은 클래스를 자동으로 `open` 상태로
  변경해줍니다.

## 5. 자주 묻는 질문 (FAQ) - Gradle 개념 잡기

### Q1. Plugins, Dependencies, Repositories의 차이는 무엇인가요?

이 세 가지는 Gradle이 빌드를 수행하는 과정에서 각기 다른 역할을 수행합니다. 요리에 비유하면 이해하기 쉽습니다.

| 개념 | 역할 | 비유 (요리) | 예시 |
| :--- | :--- | :--- | :--- |
| **Plugins** | **작업자/능력**: Gradle에게 새로운 기능(빌드, 패키징 방법 등)을 부여합니다. | **요리 기술**: 제빵 기술, 한식 조리법 | `kotlin("jvm")`, `spring-boot` |
| **Repositories** | **저장소/마트**: 라이브러리(재료)를 어디서 다운로드할지 지정합니다. | **마트**: 이마트, 코스트코 | `mavenCentral()`, `google()` |
| **Dependencies** | **재료/부품**: 실제 프로젝트에서 사용할 외부 라이브러리입니다. | **식재료**: 밀가루, 설탕 | `okhttp`, `spring-boot-starter-web` |

### Q2. `okhttp` 라이브러리는 어떻게 가져오나요?

`okhttp`는 **Dependency(재료)**이며, **Repository(마트)**에서 가져옵니다.

1. **Dependencies 선언**: "나 `okhttp` 필요해"라고 주문서를 작성합니다.
   ```kotlin
   dependencies {
       implementation("com.squareup.okhttp3:okhttp:4.12.0")
   }
   ```
2. **Repositories 탐색**: Gradle은 설정된 저장소(`mavenCentral()`)로 가서 해당 라이브러리를 찾습니다.
   ```kotlin
   repositories {
       mavenCentral() // "재료는 여기서 찾아와"
   }
   ```
3. **다운로드**: 저장소에서 `okhttp` 파일을 다운로드하여 프로젝트에 추가합니다.

**결론**: `plugins`로 빌드 환경을 구축하고, `repositories`에 지정된 곳에서 `dependencies`에 적힌 라이브러리를 가져와 개발을 진행합니다.
