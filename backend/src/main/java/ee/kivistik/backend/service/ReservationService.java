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

@Service
public class ReservationService {

    private final TableRepository tableRepository;
    private final ReservationRepository reservationRepository;

    public ReservationService(TableRepository tableRepository, ReservationRepository reservationRepository) {
        this.tableRepository = tableRepository;
        this.reservationRepository = reservationRepository;
    }

    /**
     * Tagastab parimad vabad lauad vastavalt broneeringu soovile.
     * Kui broneeringuid pole, genereeritakse automaatselt mõned juhuslikud.
     */
    public TableRecommendationResponse findBestTables(ReservationRequest request) {

        LocalTime startTime = request.time.minusHours(2);
        LocalTime endTime = request.time.plusHours(1);

        List<Integer> bookedTableIds = getBookedTables(request.date, startTime, endTime);

        List<TableEntity> recommendedTables = tableRepository.findAll().stream()
                .filter(t -> !bookedTableIds.contains(t.getId()))
                .filter(t -> t.getSeats() >= request.seats)
                .sorted((a, b) -> score(b, request) - score(a, request))
                .collect(Collectors.toList());
        if (!recommendedTables.isEmpty()) {
            TableRecommendationResponse response = new TableRecommendationResponse();
            response.tables = recommendedTables;
            response.combined = false;
            response.bookedTableIds = bookedTableIds;

            return response;
        } else {

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
                            if (seats + currentNeighbour.getSeats() >= request.seats) {
                                currentTables.add(currentNeighbour);
                                TableRecommendationResponse response = new TableRecommendationResponse();
                                response.tables = currentTables;
                                response.combined = true;
                                response.bookedTableIds = bookedTableIds;
                                return response;
                            } else {
                                seats += currentNeighbour.getSeats();
                                currentTables.add(currentNeighbour);
                                for (Integer neiboudId : currentNeighbour.getNeighbours()) {
                                    if ((!Objects.equals(table.getId(), neiboudId)) && !neighbours.contains(neiboudId) && !bookedTableIds.contains(neiboudId)) {
                                        neighbours.add(neiboudId);
                                    }

                                }
                            }
                        }

                    }
                }
            }

        }
        TableRecommendationResponse response = new TableRecommendationResponse();
        response.tables = null;
        response.combined = true;
        response.bookedTableIds = bookedTableIds;
        return response;

    }

    /**
     * Arvutab laua skoori vastavalt broneeringu soovile
     */
    private int score(TableEntity table, ReservationRequest req) {
        int score = 0;

        score += 20 - Math.abs(table.getSeats() - req.seats) * 5;

        if (req.hasWindow && table.isHasWindow()) score += 10;
        if (req.kids && table.isKids())  score += 10;
        if (req.accessible && table.isAccessible()) score += 30;
        if (req.location.equals(table.getLocation())) score += 10;

        return score;
    }

    /**
     * Tagastab juba broneeritud laudade ID-d
     * Kui ei leita ühtegi, genereeritakse juhuslikud broneeringud
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
     * Genereerib 1–4 juhuslikku broneeringut
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