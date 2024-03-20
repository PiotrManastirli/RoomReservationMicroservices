package md.manastirli.reservationservice.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import md.manastirli.reservationservice.dto.ReservationRequest;
import md.manastirli.reservationservice.dto.ReservationUpdateRequest;
import md.manastirli.reservationservice.exceptions.NotFoundException;
import md.manastirli.reservationservice.model.Reservation;
import md.manastirli.reservationservice.repository.ReservationRepository;
import md.manastirli.reservationservice.exceptions.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final WebClient webClient;

    public void placeReservation(ReservationRequest reservationRequest) {
        List<Reservation> conflictingReservations = reservationRepository.findConflictingReservations(
                reservationRequest.getRoomNumber(), reservationRequest.getCheckInDate(), reservationRequest.getCheckOutDate());
        if (!conflictingReservations.isEmpty()) {
            throw new RuntimeException("Room is not available for the specified dates");
        }
        Reservation reservation = new Reservation();
        reservation.setCheckInDate(reservationRequest.getCheckInDate());
        reservation.setCheckOutDate(reservationRequest.getCheckOutDate());
        reservation.setNumberOfGuests(reservationRequest.getNumberOfGuests());
        reservation.setGuestName(reservationRequest.getGuestName());
        reservation.setContactInformation(reservationRequest.getContactInformation());
        reservation.setStatus(reservationRequest.getStatus());
        reservation.setRoomNumber(reservationRequest.getRoomNumber());
//        reservation.setUserName();
        reservationRepository.save(reservation);
    }


    public void deleteReservation(Long id) {
        Optional<Reservation> reservationOptional = reservationRepository.findById(id);
        if (reservationOptional.isPresent()) {
            Reservation reservation = reservationOptional.get();
            reservationRepository.delete(reservation);
        } else {
            throw new EntityNotFoundException("Reservation with id " + id + " not found");
        }
    }


    public void updateReservation(Long reservationId, ReservationUpdateRequest request) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException("Reservation not found with id: " + reservationId));

        // Check if room is available for the requested dates
        if (!isRoomAvailable(reservation.getRoomNumber(), request.getCheckInDate(), request.getCheckOutDate())) {
            throw new BadRequestException("Room is not available for the requested dates.");
        }

        // Check if number of guests does not exceed room capacity
        if (!isNumberOfGuestsValid(reservation.getRoomNumber(), request.getNumberOfGuests())) {
            throw new BadRequestException("Number of guests exceeds room capacity.");
        }

        // Update reservation
        reservation.setCheckInDate(request.getCheckInDate());
        reservation.setCheckOutDate(request.getCheckOutDate());
        reservation.setNumberOfGuests(request.getNumberOfGuests());
        reservation.setGuestName(request.getGuestName());
        reservation.setContactInformation(request.getContactInformation());

        reservationRepository.save(reservation);
    }

    private boolean isRoomAvailable(int roomNumber, LocalDate checkInDate, LocalDate checkOutDate) {
        ResponseEntity<Boolean> responseEntity = webClient.get()
                .uri("/rooms/{roomNumber}/available?checkInDate={checkInDate}&checkOutDate={checkOutDate}",
                        roomNumber, checkInDate, checkOutDate)
                .retrieve()
                .toEntity(Boolean.class)
                .block();

        return responseEntity != null && responseEntity.getBody() != null && responseEntity.getBody();
    }

    private boolean isNumberOfGuestsValid(int roomNumber, int numberOfGuests) {
        ResponseEntity<Integer> responseEntity = webClient.get()
                .uri("/rooms/{roomNumber}/capacity", roomNumber)
                .retrieve()
                .toEntity(Integer.class)
                .block();

        return responseEntity != null && responseEntity.getBody() != null && numberOfGuests <= responseEntity.getBody();
    }
}
