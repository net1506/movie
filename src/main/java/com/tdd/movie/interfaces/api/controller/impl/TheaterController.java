package com.tdd.movie.interfaces.api.controller.impl;

import com.tdd.movie.application.TheaterFacade;
import com.tdd.movie.domain.theater.model.TheaterSchedule;
import com.tdd.movie.interfaces.api.controller.ITheaterController;
import com.tdd.movie.interfaces.api.dto.TheaterControllerDto.GetAvailableSchedulesResponse;
import com.tdd.movie.interfaces.api.dto.TheaterControllerDto.TheaterScheduleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/theaters")
@RequiredArgsConstructor
public class TheaterController implements ITheaterController {

    private final TheaterFacade theaterFacade;

    @Override
    @GetMapping("/{theaterId}/movies/{movieId}/available-schedules")
    public ResponseEntity<GetAvailableSchedulesResponse> getAvailableSchedules(
            @PathVariable Long theaterId,
            @PathVariable Long movieId
    ) {
        List<TheaterSchedule> reservableTheaterSchedules = theaterFacade.getReservableTheaterSchedules(theaterId, movieId);
        List<TheaterScheduleResponse> response = reservableTheaterSchedules.stream().map(TheaterScheduleResponse::new).toList();
        return ResponseEntity.ok(new GetAvailableSchedulesResponse(response));
    }

}
