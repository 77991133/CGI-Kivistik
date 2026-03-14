package ee.kivistik.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Data Transfer Object (DTO) used for creating a reservation.
 * <p>
 * This class represents the reservation information sent from the frontend
 * to the backend when a user creates a booking. It contains customer details,
 * the selected table, reservation date and time, and the number of seats.
 */
@Data
@AllArgsConstructor
public class Reservation {

    /**
     * Name of the customer making the reservation.
     */
    private String customerName;

    /**
     * Identifier of the table being reserved.
     */
    private Integer tableId;

    /**
     * Number of seats requested for the reservation.
     */
    private Integer seats;

    /**
     * Date of the reservation.
     */
    private LocalDate date;

    /**
     * Time of the reservation.
     */
    private LocalTime time;


}