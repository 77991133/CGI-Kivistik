package ee.kivistik.backend.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Data Transfer Object used for requesting table recommendations.
 */
@Data
public class ReservationRequest {

    private Integer seats;
    private boolean kids;
    private boolean hasWindow;
    private boolean accessible;
    private String location;
    private LocalDate date;
    private LocalTime time;
}