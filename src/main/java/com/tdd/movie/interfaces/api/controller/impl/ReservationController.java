package com.tdd.movie.interfaces.api.controller.impl;

import com.tdd.movie.application.TheaterFacade;
import com.tdd.movie.interfaces.api.CommonHttpHeader;
import com.tdd.movie.interfaces.api.controller.IReservationController;
import com.tdd.movie.interfaces.api.dto.ReservationControllerDto.GetReservationResponse;
import com.tdd.movie.interfaces.api.dto.ReservationControllerDto.PayReservationResponse;
import com.tdd.movie.interfaces.api.dto.ReservationControllerDto.PaymentResponse;
import com.tdd.movie.interfaces.api.dto.ReservationControllerDto.ReservationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController implements IReservationController {

    private final TheaterFacade theaterFacade;

    @Override
    @PostMapping("/{reservationId}/payment")
    public ResponseEntity<PayReservationResponse> payReservation(
            @PathVariable Long reservationId,
            @RequestHeader(CommonHttpHeader.X_USER_ID) Long userId
    ) {
        PaymentResponse payment = new PaymentResponse(theaterFacade.processPayment(reservationId, userId));
        return ResponseEntity.ok(new PayReservationResponse(payment));
    }

    @Override
    @GetMapping("/{reservationId}")
    public ResponseEntity<GetReservationResponse> getReservation(
            @PathVariable Long reservationId,
            @RequestHeader(CommonHttpHeader.X_USER_ID) Long userId
    ) {
        ReservationResponse reservation = new ReservationResponse(theaterFacade.getReservation(reservationId, userId));
        return ResponseEntity.ok(new GetReservationResponse(reservation));
    }
}
