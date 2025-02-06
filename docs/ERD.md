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

    movie {
        bigint id PK "AUTO_INCREMENT"
        varchar title
        varchar plot
        varchar poster_image_url
        int running_time
        date screening_start_date
        date screening_end_date
        datetime created_at
        datetime updated_at
    }

    theater {
        bigint id PK "AUTO_INCREMENT"
        varchar name
        varchar address
        varchar img
        varchar x
        varchar y
        datetime created_at
        datetime updated_at
    }

    theater_screen {
        bigint id PK "AUTO_INCREMENT"
        int number "상영관 번호"
        bigint theater_id FK
        bigint seat_count
        datetime created_at
        datetime updated_at
    }

    theater_schedule {
        bigint id PK "AUTO_INCREMENT"
        bigint movie_id FK
        bigint theater_id FK
        bigint theater_screen_id FK
        datetime movie_at
        datetime reservation_start_at
        datetime reservation_end_at
        datetime created_at
        datetime updated_at
    }

    theater_seat {
        bigint id PK "AUTO_INCREMENT"
        bigint theater_schedule_id FK
        int number
        int price
        boolean is_reserved
        bigint version
        datetime created_at
        datetime updated_at
    }

    reservation {
        bigint id PK "AUTO_INCREMENT"
        bigint theater_seat_id FK
        bigint user_id FK
        enum status "WAITING, CONFIRMED, CANCELED"
        datetime reserved_at
        datetime created_at
        datetime updated_at
    }

    user ||--|| wallet: "1:1"
    movie ||--o{ theater_schedule: "1:N"
    theater ||--o{ theater_screen: "1:N"
    theater ||--o{ theater_schedule: "1:N"
    theater_screen ||--o{ theater_schedule: "1:N"
    theater_schedule ||--o{ theater_seat: "1:N"
    theater_seat ||--o{ reservation: "1:N"
    user ||--o{ reservation: "1:N"
```