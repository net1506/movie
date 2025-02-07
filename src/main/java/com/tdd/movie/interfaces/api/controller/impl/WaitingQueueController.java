package com.tdd.movie.interfaces.api.controller.impl;

import com.tdd.movie.application.WaitingQueueFacade;
import com.tdd.movie.interfaces.api.controller.IWaitingQueueController;
import com.tdd.movie.interfaces.api.dto.WaitingQueueControllerDto.CreateWaitingQueueTokenResponse;
import com.tdd.movie.interfaces.api.dto.WaitingQueueControllerDto.GetWaitingQueuePositionResponse;
import com.tdd.movie.interfaces.api.dto.WaitingQueueControllerDto.WaitingQueueResponse;
import com.tdd.movie.interfaces.api.dto.WaitingQueueControllerDto.WaitingQueueResponseWithPosition;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/waiting-queues")
@RequiredArgsConstructor
public class WaitingQueueController implements IWaitingQueueController {

    private final WaitingQueueFacade waitingQueueFacade;

    @Override
    @PostMapping("/tokens")
    public ResponseEntity<CreateWaitingQueueTokenResponse> createWaitingQueueToken() {
        WaitingQueueResponse waitingQueue = new WaitingQueueResponse(
                waitingQueueFacade.createWaitingQueueToken()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CreateWaitingQueueTokenResponse(waitingQueue));
    }

    @Override
    @GetMapping("/position")
    public ResponseEntity<GetWaitingQueuePositionResponse> getWaitingQueuePosition(String waitingQueueTokenUuid) {
        WaitingQueueResponseWithPosition waitingQueue = new WaitingQueueResponseWithPosition(
                waitingQueueFacade.getWaitingQueueWithPosition(waitingQueueTokenUuid));

        return ResponseEntity.ok(new GetWaitingQueuePositionResponse(waitingQueue));
    }
}
