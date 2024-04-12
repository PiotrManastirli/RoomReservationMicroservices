package md.manastirli.reservationservice.service;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import md.manastirli.reservationservice.dto.ReservationRequest;
import md.manastirli.reservationservice.dto.ReservationUpdateRequest;
import md.manastirli.reservationservice.event.ReservationPlacedEvent;
import md.manastirli.reservationservice.exceptions.NotFoundException;
import md.manastirli.reservationservice.model.Reservation;
import md.manastirli.reservationservice.repository.ReservationRepository;
import md.manastirli.reservationservice.exceptions.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final WebClient.Builder webClientBuilder;
    private final Tracer tracer;
    private final KafkaTemplate<String,ReservationPlacedEvent> kafkaTemplate;

    public String placeReservation(ReservationRequest reservationRequest) {
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
        kafkaTemplate.send("notificationTopic",new ReservationPlacedEvent(reservation.getId()));
        return "Reservation placed successfully!";
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
        if (!isRoomAvailable(reservation.getRoomNumber(), request.getCheckInDate(), request.getCheckOutDate())) {
            throw new BadRequestException("Room is not available for the requested dates.");
        }
        if (!isNumberOfGuestsValid(reservation.getRoomNumber(), request.getNumberOfGuests())) {
            throw new BadRequestException("Number of guests exceeds room capacity.");
        }
        reservation.setCheckInDate(request.getCheckInDate());
        reservation.setCheckOutDate(request.getCheckOutDate());
        reservation.setNumberOfGuests(request.getNumberOfGuests());
        reservation.setGuestName(request.getGuestName());
        reservation.setContactInformation(request.getContactInformation());
        reservationRepository.save(reservation);
    }

    private boolean isRoomAvailable(int roomNumber, LocalDate checkInDate, LocalDate checkOutDate) {
        List<Reservation> conflictingReservations = reservationRepository
                .findConflictingReservations(roomNumber, checkInDate, checkOutDate);
        return conflictingReservations.isEmpty();
    }

    private boolean isNumberOfGuestsValid(int roomNumber, int numberOfGuests) {
        Span roomServiceLookup = tracer.nextSpan().name("RoomServiceLookup");
        try(Tracer.SpanInScope spanInScope = tracer.withSpan(roomServiceLookup.start())){
        ResponseEntity<Integer> responseEntity = webClientBuilder.build().get()
                .uri("http://room-service/api/room/{roomNumber}/capacity", roomNumber)
                .retrieve()
                .toEntity(Integer.class)
                .block();

        return responseEntity != null && responseEntity.getBody() != null && numberOfGuests <= responseEntity.getBody();
    } finally {
            roomServiceLookup.end();
        }
    }

    public List<Integer> getAllReservations(LocalDate startDate, LocalDate endDate) {
        List<Reservation> reservations = reservationRepository
                .findAllByCheckInDateLessThanEqualAndCheckOutDateGreaterThanEqual(endDate, startDate);
        return reservations.stream()
                .map(Reservation::getRoomNumber)
                .collect(Collectors.toList());
    }

    public List<Reservation> getReservation(Optional<Integer> roomNumber, Optional<String> guestName) {
        if (roomNumber.isPresent()) {
            return reservationRepository.findAllByRoomNumber(roomNumber.get());
        } else if (guestName.isPresent()) {
            return reservationRepository.findAllByGuestName(guestName.get());
        } else {
            return reservationRepository.findAll();
        }
    }
}
