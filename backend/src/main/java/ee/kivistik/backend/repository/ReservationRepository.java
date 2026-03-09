package ee.kivistik.backend.repository;

import ee.kivistik.backend.dto.Reservation;
import ee.kivistik.backend.entity.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<ReservationEntity,Integer> {
    @Query("""
       SELECT r.table.id
       FROM ReservationEntity r
       WHERE r.date = :date
       AND r.time BETWEEN :startTime AND :endTime
       """)
    List<Integer> findBookedTables(
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime
    );
}
