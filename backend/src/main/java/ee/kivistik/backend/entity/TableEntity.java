package ee.kivistik.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "restaurant_table")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableEntity {
    @Id
    private Integer id;
    private Integer seats;
    private boolean hasWindow;
    private boolean accessible;
    private String location;
    private boolean kids;
    @ElementCollection
    @CollectionTable(
            name = "table_neighbours",
            joinColumns = @JoinColumn(name = "table_id")
    )
    @Column(name = "neighbour_id")
    private List<Integer> neighbours;

}