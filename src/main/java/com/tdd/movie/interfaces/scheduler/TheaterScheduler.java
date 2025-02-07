package com.tdd.movie.interfaces.scheduler;

import com.tdd.movie.application.TheaterFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class TheaterScheduler {

    TheaterFacade theaterFacade;

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void expireReservations() {
        theaterFacade.expireReservations();
    }
}
