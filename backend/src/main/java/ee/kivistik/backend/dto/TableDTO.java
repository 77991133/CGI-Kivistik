package ee.kivistik.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Table DTO for sending table info to the frontend.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableDTO {
    private Integer id;
    private Integer seats;
    private boolean hasWindow;
    private boolean accessible;
    private String location;
    private boolean kids;
    private List<Integer> neighbours;
}