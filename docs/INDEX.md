## 인덱스 최적화 설명

### 1. **`Movie` 엔티티**

```java

@Entity
@Table(name = "movies", indexes = {
        @Index(name = "idx_screening_period", columnList = "screeningStartDate, screeningEndDate")
})
public class Movie extends BaseEntity {
    private String title;
    private String plot;
    private String posterImageUrl;
    private Integer runningTime;
    private LocalDate screeningStartDate;
    private LocalDate screeningEndDate;
}
```

**설명:**

- `screeningStartDate`와 `screeningEndDate`의 복합 인덱스를 통해 현재 상영 중인 영화나 상영 예정인 영화를 빠르게 조회할 수 있다.

**관련 쿼리:**

```java

@Query("SELECT m FROM Movie m WHERE m.screeningStartDate <= :date AND m.screeningEndDate >= :date")
List<Movie> findAllByScreeningPeriod(LocalDate date);

@Query("SELECT m FROM Movie m WHERE m.screeningStartDate > :date")
List<Movie> findAllByScreeningStartDateAfter(LocalDate date);
```

```sql
SELECT m
FROM Movie m
WHERE m.screeningStartDate <= :date
  AND m.screeningEndDate >= :date;

SELECT m
FROM Movie m
WHERE m.screeningStartDate > :date;
```

- 이 쿼리는 현재 상영 중인 영화나 상영 예정인 영화를 조회하는데 사용되며, 인덱스를 통해 빠른 검색이 가능하다.

### 2. **`TheaterSeat` 엔티티**

```java

@Entity
@Table(name = "theater_seats", indexes = {
        @Index(name = "idx_schedule_reserved", columnList = "theaterScheduleId, isReserved")
})
public class TheaterSeat extends BaseEntity {
    private Long theaterScheduleId;
    private Integer number;
    private Integer price;
    private Boolean isReserved;
    @Version
    private Long version;
}
```

**설명:**

- `theaterScheduleId`와 `isReserved`의 복합 인덱스를 통해 특정 상영 일정의 예약 가능한 좌석을 빠르게 조회할 수 있다.
- 예약 상태(`isReserved`)에 따라 필터링하는 쿼리 성능을 향상시킬 수 있다.

**관련 쿼리:**

```java

@Query("SELECT ts FROM TheaterSeat ts WHERE ts.theaterScheduleId = :theaterScheduleId AND ts.isReserved = :isReserved")
List<TheaterSeat> findAllByTheaterScheduleIdAndIsReserved(Long theaterScheduleId, Boolean isReserved);
```

```sql
SELECT ts
FROM TheaterSeat ts
WHERE ts.theaterScheduleId = :theaterScheduleId
  AND ts.isReserved = false;
```

- 이 쿼리는 특정 상영 일정의 예약 가능한 좌석만 조회하며, 인덱스를 통해 빠른 검색이 가능하다.

### 3. **`TheaterSchedule` 엔티티**

```java

@Entity
@Table(name = "theater_schedules", indexes = {
        @Index(name = "idx_movie_theater", columnList = "movieId, theaterId"),
        @Index(name = "idx_reservation_period", columnList = "reservationStartAt, reservationEndAt")
})
public class TheaterSchedule extends BaseEntity {
    private Long movieId;
    private Long theaterId;
    private Long theaterScreenId;
    private LocalDateTime movieAt;
    private LocalDateTime reservationStartAt;
    private LocalDateTime reservationEndAt;
}
```

**설명:**

- `movieId`와 `theaterId`의 복합 인덱스를 통해 특정 영화가 상영 중인 극장을 빠르게 조회할 수 있다.
- `reservationStartAt`과 `reservationEndAt`의 복합 인덱스로 예약 가능한 기간을 기준으로 일정 검색 성능을 최적화할 수 있다.

**관련 쿼리:**

```java

@Query("SELECT DISTINCT ts.theaterId FROM TheaterSchedule ts WHERE ts.movieId = :movieId")
List<Long> findDistinctTheaterIdsByMovieId(Long movieId);

@Query("SELECT ts FROM TheaterSchedule ts WHERE ts.theaterId = :theaterId AND ts.movieId = :movieId AND ts.reservationStartAt <= :now AND ts.reservationEndAt >= :now")
List<TheaterSchedule> findByTheaterIdAndMovieIdAndReservationPeriod(Long theaterId, Long movieId, LocalDateTime now);
```

```sql
SELECT ts
FROM TheaterSchedule ts
WHERE ts.theaterId = :theaterId
  AND ts.movieId = :movieId
  AND ts.reservationStartAt <= :now
  AND ts.reservationEndAt >= :now;
```

- 이 쿼리는 특정 영화의 예약 가능한 상영 일정을 조회하며, 인덱스를 통해 예약 가능 기간 내 빠른 검색이 가능하다.

### 4. **`Reservation` 엔티티**

```java

@Entity
@Table(name = "reservations", indexes = {
        @Index(name = "idx_user_seat", columnList = "userId, theaterSeatId")
})
public class Reservation extends BaseEntity {
    private Long theaterSeatId;
    private Long userId;
    private ReservationStatus status;
    private LocalDateTime reservedAt;
}
```

**설명:**

- `userId`, `theaterSeatId`의 복합 인덱스를 통해 특정 사용자의 예약 내역을 빠르게 조회하고 상태별로 필터링할 수 있다.
- 결제 여부나 취소된 예약을 빠르게 확인하는 데 도움이 된다.
- status에 인덱스를 주지 않은 이유는 ENUM 타입으로 'WAITING', 'CONFIRMED', 'CANCELED' 같은 소수의 값만을 가진다. <br>
  이 경우 옵티마이저는 인덱스를 만들어도 인덱스를 사용하는 것보다 전체 테이블 스캔(Full Table Scan)이 더 효율적이라고 판단할 수 있다. <br>
  또한 status 의 경우 예약, 예약 취소함에 따라 값이 자주 변경되는 항목이다. 자주 변경되는 컬럼에 인덱스를 걸면 이에 따른 인덱스 생성 비용이 추가적으로 발생할 수 있다.
  **관련 쿼리:**

```sql
SELECT r
FROM Reservation r
WHERE r.userId = :userId
  AND r.theaterSeatId = :theaterSeatId
  AND r.status = 'CONFIRMED';
```

- 이 쿼리는 특정 사용자가 예약한 좌석의 결제 상태를 조회하며, 복합 인덱스를 통해 빠른 필터링이 가능하다.

---
