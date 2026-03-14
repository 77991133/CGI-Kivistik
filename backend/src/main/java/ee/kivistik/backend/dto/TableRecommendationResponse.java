package ee.kivistik.backend.dto;

import ee.kivistik.backend.entity.TableEntity;
import lombok.Data;

import java.util.List;

/**
 * Data Transfer Object used to return table recommendation results
 * from the backend to the frontend.
 * The response contains the tables suggested for the reservation,
 * information about whether multiple tables had to be combined,
 * and a list of tables that are already booked for the requested
 * time slot.
 */
@Data
public class TableRecommendationResponse {

    /**
     * List of recommended tables that satisfy the reservation request.
     * This may contain either a single suitable table or multiple
     * neighbouring tables if they were combined to meet the seating requirement.
     */
    private List<TableEntity> tables;

    /**
     * Indicates whether the returned tables were combined from multiple
     * neighbouring tables to satisfy the seat requirement.
     */
    private boolean combined;

    /**
     * List of table IDs that are already booked for the requested
     * date and time range.
     * This allows the frontend to visually distinguish booked tables
     * from free but non-recommended tables.
     */
    private List<Integer> bookedTableIds;

}