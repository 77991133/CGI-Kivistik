package ee.kivistik.backend.controller;

import ee.kivistik.backend.repository.ReservationRepository;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReservationController {
    private final ReservationRepository reservationRepository;

    public ReservationController(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }
}
