package md.manastirli.reservationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationUpdateRequest {
    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private int numberOfGuests;

    private String guestName;

    private String contactInformation;

}
