package ee.kivistik.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Data Transfer Object representing a reservation.
 * <p>
 * Used to return reservation info to the frontend without exposing
 * the full entity and its database details.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDTO {

    private Integer id;
    private String customerName;
    private Integer tableId;
    private LocalDate date;
    private LocalTime time;
    private Integer people;
}