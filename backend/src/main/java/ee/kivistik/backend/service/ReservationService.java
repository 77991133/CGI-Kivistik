package ee.kivistik.backend.service;

import ee.kivistik.backend.dto.ReservationRequest;
import ee.kivistik.backend.entity.TableEntity;
import ee.kivistik.backend.repository.ReservationRepository;
import ee.kivistik.backend.repository.TableRepository;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
public class ReservationService {

    private final TableRepository tableRepository;
    private final ReservationRepository reservationRepository;

    public ReservationService(TableRepository tableRepository, ReservationRepository reservationRepository) {
        this.tableRepository = tableRepository;
        this.reservationRepository = reservationRepository;
    }

    public  List<TableEntity> findBestTables(ReservationRequest request){

        List<TableEntity> tables = tableRepository.findAll();
        LocalTime startTime = request.time.minusHours(2);
        LocalTime endTime = request.time.plusHours(1);

        List<Integer> bookedTableIds = reservationRepository.findBookedTables(request.date, startTime,endTime);
        List<TableEntity> freeTablesSorted = tables.stream()
                .filter(t ->!bookedTableIds.contains(t.getId()))
                .filter(t -> t.getSeats() >= request.seats)
                .sorted((a,b) -> score(b,request) - score(a,request))
                .toList();
        System.out.println(freeTablesSorted);
        return freeTablesSorted;
    }

    private int score(TableEntity table, ReservationRequest req){

        int score = 0;

        score += 20 - (table.getSeats() - req.seats)*5;

        if(req.hasWindow && table.isHasWindow())
            score += 10;

        if(req.kids && table.isKids())
            score += 10;

        if(req.accessible && table.isAccessible())
            score += 30;
        if(req.location.equals(table.getLocation()))
            score += 10;

        return score;
    }
}