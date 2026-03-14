package ee.kivistik.backend.mapper;

import ee.kivistik.backend.dto.TableDTO;
import ee.kivistik.backend.entity.TableEntity;

/**
 * Utility class for mapping TableEntity objects to TableDTO objects.
 *
 * This is used to safely send table information to the frontend
 * without exposing the full JPA entity, its relationships, or database details.
 */
public class TableMapperUtil {

    /**
     * Converts a TableEntity to a TableDTO.
     *
     * @param table the TableEntity to be mapped
     * @return a TableDTO containing only the necessary information
     *         for the frontend
     */
    public static TableDTO mapToDTO(TableEntity table) {
        return new TableDTO(
                table.getId(),
                table.getSeats(),
                table.isHasWindow(),
                table.isAccessible(),
                table.getLocation(),
                table.isKids(),
                table.getNeighbours()
        );
    }
}