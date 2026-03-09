package ee.kivistik.backend.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

public class Reservation {
    public String customerName;
    public Integer tableId;
    public Integer seats;
    public LocalDate date;
    public LocalTime time;


    public Reservation(String customerName, Integer tableId, Integer seats, LocalDate date, LocalTime time) {
        this.customerName = customerName;
        this.tableId = tableId;
        this.seats = seats;
        this.date = date;
        this.time = time;
    }


}
