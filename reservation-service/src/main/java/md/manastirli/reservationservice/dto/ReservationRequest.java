package md.manastirli.reservationservice.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import md.manastirli.reservationservice.model.Reservation;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationRequest {
    private LocalDate checkInDate;

    private LocalDate checkOutDate;
    
    private int numberOfGuests;

    private String guestName;

    private String contactInformation;

    private Reservation.ReservationStatus status;

    private int roomNumber;
}
