package com.tdd.movie.interfaces.api.controller.impl;

import com.tdd.movie.interfaces.api.dto.TheaterControllerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/theaters")
@RequiredArgsConstructor
public class TheaterScheduleController {

    @RequestMapping("/{theaterId}/movies/{movieId}/available-schedules")
    public ResponseEntity<TheaterControllerDto.GetAvailableSchedulesResponse> getAvailableSchedules(Long movieId) {
        return null;
    }

}
