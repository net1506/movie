package com.tdd.movie.interfaces.api.controller.impl;

import com.tdd.movie.interfaces.api.dto.TheaterControllerDto.GetAvailableSchedulesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/theater-schedules")
@RequiredArgsConstructor
public class TheaterScheduleController {

    @RequestMapping("/{theaterId}/movies/{movieId}/available-schedules")
    public ResponseEntity<GetAvailableSchedulesResponse> getAvailableSchedules(@PathVariable Long theaterId, @PathVariable Long movieId) {
        return null;
    }

}
