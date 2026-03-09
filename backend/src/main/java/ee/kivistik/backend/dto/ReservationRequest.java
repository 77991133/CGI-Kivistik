package ee.kivistik.backend.dto;

import ee.kivistik.backend.entity.TableEntity;

import java.time.LocalDate;
import java.time.LocalTime;

public class ReservationRequest {

    public Integer seats;
    public boolean kids;
    public boolean hasWindow;
    public boolean accessible;
    public String location;
    public LocalDate date;
    public LocalTime time;


}
