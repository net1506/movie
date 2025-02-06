package com.tdd.movie.interfaces.api.dto;

import com.tdd.movie.domain.waitingqueue.model.WaitingQueue;
import com.tdd.movie.domain.waitingqueue.model.WaitingQueueWithPosition;
import io.swagger.v3.oas.annotations.media.Schema;

public class WaitingQueueControllerDto {
    // 대기열을 생성한다.
    public record CreateWaitingQueueTokenResponse(
            WaitingQueueResponse waitingQueue
    ) {

    }

    // 대기열의 번호를 가져온다.
    public record GetWaitingQueuePositionResponse(
            WaitingQueueResponseWithPosition waitingQueue
    ) {

    }

    // 대기열 응답 DTO
    public record WaitingQueueResponse(
            @Schema(description = "대기열 UUID", example = "1")
            String uuid

    ) {
        public WaitingQueueResponse(WaitingQueue waitingQueueToken) {
            this(waitingQueueToken.getUuid());
        }
    }

    // 대기열 순번 응답 DTO
    public record WaitingQueueResponseWithPosition(

            @Schema(description = "대기열 UUID", example = "1")
            String uuid,

            @Schema(description = "대기열 순번", example = "1")
            Long position

    ) {

        public WaitingQueueResponseWithPosition(WaitingQueueWithPosition waitingQueueWithPosition) {
            this(
                    waitingQueueWithPosition.uuid(),
                    waitingQueueWithPosition.position()
            );
        }
    }

}
