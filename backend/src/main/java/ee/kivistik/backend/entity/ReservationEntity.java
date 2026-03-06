package ee.kivistik.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "reservations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "table_id")
    private TableEntity table;
    private Integer people;
    private boolean wantsWindow;
    private boolean accessible;
    private String location;
    private boolean kids;
    private LocalDate date;
    private LocalTime time;


}