package ee.kivistik.backend.service;

import ee.kivistik.backend.dto.ReservationRequest;
import ee.kivistik.backend.dto.TableRecommendationResponse;
import ee.kivistik.backend.entity.ReservationEntity;
import ee.kivistik.backend.entity.TableEntity;
import ee.kivistik.backend.repository.ReservationRepository;
import ee.kivistik.backend.repository.TableRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class responsible for handling restaurant table reservations.
 * <p>
 * Provides logic for recommending the best available tables based on
 * customer preferences, generating random test reservations, and
 * checking already booked tables.
 */
@Service
public class ReservationService {

    private final TableRepository tableRepository;
    private final ReservationRepository reservationRepository;

    public ReservationService(TableRepository tableRepository, ReservationRepository reservationRepository) {
        this.tableRepository = tableRepository;
        this.reservationRepository = reservationRepository;
    }

    /**
     * Finds the best available tables based on the reservation request.
     * <p>
     * If no fully suitable tables are available, it attempts to combine
     * neighboring tables to meet the requested number of seats.
     *
     * @param request the reservation request containing customer preferences
     * @return a TableRecommendationResponse containing recommended tables,
     *         a flag indicating if tables are combined, and IDs of already booked tables
     */
    public TableRecommendationResponse findBestTables(ReservationRequest request) {

        LocalTime startTime = request.getTime().minusHours(2);
        LocalTime endTime = request.getTime().plusHours(1);

        List<Integer> bookedTableIds = getBookedTables(request.getDate(), startTime, endTime);

        List<TableEntity> recommendedTables = tableRepository.findAll().stream()
                .filter(t -> !bookedTableIds.contains(t.getId()))
                .filter(t -> t.getSeats() >= request.getSeats())
                .sorted((a, b) -> score(b, request) - score(a, request))
                .collect(Collectors.toList());

        if (!recommendedTables.isEmpty()) {
            TableRecommendationResponse response = new TableRecommendationResponse();
            response.setTables(recommendedTables);
            response.setCombined(false);
            response.setBookedTableIds(bookedTableIds);
            return response;
        } else {
            // Attempt to combine neighbouring tables
            List<TableEntity> freeTables = tableRepository.findAll().stream()
                    .filter(t -> !bookedTableIds.contains(t.getId())).toList();

            for (TableEntity table : freeTables) {
                int seats = table.getSeats();
                List<Integer> neighbours = table.getNeighbours();
                List<TableEntity> currentTables = new ArrayList<>();
                if (!neighbours.isEmpty()) {
                    for (int i = 0; i < neighbours.size(); i++) {
                        currentTables.add(table);
                        Optional<TableEntity> neighbour = tableRepository.findById(neighbours.get(i));
                        if (neighbour.isPresent() && !bookedTableIds.contains(neighbours.get(i))) {
                            TableEntity currentNeighbour = neighbour.get();
                            if (seats + currentNeighbour.getSeats() >= request.getSeats()) {
                                currentTables.add(currentNeighbour);
                                TableRecommendationResponse response = new TableRecommendationResponse();
                                response.setTables(currentTables);
                                response.setCombined(true);
                                response.setBookedTableIds(bookedTableIds);
                                return response;
                            } else {
                                seats += currentNeighbour.getSeats();
                                currentTables.add(currentNeighbour);
                                for (Integer neighbourId : currentNeighbour.getNeighbours()) {
                                    if ((!Objects.equals(table.getId(), neighbourId)) &&
                                            !neighbours.contains(neighbourId) &&
                                            !bookedTableIds.contains(neighbourId)) {
                                        neighbours.add(neighbourId);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        TableRecommendationResponse response = new TableRecommendationResponse();
        response.setTables(null);
        response.setCombined(true);
        response.setBookedTableIds(bookedTableIds);
        return response;
    }

    /**
     * Calculates a score for a table based on how well it matches the reservation request.
     *
     * @param table the table to score
     * @param req the reservation request
     * @return an integer score; higher means better match
     */
    private int score(TableEntity table, ReservationRequest req) {
        int score = 0;

        score += 20 - Math.abs(table.getSeats() - req.getSeats()) * 5;

        if (req.isHasWindow() && table.isHasWindow()) score += 10;
        if (req.isKids() && table.isKids()) score += 10;
        if (req.isAccessible() && table.isAccessible()) score += 30;
        if (req.getLocation().equals(table.getLocation())) score += 10;

        return score;
    }

    /**
     * Returns a list of IDs of tables that are already booked for a given date and time range.
     * <p>
     * If no reservations exist, random test reservations are generated.
     *
     * @param date the reservation date
     * @param startTime start of the time range
     * @param endTime end of the time range
     * @return list of booked table IDs
     */
    public List<Integer> getBookedTables(LocalDate date, LocalTime startTime, LocalTime endTime) {
        List<Integer> booked = reservationRepository.findBookedTables(date, startTime, endTime);

        if (booked.isEmpty()) {
            generateRandomReservations(date, startTime);
            booked = reservationRepository.findBookedTables(date, startTime, endTime);
        }

        return booked;
    }

    /**
     * Generates 1–4 random test reservations for a given date and time.
     * <p>
     * This is mainly used to simulate booked tables when none exist.
     *
     * @param date the reservation date
     * @param time the reservation time
     */
    private void generateRandomReservations(LocalDate date, LocalTime time) {
        List<TableEntity> tables = tableRepository.findAll();
        Collections.shuffle(tables);

        Random random = new Random();
        int amount = random.nextInt(4) + 1;

        for (int i = 0; i < amount && i < tables.size(); i++) {
            ReservationEntity r = new ReservationEntity();
            r.setTable(tables.get(i));
            r.setDate(date);
            r.setTime(time);
            r.setPeople(2);
            r.setName("Test Booking " + (i + 1));

            reservationRepository.save(r);
        }
    }
}