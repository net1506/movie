# 도메인 아키텍처 정리

## 도메인 개념 설명

### 1. Movie (영화)

- 영화의 기본 정보를 관리하는 도메인.
- 영화 제목, 줄거리, 포스터 이미지, 상영 시간, 상영 시작 및 종료일 등의 정보를 포함.

### 2. Theater (영화관)

- 영화가 상영되는 극장.
- 극장명, 주소, 이미지, 위치 좌표(x, y) 등의 정보를 포함하며, 하나의 극장은 여러 개의 상영관(Screen)을 가질 수 있음.

### 3. TheaterScreen (상영관)

- 특정 영화관 내에서 영화를 상영하는 상영관.
- 상영관 번호, 좌석 수 등의 정보를 포함.

### 4. TheaterSchedule (상영 일정)

- 특정 극장에서 특정 영화가 언제 상영되는지를 관리하는 도메인.
- 상영 일자 및 시간, 예약 시작 및 종료 시간 정보를 포함.

### 5. TheaterSeat (좌석)

- 상영관 내 개별 좌석을 나타내며, 상영 일정과 연결됨.
- 좌석 번호, 가격, 예약 여부 등의 정보를 포함.

### 6. Reservation (예매)

- 특정 사용자가 특정 영화의 특정 좌석을 예약하는 도메인.
- 예약 상태(대기, 확정, 취소)와 예약 시간을 관리.

### 7. Wallet (지갑)

- 사용자의 결제 수단 및 잔액을 관리.
- 잔액 충전 및 차감 기능을 제공.

---

## 도메인 관계 흐름

```
User (사용자)
  └──> Wallet (지갑)
Movie (영화)
  └──> Theater (영화관)
        └──> TheaterScreen (상영관)
              └──> TheaterSchedule (상영 일정)
                    └──> TheaterSeat (좌석)
                          └──> Reservation (예매)
```

---

## API 설계

### 1. 현재 상영 중인 영화 목록 조회

```
GET /api/v1/movies/now-showing
```

### 2. 상영 예정 영화 목록 조회

```
GET /api/v1/movies/coming-soon
```

### 3. 특정 영화의 상영 극장 조회

```
GET /api/v1/movies/{movieId}/available-theaters
```

### 4. 극장에서 특정 영화의 상영 일정 조회

```
GET /api/v1/theaters/{theaterId}/movies/{movieId}/available-schedules
```

### 5. 선택한 상영 일정의 예약 가능한 좌석 조회

```
GET /api/v1/theater-schedules/{theaterScheduleId}/available-seats
```

### 6. 좌석 선택 후 예매 진행

```
POST /api/v1/theater-seats/{theaterSeatId}/reservation?userId={userId}
```

### 7. 예매 내역 결제 진행

```
POST /api/v1/reservations/{reservationId}/payment?userId={userId}
```

### 8. 사용자의 지갑 정보 조회

```
GET /api/v1/users/{userId}/wallet
```

### 9. 사용자의 지갑 금액 충전

```
PUT /api/v1/users/{userId}/wallets/{walletId}/charge
```

---
