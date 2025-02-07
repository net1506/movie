# OpenAPI 인증 방식 비교와 Swagger 설정

## OpenAPI 인증 방식 비교

### SecurityScheme Type 비교

| **특성**        | **APIKEY**                   | **HTTP (Bearer)**          |
|---------------|------------------------------|----------------------------|
| **Type**      | `SecurityScheme.Type.APIKEY` | `SecurityScheme.Type.HTTP` |
| **Scheme**    | 없음                           | Bearer                     |
| **인증 데이터 위치** | 헤더, 쿼리 파라미터, 쿠키 가능           | HTTP 헤더 (Authorization)    |
| **인증 데이터 형식** | 단순 문자열 (API Key)             | JWT 또는 Bearer Token        |
| **보안 수준**     | 낮음                           | 높음 (토큰 만료 및 서명 포함)         |
| **권장 시나리오**   | 단순 인증                        | 복잡한 인증/권한 관리               |

### 1. APIKEY 방식

#### 설명:

요청 헤더 또는 쿼리 파라미터에 사전에 정의된 키를 포함하여 인증한다.<br>
키는 주로 정적으로 설정된 값을 사용하며, 보안성은 요청에 포함된 키의 보호 수준에 따라 다르다.

#### 특징:

정적 키(API Key) 사용. <br>
요청 헤더, 쿼리 파라미터 또는 쿠키를 통해 전달 가능.<br>
간단한 인증 방식을 제공하며, 주로 내부 API 또는 간단한 애플리케이션에서 사용.<br>
보안 수준이 상대적으로 낮음 (키가 유출되면 보안에 취약).

### 2. HTTP (Bearer) 방식

#### 설명:

Bearer 토큰은 OAuth 2.0 또는 JWT 기반 인증에 주로 사용.<br>
클라이언트는 토큰을 받아 이를 HTTP 헤더의 Authorization 필드에 포함하여 서버로 요청.

#### 특징:

동적 토큰 사용 (JWT 또는 OAuth 토큰).<br>
HTTP 헤더의 Authorization 필드에 포함.<br>
주로 인증 서버와 통합된 보안 환경에서 사용.<br>
토큰 만료, 재발급 등 추가 관리 기능 포함.<br>
상대적으로 높은 보안 수준 제공.

### SecurityScheme 예제 코드

#### 1. APIKEY 인증 방식 설정

```java

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("X-Queue-Token"))
                .components(new Components().addSecuritySchemes("X-Queue-Token", createQueueTokenScheme()));
    }

    private SecurityScheme createQueueTokenScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .name("X-Queue-Token");
    }
}
```

#### 2. HTTP (Bearer) 인증 방식 설정

```java

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");

        return new OpenAPI()
                .components(new Components().addSecuritySchemes("bearerAuth", securityScheme))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
```