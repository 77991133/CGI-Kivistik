package ee.kivistik.backend.service;

import ee.kivistik.backend.dto.ReservationRequest;
import ee.kivistik.backend.entity.ReservationEntity;
import ee.kivistik.backend.entity.TableEntity;
import ee.kivistik.backend.repository.ReservationRepository;
import ee.kivistik.backend.repository.TableRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Random;
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
    public List<TableEntity> findBestTables(ReservationRequest request) {

        LocalTime startTime = request.time.minusHours(2);
        LocalTime endTime = request.time.plusHours(1);

        // Saame juba broneeritud laudade ID-d või genereerime juhuslikud
        List<Integer> bookedTableIds = getBookedTables(request.date, startTime, endTime);

        // Filtreerime ja skoorime vabad lauad
        return tableRepository.findAll().stream()
                .filter(t -> !bookedTableIds.contains(t.getId()))
                .filter(t -> t.getSeats() >= request.seats)
                .sorted((a, b) -> score(b, request) - score(a, request))
                .collect(Collectors.toList());
    }

    /**
     * Arvutab laua skoori vastavalt broneeringu soovile
     */
    private int score(TableEntity table, ReservationRequest req) {
        int score = 0;

        score += 20 - Math.abs(table.getSeats() - req.seats) * 5;

        if (req.hasWindow && table.isHasWindow()) score += 10;
        if (req.kids && table.isKids()) score += 10;
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
     * Genereerib 1–3 juhuslikku broneeringut
     */
    private void generateRandomReservations(LocalDate date, LocalTime time) {

        List<TableEntity> tables = tableRepository.findAll();
        Collections.shuffle(tables);

        Random random = new Random();
        int amount = random.nextInt(4) + 1; // 1-3 broneeringut

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