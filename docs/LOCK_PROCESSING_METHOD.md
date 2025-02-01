# Pessimistic Locking (비관적 락)

```
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT s FROM Seat s WHERE s.id = :id")
Seat findSeatWithLock(Long id);
```

## 동작 방식

* 트랜잭션이 시작될 때 즉시 해당 좌석을 "잠금".
* 다른 트랜잭션이 같은 좌석을 수정하려고 하면 대기(Blocking).
* 트랜잭션이 끝날 때까지 다른 사용자는 이 좌석을 수정할 수 없음.

## 문제가 발생할 경우

* 사용자가 좌석을 선택했지만 결제를 안 하고 오래 기다리면?<br/>
  다른 사용자는 좌석을 예약하려고 해도 락이 걸려 있어서 기다려야 함.<br/>
  트랜잭션이 오래 지속되면 성능 저하 발생.
* Deadlock(교착 상태) 발생 가능<br/>
  여러 트랜잭션이 서로 다른 자원을 잠그고 기다리면 데드락(Deadlock)이 발생하여 시스템이 멈출 수 있음.<br/>
  예를 들어, 사용자가 A 좌석을 잠그고 B 좌석도 잠그려고 하고,
  다른 사용자가 B 좌석을 잠그고 A 좌석도 잠그려고 하면 양쪽 모두 영원히 대기.
* 트랜잭션이 중단되면 락이 풀리기 전까지 다른 사용자가 기다려야 함<br/>
  `네트워크 장애, 애플리케이션 오류 등으로 트랜잭션이 비정상 종료되면 데이터베이스에 락`이 걸려서 다른 사용자가 예약을 못 할 수 있음.

# Optimistic Locking (낙관적 락)

```
@Entity
public class Seat {
    @Version
    private Long version;
}
```

## 동작 방식

* 락을 사용하지 않음.
* 좌석을 가져올 때 버전(Version) 필드를 함께 가져옴.
* 트랜잭션이 완료될 때 버전이 변경되었는지 확인 후 다른 트랜잭션이 동시에 변경했으면 예외 발생.

## 문제가 발생할 경우

* 동시에 여러 사용자가 같은 좌석을 예약하면?<br/>
  마지막에 저장하는 사용자는 OptimisticLockException 이 발생하여 실패.
  예를 들어, 5명이 같은 좌석을 예약하면 가장 마지막에 예약을 시도한 사용자는 실패.
* 낙관적 락 충돌이 많아지면 성능 저하 가능
  트래픽이 높은 경우 계속해서 예외가 발생하여 여러 번 재시도 필요.
  좌석 예약처럼 실시간 처리가 필요한 경우 적절한 대기 전략이 필요.
* 충돌이 발생했을 때 재시도 로직이 필요함

```
@Transactional
public void reserveSeat(Long seatId) {
    for (int i = 0; i < 3; i++) { // 최대 3번 재시도
        try {
            Seat seat = seatRepository.findById(seatId).orElseThrow();
            seat.reserve();
            seatRepository.save(seat);
            return;
        } catch (OptimisticLockException e) {
            log.warn("Optimistic Lock 충돌, 재시도 중...");
        }
    }
    throw new SeatReservationException("좌석 예약 실패");
}
```

* 낙관적 락은 충돌이 발생하면 예외가 발생하므로, 재시도 로직을 구현해야 함.
* 충돌이 잦으면 예약 성공률이 낮아질 수 있음.

## 예시로 설명

### A, B, C가 같은 좌석을 예약하는 경우 (Optimistic Locking 적용)

#### A, B, C가 같은 좌석을 조회

A, B, C가 동시에 좌석 ID가 1인 좌석(Seat)을 조회했다고 가정하자.
이때, 각 사용자는 DB에서 좌석 정보를 가져오면서 version 필드도 함께 가져옴.

| 사용자 | 조회한 Seat 데이터 (version=1)    |
|-----|-----------------------------|
| A   | `{ seatId: 1, version: 1 }` |
| B   | `{ seatId: 1, version: 1 }` |
| C   | `{ seatId: 1, version: 1 }` |

#### A가 먼저 좌석을 예약하고 저장

* A가 좌석을 예약한 후, DB에 저장.
* 저장 시, @Version 필드가 1에서 2로 증가.
* 트랜잭션이 정상적으로 커밋됨.

| 사용자 | 변경 후 Seat 데이터 (version 변경)            |
|-----|---------------------------------------|
| A   | `{ seatId: 1, version: 2 }` ✅ 성공!     |
| B   | `{ seatId: 1, version: 1 }` (아직 변경 X) |
| C   | `{ seatId: 1, version: 1 }` (아직 변경 X) |

#### B가 예약을 시도하지만 충돌 발생

* B는 자신의 version=1을 가지고 있지만, DB에서는 version이 2로 증가한 상태.
* 따라서 OptimisticLockException 예외 발생.
* B는 재시도 로직을 수행하여 다시 DB 에서 최신 데이터를 가져와야 함.

| 사용자 | 예약 시도 결과                     |
|-----|------------------------------|
| B   | ❌ OptimisticLockException 발생 |

#### B가 재시도 후 성공

* B가 예외 발생 후, 다시 좌석을 조회 (이제 version=2).
* B가 예약을 성공적으로 수행하고 DB에 저장 (version=3으로 증가).

| 사용자 | 변경 후 Seat 데이터 (version 변경)            |
|-----|---------------------------------------|
| B   | `{ seatId: 1, version: 3 }` ✅ 성공!     |
| C   | `{ seatId: 1, version: 1 }` (아직 변경 X) |

#### C가 예약을 시도하지만 충돌 발생

* C는 여전히 version=1인 데이터를 가지고 있음.
* 하지만 DB 에서는 이미 version=3이 되었으므로 충돌 발생.
* C는 재시도 로직을 수행해야 함.

| 사용자 | 예약 시도 결과                     |
|-----|------------------------------|
| C   | ❌ OptimisticLockException 발생 |

#### C가 재시도

* C가 다시 DB에서 최신 데이터를 조회 (version=3).
* C가 예약 성공 후, version=4로 증가.

| 사용자 | 최종 Seat 데이터 (version=4)           |
|-----|-----------------------------------|
| C   | `{ seatId: 1, version: 4 }` ✅ 성공! |

# Redis 분산 락 (Redisson)

# 메시지 큐 기반 예약 (RabbitMQ, Kafka)

# 최종 정리

| 방식                  | 문제 발생 시 어떻게 되는가?                           | 해결 방법                            |
|---------------------|--------------------------------------------|----------------------------------|
| Pessimistic Locking | 예약을 오래 안 하면 다른 사용자가 대기해야 함, Deadlock 발생 가능 | 트랜잭션 시간을 최소화하고, 타임아웃 설정          |
| Optimistic Locking  | 충돌이 많아지면 성능 저하, 예외 발생                      | 재시도 로직 구현                        |
| Redis 분산 락          | Redis 장애 발생 시 예약 불가, 락이 풀리지 않으면 문제 발생      | TTL 설정, Fallback 로직 구현           |
| 메시지 큐 예약            | 예약 요청이 몰리면 지연 발생, 메시지 유실 가능                | 컨슈머(Consumer) 확장, Durable 메시지 설정 |


