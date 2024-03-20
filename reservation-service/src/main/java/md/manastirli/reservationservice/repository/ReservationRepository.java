package md.manastirli.reservationservice.repository;

import md.manastirli.reservationservice.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation,Long> {

//    Reservation findReservationByCheckInDateNotInAndAndCheckOutDateNotIn(LocalDate checkInDate, LocalDate checkOutDate);
    @Query("SELECT r FROM Reservation r WHERE r.roomNumber = :roomNumber " +
            "AND ((r.checkInDate >= :checkInDate AND r.checkInDate < :checkOutDate) " +
            "OR (r.checkOutDate > :checkInDate AND r.checkOutDate <= :checkOutDate))")
    List<Reservation> findConflictingReservations(int roomNumber, LocalDate checkInDate, LocalDate checkOutDate);

}
