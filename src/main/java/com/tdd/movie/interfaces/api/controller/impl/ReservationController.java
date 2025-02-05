package com.tdd.movie.interfaces.api.controller.impl;

import com.tdd.movie.application.TheaterFacade;
import com.tdd.movie.interfaces.api.controller.IReservationController;
import com.tdd.movie.interfaces.api.dto.ReservationControllerDto;
import com.tdd.movie.interfaces.api.dto.UserControllerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.tdd.movie.interfaces.api.CommonHttpHeader.X_USER_ID;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController implements IReservationController {

    private final TheaterFacade theaterFacade;

    @Override
    @PostMapping("/{reservationId}/payment")
    public ResponseEntity<ReservationControllerDto.PayReservationResponse> payReservation(
            @PathVariable Long reservationId,
            @RequestHeader(X_USER_ID) Long userId
    ) {
        theaterFacade.processPayment(reservationId, userId);
        return null;
    }

    @Override
    @GetMapping("/{reservationId}")
    public ResponseEntity<UserControllerDto.GetWalletResponse> getReservation(
            @PathVariable Long reservationId,
            @RequestHeader(X_USER_ID) Long userId
    ) {
        return null;
    }
}
