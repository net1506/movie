# SEQUENCE DIAGRAM

## 영화 예매 시퀀스 다이어그램

영화 예매 전체 흐름에서 성공 케이스 중심으로 시퀀스 다이어그램을 작성했습니다. 자세한 예외 처리는 각 Use Case 별 시퀀스 다이어그램을 참고하세요.
이 시퀀스 다이어그램은 영화 예매 시스템의 전체적인 흐름을 직관적으로 보여주며, 사용자 경험을 향상시키기 위해 필요한 검증 및 처리 단계를 포함하고 있습니다.

### 대기열 토큰 생성

```mermaid
sequenceDiagram
    actor User
    participant API
    participant 대기열 검증
    title 대기열 토큰 생성 시퀀스 다이어그램
    User ->> API: 대기열 토큰 생성 API 요청
    API ->> 대기열 검증: 대기열 토큰 생성 요청
    대기열 검증 ->> 대기열 검증: 대기열 토큰 생성
    대기열 검증 -->> API: 대기열 토큰 반환
    API -->> User: 대기열 토큰 반환
```

### 상영 중 영화 조회

```mermaid
sequenceDiagram
    actor User
    participant API
    participant 영화
    title 상영 중 영화 조회 시퀀스 다이어그램
    User ->> API: [대기열 검증 통과] 상영 중 영화 조회 API 요청
    API ->> 영화: 상영 중 영화 조회 요청
    영화 -->> API: 상영 중 영화 목록 반환
    API -->> User: 상영 중 영화 목록 반환
```

### 상영 예정 영화 조회

```mermaid
sequenceDiagram
    actor User
    participant API
    participant 영화
    title 상영 예정 영화 조회 시퀀스 다이어그램
    User ->> API: [대기열 검증 통과] 상영 중 영화 조회 API 요청
    API ->> 영화: 상영 예정 영화 조회 요청
    영화 -->> API: 상영 예정 영화 조회 반환
    API -->> User: 상영 예정 영화 조회 반환
```

### 영화관 조회

```mermaid
sequenceDiagram
    actor User
    participant API
    participant 영화관
    title 영화관 조회 시퀀스 다이어그램
    User ->> API: [대기열 검증 통과] 상영 가능한 영화관 조회 API 요청
    API ->> 영화관: 상영 가능한 영화관 조회 요청
    영화관 -->> API: 상영 가능한 영화관 목록 반환
    API -->> User: 상영 가능한 영화관 목록 반환
```

### 상영 일정 조회

```mermaid
sequenceDiagram
    actor User
    participant API
    participant 상영 일정
    title 상영 일정 조회 시퀀스 다이어그램
    User ->> API: [대기열 검증 통과] 상영 일정 조회 API 요청
    API ->> 상영 일정: 상영 일정 조회 요청
    상영 일정 -->> API: 상영 일정 목록 반환
    API -->> User: 상영 일정 목록 반환
```

### 좌석 예약

```mermaid
sequenceDiagram
    actor User
    participant API
    participant 좌석
    participant 사용자
    title 좌석 예약 시퀀스 다이어그램
    User ->> API: [대기열 검증 통과] 좌석 예약 요청
    API ->> 좌석: 좌석 임시 예약 요청
    좌석 ->> 사용자: 사용자 정보 조회 요청
    사용자 -->> 좌석: 사용자 정보 반환
    좌석 ->> 좌석: 좌석 임시 예약 처리
    좌석 -->> API: 좌석 임시 예약 성공 반환
    API -->> User: 좌석 임시 예약 성공 반환
```

### 결제 및 예약 확정

```mermaid
sequenceDiagram
    actor User
    participant API
    participant 지갑
    participant 좌석
    participant 사용자
    title 결제 및 예약 확정 시퀀스 다이어그램
    User ->> API: [대기열 검증 통과] 결제 요청
    API ->> 지갑: 결제 금액 차감 요청
    지갑 ->> 사용자: 사용자 정보 조회 요청
    사용자 -->> 지갑: 사용자 정보 반환
    지갑 ->> 지갑: 결제 금액 차감
    지갑 -->> API: 결제 성공 반환
    API ->> 좌석: 좌석 예약 확정 요청
    좌석 -->> API: 좌석 예약 확정 성공 반환
    API -->> User: 결제 및 예약 성공 반환
```

