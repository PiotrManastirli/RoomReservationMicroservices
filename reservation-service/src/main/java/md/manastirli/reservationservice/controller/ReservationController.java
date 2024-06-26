package md.manastirli.reservationservice.controller;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import md.manastirli.reservationservice.dto.ReservationRequest;
import md.manastirli.reservationservice.dto.ReservationUpdateRequest;
import md.manastirli.reservationservice.model.Reservation;
import md.manastirli.reservationservice.service.ReservationService;
import md.manastirli.reservationservice.exceptions.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/reservation")
@RequiredArgsConstructor
public class ReservationController {
    @Autowired
    private final ReservationService reservationService;



    @GetMapping("dateReservations")
    @ResponseStatus(HttpStatus.OK)
    public List<Integer> getAllReservationsForRequestedDates
            (@RequestParam LocalDate startDate,
             @RequestParam LocalDate endDate) {
        return reservationService.getAllReservations(startDate,endDate);
    }

    @GetMapping()
    public List<Reservation> fetchReservations(
            @RequestParam(value = "room_number", required = false) Optional<Integer> optionalRoomNumber,
            @RequestParam(value = "guest_name", required = false) Optional<String> optionalGuestName){
        return reservationService.getReservation(optionalRoomNumber, optionalGuestName);
    }


    @PostMapping
    @CircuitBreaker(name = "inventory",fallbackMethod = "fallbackMethod")
    @TimeLimiter(name = "inventory")
    @Retry(name = "inventory")
    public CompletableFuture<String> placeReservation(@RequestBody ReservationRequest reservationRequest) {
        return CompletableFuture.supplyAsync(()->reservationService.placeReservation(reservationRequest));
    }

    public CompletableFuture<String> fallbackMethod(ReservationRequest reservationRequest, RuntimeException runtimeException){
        return CompletableFuture.supplyAsync(()->"Oops! Something went wrong, please add reservation after some time!");
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deleteReservation(@RequestParam Long id){
        reservationService.deleteReservation(id);
    }

    @PutMapping("/{reservationId}")
    @ResponseStatus(HttpStatus.OK)
    public void updateReservation(@PathVariable Long reservationId,
                           @RequestBody ReservationUpdateRequest request) {
        reservationService.updateReservation(reservationId, request);
    }
}
