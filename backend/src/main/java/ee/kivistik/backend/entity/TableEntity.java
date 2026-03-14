package ee.kivistik.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Entity representing a restaurant table.
 * Stores information about the table's capacity, location,
 * accessibility, window preference, suitability for children,
 * and neighboring tables for combining reservations.
 * Mapped to the "restaurant_table" table in the database.
 */
@Entity
@Table(name = "restaurant_table")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableEntity {

    /**
     * Unique identifier of the table.
     */
    @Id
    private Integer id;

    /**
     * Number of seats at this table.
     */
    private Integer seats;

    /**
     * Indicates whether the table is located near a window.
     */
    private boolean hasWindow;

    /**
     * Indicates whether the table is accessible (e.g., wheelchair access).
     */
    private boolean accessible;

    /**
     * Location of the table within the restaurant (e.g., terrace or main hall).
     */
    private String location;

    /**
     * Indicates whether the table is near children's corner
     */
    private boolean kids;

    /**
     * List of neighboring table IDs.
     * <p>
     * Used for combining tables if the requested number of seats
     * cannot be met by a single table. This is mapped to a separate
     * "table_neighbors" collection table.
     */
    @ElementCollection
    @CollectionTable(
            name = "table_neighbours",
            joinColumns = @JoinColumn(name = "table_id")
    )
    @Column(name = "neighbour_id")
    private List<Integer> neighbours;

}