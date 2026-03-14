package ee.kivistik.backend.repository;

import ee.kivistik.backend.entity.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Spring Data JPA repository for managing ReservationEntity objects.
 * <p>
 * Provides standard CRUD operations and custom queries for retrieving
 * reservations and booked table information.
 */
public interface ReservationRepository extends JpaRepository<ReservationEntity, Integer> {

    /**
     * Finds IDs of tables that are already booked for a specific date and time range.
     * <p>
     * This query is used to determine which tables are unavailable when
     * a new reservation is being requested.
     *
     * @param date the date of the reservation
     * @param startTime the start of the time range to check
     * @param endTime the end of the time range to check
     * @return a list of table IDs that are booked in the specified time range
     */
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