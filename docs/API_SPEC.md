# API SPEC

## 콘서트 예약 서비스 API 명세서

### 1. 영화 조회 API

#### Request

- **URL**: `/api/v1/movies/{movieId}`
- **Method**: `GET`
- **Headers**:
    - Content-Type: application/json;charset=UTF-8
- **Path Parameters**:
    - `movieId`: 영화 ID (Integer)

#### Response

- **Status Code**: 200 OK
  ```json
  {
    "movie": {
      "id": 1,
      "title": "드래곤볼",
      "plot": "드래곤볼 줄거리",
      "posterImageUrl": "/images/backdrop/dragonball-image.png",
      "runningTime": 107,
      "screeningStartDate": "2024-10-09",
      "screeningEndDate": "2024-10-29"
    }
  }
  ```

### 2. 상영 가능한 영화관 조회 API

#### Request

- **URL**: `/api/v1/movies/{movieId}/available-theaters`
- **Method**: `GET`
- **Headers**:
    - Content-Type: application/json;charset=UTF-8
- **Query Parameters**:
    - `movieId`: 영화 ID (Integer)

#### Response

- **Status Code**: 200 OK
  ```json
  {
    "theaters": [
      {
        "id": 1,
        "name": "CGV 강남",
        "address": "서울특별시 강남구 강남대로 102길 23",
        "img": "/images/theaters/cgv-gangnam.png",
        "x": "37.498095",
        "y": "127.02761"
      }
    ]
  }
  ```

### 3. 상영 예정 영화 조회 API

#### Request

- **URL**: `/api/v1/movies/coming-soon`
- **Method**: `GET`
- **Headers**:
    - Content-Type: application/json;charset=UTF-8

#### Response

- **Status Code**: 200 OK
  ```json
  {
    "movies": [
      {
        "id": 1,
        "title": "드래곤볼",
        "plot": "드래곤볼 줄거리",
        "posterImageUrl": "/images/backdrop/dragonball-image.png",
        "runningTime": 107,
        "screeningStartDate": "2024-10-09",
        "screeningEndDate": "2024-10-29"
      }
    ]
  }
  ```

### 4. 상영 중인 영화 조회 API

#### Request

- **URL**: `/api/v1/movies/now-showing`
- **Method**: `GET`
- **Headers**:
    - Content-Type: application/json;charset=UTF-8

#### Response

- **Status Code**: 200 OK
  ```json
  {
    "movies": [
      {
        "id": 1,
        "title": "드래곤볼",
        "plot": "드래곤볼 줄거리",
        "posterImageUrl": "/images/backdrop/dragonball-image.png",
        "runningTime": 107,
        "screeningStartDate": "2024-10-09",
        "screeningEndDate": "2024-10-29"
      }
    ]
  }
  ```

### 5. 영화관 예매 내역 조회 API

#### Request

- **URL**: `/api/v1/reservations/{reservationId}`
- **Method**: `GET`
- **Headers**:
    - Content-Type: application/json;charset=UTF-8
    - X-User-Id: [사용자 ID]
- **Path Parameters**:
    - `reservationId`: 영화관 예매 내역 ID (String)

#### Response

- **Status Code**: 200 OK
  ```json
  {
    "reservation": {
        "id": 1,
        "userId": 1,
        "status": "CONFIRMED",
        "reservedAt": "DateTime",
        "createdAt": "DateTime",
        "updatedAt": "DateTime"
    }
  }
  ```

### 6. 영화관 예매 내역 결제 API

#### Request

- **URL**: `/api/v1/reservations/{reservationId}/payment`
- **Method**: `POST`
- **Headers**:
    - Content-Type: application/json;charset=UTF-8
    - X-User-Id: [사용자 ID]
- **Path Parameters**:
    - `reservationId`: 영화관 예매 내역 ID (String)

#### Response

- **Status Code**: 200 OK
  ```json
  {
    "paymentResponse": {
        "id": 1,
        "reservationId": 1,
        "userId": 1,
        "price": 1000
    }
  }
  ```

### 7. 예매 가능한 영화관 일정 조회 API

#### Request

- **URL**: `/api/v1/theaters/{theaterId}/movies/{movieId}/available-schedules`
- **Method**: `GET`
- **Headers**:
    - Content-Type: application/json;charset=UTF-8
- **Path Parameters**:
    - `theaterId`: 영화관 ID (String)
    - `movieId`: 영화 ID (String)

#### Response

- **Status Code**: 200 OK
  ```json
  {
    "theaterSchedules": [
        {
            "id": 1,
            "movieId": 10,
            "theaterId": 5,
            "theaterScreenId": 2,
            "movieAt": "DateTime",
            "reservationStartAt": "DateTime",
            "reservationEndAt": "DateTime"
        }
    ]
  }
  ```

### 8. 예매 가능한 좌석 조회 API

#### Request

- **URL**: `/api/v1/theater-schedules/{theaterScheduleId}/available-seats`
- **Method**: `GET`
- **Headers**:
    - Content-Type: application/json;charset=UTF-8
- **Path Parameters**:
    - `theaterScheduleId`: 상영 일정 ID (Integer)

#### Response

- **Status Code**: 200 OK
  ```json
  {
    "theaterSchedules": [
        {
            "id": 10,
            "theaterScheduleId": 1,
            "number": 2,
            "price": 15000,
            "isReserved": true
        }
    ]
  }
  ```

### 9. 지갑 충전 API

#### Request

- **URL**: `/api/v1/users/{userId}/wallets/{walletId}/charge`
- **Method**: `PUT`
- **Headers**:
    - Content-Type: application/json;charset=UTF-8
- **Path Parameters**:
    - `userId`: 사용자 ID (String)
    - `walletId`: 지갑 ID (String)
- **Body**:
  ```json
  {
      "amount": 100000
  }
  ```

#### Response

- **Status Code**: 200 OK
  ```json
  {
    "response": {
      "id": 1,
      "userId": 1,
      "amount": 100000,
      "createdAt": "DateTime",
      "updatedAt": "DateTime"
    }
  }
  ```

### 10. 영화관 좌석 예매 API

#### Request

- **URL**: `/api/v1/theater-seats/{theaterSeatId}/reservation`
- **Method**: `POST`
- **Headers**:
    - Content-Type: application/json;charset=UTF-8
    - X-User-Id: [사용자 ID]
- **Path Parameters**:
    - `theaterSeatId`: 영화관 좌석 ID (String)

#### Response

- **Status Code**: 200 OK
  ```json
  {
    "reservation": {
        "id": 1,
        "userId": 1,
        "status": "CONFIRMED",
        "reservedAt": "DateTime",
        "createdAt": "DateTime",
        "updatedAt": "DateTime"
    }
  }
  ```

### 11. 영화관 예매 내역 결제 API

#### Request

- **URL**: `/api/v1/reservations/{reservationId}/payment`
- **Method**: `POST`
- **Headers**:
    - Content-Type: application/json;charset=UTF-8
    - X-User-Id: [사용자 ID]
- **Path Parameters**:
    - `reservationId`: 영화관 예매 내역 ID (String)

#### Response

- **Status Code**: 200 OK
  ```json
  {
    "paymentResponse": {
        "id": 1,
        "reservationId": 1,
        "userId": 1,
        "price": 1000
    }
  }
  ```

### 12. 예매 가능한 영화관 일정 조회 API

#### Request

- **URL**: `/api/v1/theaters/{theaterId}/movies/{movieId}/available-schedules`
- **Method**: `GET`
- **Headers**:
    - Content-Type: application/json;charset=UTF-8
- **Path Parameters**:
    - `theaterId`: 영화관 ID (String)
    - `movieId`: 영화 ID (String)

#### Response

- **Status Code**: 200 OK
  ```json
  {
    "theaterSchedules": [
        {
            "id": 1,
            "movieId": 10,
            "theaterId": 5,
            "theaterScreenId": 2,
            "movieAt": "DateTime",
            "reservationStartAt": "DateTime",
            "reservationEndAt": "DateTime"
        }
    ]
  }
  ```

### 13. 예매 가능한 좌석 조회 API

#### Request

- **URL**: `/api/v1/theater-schedules/{theaterScheduleId}/available-seats`
- **Method**: `GET`
- **Headers**:
    - Content-Type: application/json;charset=UTF-8
- **Path Parameters**:
    - `theaterScheduleId`: 상영 일정 ID (Integer)

#### Response

- **Status Code**: 200 OK
  ```json
  {
    "theaterSchedules": [
        {
            "id": 10,
            "theaterScheduleId": 1,
            "number": 2,
            "price": 15000,
            "isReserved": true
        }
    ]
  }
  ```

