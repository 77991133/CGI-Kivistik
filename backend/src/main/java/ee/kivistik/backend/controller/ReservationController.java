package ee.kivistik.backend.controller;

import ee.kivistik.backend.dto.Reservation;
import ee.kivistik.backend.dto.ReservationDTO;
import ee.kivistik.backend.dto.ReservationRequest;
import ee.kivistik.backend.dto.TableRecommendationResponse;
import ee.kivistik.backend.entity.ReservationEntity;
import ee.kivistik.backend.entity.TableEntity;
import ee.kivistik.backend.mapper.ReservationMapperUtil;
import ee.kivistik.backend.repository.ReservationRepository;
import ee.kivistik.backend.repository.TableRepository;
import ee.kivistik.backend.service.ReservationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *  REST controller that handles reservation related API endpoints.
 */
@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class ReservationController {
    private final ReservationRepository reservationRepository;
    private final ReservationService reservationService;
    private final TableRepository tableRepository;
    /**
     * Constructs a ReservationController with required dependencies.
     *
     * @param reservationRepository repository used to access reservation data
     * @param reservationService service responsible for table recommendation logic
     * @param tableRepository repository used to access table data
     */
    public ReservationController(ReservationRepository reservationRepository, ReservationService reservationService, TableRepository tableRepository) {
        this.reservationRepository = reservationRepository;
        this.tableRepository = tableRepository;
        this.reservationService = reservationService;
    }
    /**
     * Returns all reservations stored in the system.
     *
     * @return iterable collection of all ReservationDTO objects
     */
    @GetMapping("/bookings")
    public List<ReservationDTO> findAllReservations() {
        return reservationRepository.findAll()
                .stream()
                .map(ReservationMapperUtil::toDTO)
                .toList();
    }

    /**
     * Creates a new reservation
     * The request body must contain reservation details including the table id,
     * customer name, reservation date, time and number of seats.
     *
     * @param booking reservation request data received from the client
     * @return saved ReservationEntity object
     * @throws IllegalArgumentException if the table id is null
     * @throws RuntimeException if the specified table does not exist
     */
    @PostMapping("/bookings")
    public ReservationEntity addReservation(@RequestBody Reservation booking) {
        if (booking.getTableId() == null) {
            throw new IllegalArgumentException("Table ID cannot be null");
        }
        TableEntity table = tableRepository.findById(booking.getTableId())
                .orElseThrow(() -> new RuntimeException("Table not found"));

        ReservationEntity reservation = new ReservationEntity();
        reservation.setName(booking.getCustomerName());
        reservation.setTable(table);
        reservation.setDate(booking.getDate());
        reservation.setTime(booking.getTime());
        reservation.setPeople(booking.getSeats());

        return reservationRepository.save(reservation);
    }
    /**
     * Returns recommended tables for a reservation request.
     * @param request reservation request containing date, time and number of seats
     * @return TableRecommendationResponse containing recommended tables,
     *         whether tables were combined, and booked table ids
     */
    @PostMapping("/available-tables")
    public TableRecommendationResponse getAvailableTables(@RequestBody ReservationRequest request) {
        return reservationService.findBestTables(request);
    }



}
