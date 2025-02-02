# API SPEC

## 콘서트 예약 서비스 API 명세서

### 1. 지갑 충전 API

#### Request

- **URL**: `/api/v1/users/{userId}/wallets/{walletId}/charge`
- **Method**: `GET`
- **Headers**:
    - Content-Type: application/json
- **Path Parameters**:
    - `userId`: 사용자 ID (Integer)
    - `walletId`: 지갑 ID (Integer)
- **Query Parameters**:
    - `request`: 충전할 잔액 (Integer)

#### Response

- **Status Code**: 200 OK
  ```json
  {
    "response": {
      "id": 1,
      "userId": 1,
      "amount": 1000,
      "createdAt": "DateTime",
      "updatedAt": "DateTime"
    }
  }
  ```

### 2. 지갑 조회 API

#### Request

- **URL**: `/api/v1/users/{userId}/wallet`
- **Method**: `GET`
- **Headers**:
    - Content-Type: application/json
- **Path Parameters**:
    - `userId`: 사용자 ID (Integer)

#### Response

- **Status Code**: 200 OK
  ```json
  {
    "response": {
      "id": 1,
      "userId": 1,
      "amount": 1000,
      "createdAt": "DateTime",
      "updatedAt": "DateTime"
    }
  }
  ```
