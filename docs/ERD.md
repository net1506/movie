# ERD

## 영화 예매 시스템 ERD

모든 Entity 에는 생성 시간과 수정 시간을 관리하기 위한 공통 필드가 존재합니다.
Entity 의 생성 시간과 수정 시간이 중요하지 않은 경우에는 ERD 에선 생략하였습니다.

```mermaid
---
titld: "영화 예매 시스템 ERD"
---

erDiagram
    user {
        bigint id PK "AUTO_INCREMENT"
        varchar name
        datetime created_at
        datetime updated_at
    }

    wallet {
        bigint id PK "AUTO_INCREMENT"
        bigint user_id FK, UK
        int amount
        datetime created_at
        datetime updated_at
    }

    user ||--|| wallet: "1:1"
```