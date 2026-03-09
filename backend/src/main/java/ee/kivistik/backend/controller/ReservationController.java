package ee.kivistik.backend.controller;

import ee.kivistik.backend.dto.Reservation;
import ee.kivistik.backend.dto.ReservationRequest;
import ee.kivistik.backend.entity.ReservationEntity;
import ee.kivistik.backend.entity.TableEntity;
import ee.kivistik.backend.repository.ReservationRepository;
import ee.kivistik.backend.repository.TableRepository;
import ee.kivistik.backend.service.ReservationService;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class ReservationController {
    private final ReservationRepository reservationRepository;
    private final ReservationService reservationService;
    private final TableRepository tableRepository;

    public ReservationController(ReservationRepository reservationRepository, ReservationService reservationService, TableRepository tableRepository) {
        this.reservationRepository = reservationRepository;
        this.tableRepository = tableRepository;
        this.reservationService = reservationService;
    }

    @GetMapping("/bookings")
    public Iterable<ReservationEntity> findAllTables() {
        return this.reservationRepository.findAll();
    }

    @PostMapping("/bookings")
    public ReservationEntity addReservation(@RequestBody Reservation booking) {
        System.out.println("id " + booking.tableId+ booking.customerName+ booking.time+ booking.seats);
        if (booking.tableId == null) {
            throw new IllegalArgumentException("Table ID cannot be null");
        }
        TableEntity table = tableRepository.findById(booking.tableId)
                .orElseThrow(() -> new RuntimeException("Table not found"));

        ReservationEntity reservation = new ReservationEntity();
        reservation.setName(booking.customerName);
        reservation.setTable(table);
        reservation.setDate(booking.date);
        reservation.setTime(booking.time);
        reservation.setPeople(booking.seats);

        return reservationRepository.save(reservation);
    }

    @PostMapping("/available-tables")
    public List<TableEntity> getAvailableTables(@RequestBody ReservationRequest request) {
        return reservationService.findBestTables(request);
    }



}
