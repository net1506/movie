package com.tdd.movie.interfaces.api.controller.impl;

import com.tdd.movie.application.TheaterFacade;
import com.tdd.movie.domain.theater.domain.TheaterSeat;
import com.tdd.movie.interfaces.api.dto.TheaterScheduleControllerDto.GetAvailableSeatsResponse;
import com.tdd.movie.interfaces.api.dto.TheaterScheduleControllerDto.TheaterSeatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/theater-schedules")
@RequiredArgsConstructor
public class TheaterScheduleController {

    private final TheaterFacade theaterFacade;

    @RequestMapping("/{theaterScheduleId}/available-seats")
    public ResponseEntity<GetAvailableSeatsResponse> getAvailableSeats(@PathVariable Long theaterScheduleId) {
        List<TheaterSeat> reservableTheaterSeats = theaterFacade.getReservableTheaterSeats(theaterScheduleId);
        List<TheaterSeatResponse> responseList = reservableTheaterSeats.stream().map(TheaterSeatResponse::new).toList();

        return ResponseEntity.ok(new GetAvailableSeatsResponse(responseList));
    }

}
