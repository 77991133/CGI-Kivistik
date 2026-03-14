package ee.kivistik.backend.mapper;

import ee.kivistik.backend.dto.ReservationDTO;
import ee.kivistik.backend.entity.ReservationEntity;

/**
 * Utility class for mapping ReservationEntity objects to ReservationDTO objects.
 * <p>
 * This is used to safely send reservation information to the frontend
 * without exposing the full JPA entity, its relationships, or database details.
 */
public class ReservationMapperUtil {
    /**
     * Converts a ReservationEntity to a ReservationDTO.
     *
     * @param reservation the ReservationEntity to be mapped
     * @return a ReservationDTO containing only the necessary information
     *         for the frontend, including the table ID instead of the full TableEntity
     */
    public static ReservationDTO toDTO(ReservationEntity reservation) {
        return new ReservationDTO(
                reservation.getId(),
                reservation.getName(),
                reservation.getTable().getId(),
                reservation.getDate(),
                reservation.getTime(),
                reservation.getPeople()
        );
    }
}