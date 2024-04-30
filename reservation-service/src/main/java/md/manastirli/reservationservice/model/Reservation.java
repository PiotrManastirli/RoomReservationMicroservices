package md.manastirli.reservationservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "r_reservations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private int numberOfGuests;

    private String guestName;

    private String userName;

    private String contactInformation;

//    @Enumerated(EnumType.STRING)
//    private ReservationStatus status;

    private int roomId;
//    public enum ReservationStatus {
//        NEW,
//        CONFIRMED,
//        CANCELLED
//    }
}
