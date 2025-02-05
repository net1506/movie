package com.tdd.movie.interfaces.api.controller.impl;

import com.tdd.movie.application.TheaterFacade;
import com.tdd.movie.interfaces.api.controller.ITheaterSeatController;
import com.tdd.movie.interfaces.api.dto.TheaterSeatControllerDto.ReservationResponse;
import com.tdd.movie.interfaces.api.dto.TheaterSeatControllerDto.ReserveSeatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/theater-seats")
@RequiredArgsConstructor
public class TheaterSeatController implements ITheaterSeatController {

    private final TheaterFacade theaterFacade;

    @Override
    @PostMapping("/{theaterSeatId}/reservation")
    public ResponseEntity<ReserveSeatResponse> reserveSeat(
            @PathVariable Long theaterSeatId,
            @RequestParam(name = "userId") Long userId
    ) {
        ReservationResponse response = new ReservationResponse(theaterFacade.processReservation(userId, theaterSeatId));
        return ResponseEntity.ok(new ReserveSeatResponse(response));
    }
}
