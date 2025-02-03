# 도메인 아키텍쳐 정리

## 도메인 개념 설명

### 1. Movie (영화)

- 영화의 기본 정보를 관리하는 도메인.
- 영화 제목, 장르, 상영 시간, 개봉일 등의 정보를 포함함.

### 2. Theater (영화관)

- 영화가 상영되는 극장.
- 극장명, 위치 정보 등을 포함하며, 하나의 극장은 여러 개의 스크린을 가질 수 있음.

### 3. Schedule (상영 일정)

- 특정 극장에서 특정 영화가 언제 상영되는지를 관리하는 도메인.
- 극장(Theater)과 영화(Movie)를 연결하는 역할.
- 상영 시간이 포함되며, 좌석 예약 가능 여부를 결정함.

### 4. Seat (좌석)

- 상영관(Screen) 내 개별 좌석을 나타냄.
- 특정 상영 일정(Schedule)과 연결되어, 좌석 예약이 가능함.

### 5. Reservation (예매)

- 특정 사용자가 특정 영화의 특정 좌석을 예약하는 도메인.
- 결제와 연결되며, 좌석 예약 상태를 관리함.

## 도메인 관계 흐름

```
Movie (영화)
  └──> Theater (극장)
        └──> Schedule (상영 일정)
              └──> Seat (좌석)
                    └──> Reservation (예매)
```

## API 설계

### 1. 현재 상영중인 영화 목록 조회

```
GET /api/v1/movies/now-showing
```

### 2. 상영 예정 영화 목록 조회

```
GET /api/v1/movies/coming-soon
```

### 3. 사용자가 특정 영화의 상영 극장 조회

```
GET /api/v1/movies/{movieId}/theaters
```

### 4. 극장에서 해당 영화의 상영 일정 조회

```
GET /api/v1/theaters/{theaterId}/movies/{movieId}/available-schedules
```

### 5. 선택한 상영 일정의 예약 가능한 좌석 조회

```
GET /api/v1/theater-schedules/{scheduleId}/available-seats
```

### 6. 좌석 선택 후 예매 진행

```
POST /api/v1/theater-seats/{theaterSeatId}/reservations
```