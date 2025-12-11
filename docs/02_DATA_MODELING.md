# 02. 데이터 모델링 및 DB 연동 (Data Modeling & Database Integration)

> **Commit**: `f4bc75e`
> **주제**: Spring Data JPA, Entity 설계, MySQL/MongoDB 설정

이 문서는 **실무에서 데이터 모델링을 할 때 참고할 수 있는 레퍼런스**입니다. 설정 방법, Entity 구현 패턴, 그리고 Kotlin과 JPA를 함께 사용할 때의 주의사항을 정리했습니다.

## 1. 설정 (Configuration)

DB 연동 시 `build.gradle.kts`에 추가해야 할 필수 의존성입니다.

```kotlin
dependencies {
    // 1. JPA & MySQL
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("mysql:mysql-connector-java:8.0.33")
    
    // 2. Kotlin JPA Support (필수)
    // 리플렉션을 통해 런타임에 객체를 조작하기 위해 필요
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // 3. MongoDB (Optional)
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
}
```

## 2. Entity 구현 패턴 (Implementation Pattern)

Kotlin에서 JPA Entity를 작성할 때 사용하는 표준 패턴입니다. 복사해서 템플릿으로 사용하세요.

### 2.1. 기본 Entity 구조 (Account 예시)

```kotlin
@Entity // 1. JPA Entity 선언
@Table(name = "account") // 2. 테이블명 매핑 (예약어 피하기)
data class Account(
    
    // 3. PK 전략: ULID 사용 (String)
    @Id
    @Column(name = "ulid", length = 12, nullable = false)
    val ulid: String, 

    // 4. N:1 연관관계 (지연 로딩 필수)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_ulid", nullable = false)
    val user: User,

    // 5. 금융 데이터: BigDecimal 사용
    @Column(name = "balance", nullable = false, precision = 15, scale = 2)
    var balance : BigDecimal = BigDecimal.ZERO,

    // 6. 감사(Auditing) 필드
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),
)
```

### 2.2. 연관관계 매핑 치트시트 (Cheat Sheet)

| 관계 | 어노테이션 | 설명 | 권장 설정 |
| :--- | :--- | :--- | :--- |
| **N:1** | `@ManyToOne` | 가장 많이 사용됨. 외래 키를 가진 쪽. | `fetch = FetchType.LAZY` (필수) |
| **1:N** | `@OneToMany` | 조회 전용. N:1의 반대편. | `mappedBy = "필드명"` (필수) |
| **1:1** | `@OneToOne` | 주 테이블에 외래 키 저장. | `fetch = FetchType.LAZY` |

## 3. 핵심 설계 가이드 (Design Guide)

### 3.1. PK 전략: 왜 ULID인가?
Auto Increment(숫자 증가) 대신 ULID를 사용하는 이유와 장점입니다.

| 전략 | 정렬 가능 | 분산 환경 안전 | 보안성 | 설명 |
| :--- | :---: | :---: | :---: | :--- |
| **Auto Increment** | O | X | 낮음 | DB에 의존적. URL 파라미터로 데이터 갯수 유추 가능. |
| **UUID** | X | O | 높음 | 랜덤 문자열이라 인덱스 성능 저하 (DB 파편화). |
| **ULID** | **O** | **O** | **높음** | **시간 순 정렬 가능** + 랜덤성. 인덱스 성능 좋음. |

### 3.2. 데이터 타입: BigDecimal
돈과 관련된 필드는 무조건 `BigDecimal`을 사용해야 합니다.

- **문제점**: `Double`, `Float`는 부동 소수점 방식으로, `0.1 + 0.2 = 0.30000000000000004` 같은 오차가 발생함.
- **해결책**: `BigDecimal`은 숫자를 정확하게 저장하고 연산함.
- **주의사항**: 생성 시 문자열을 사용하세요.
  ```kotlin
  val amount = BigDecimal("0.1") // (O) 정확함
  val amount2 = BigDecimal(0.1)  // (X) 이미 오차가 포함된 상태로 생성됨
  ```

## 4. Kotlin + JPA 주의사항 (Checklist)

Kotlin으로 JPA를 사용할 때 자주 발생하는 문제를 방지하기 위한 체크리스트입니다.

1.  **`plugin.spring` 적용 확인**:
    - Kotlin 클래스는 기본적으로 `final`입니다. JPA는 프록시 생성을 위해 클래스가 상속 가능(`open`)해야 합니다.
    - `build.gradle.kts`에 `kotlin("plugin.spring")`이 있으면 `@Entity`가 붙은 클래스를 자동으로 `open` 처리해줍니다.
2.  **기본 생성자 (No-arg Constructor)**:
    - JPA 스펙상 파라미터 없는 기본 생성자가 필수입니다.
    - `plugin.jpa`를 추가하거나, 모든 필드에 기본값(default value)을 주어 해결해야 합니다. (현재 코드는 기본값을 주는 방식 사용 중)
3.  **Data Class 사용 시 주의**:
    - `data class`는 `equals()`, `hashCode()`, `toString()`을 자동으로 생성합니다.
    - **문제**: 양방향 연관관계(User <-> Account)가 있을 때 `toString()`이 서로를 계속 호출하여 **StackOverflow**가 발생할 수 있습니다.
    - **해결**: `toString()`을 오버라이드하여 연관관계 필드는 제외하거나, `data class` 대신 일반 `class`를 사용하는 것을 고려해야 합니다.
