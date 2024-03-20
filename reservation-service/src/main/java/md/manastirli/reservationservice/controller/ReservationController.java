package md.manastirli.reservationservice.controller;

import lombok.RequiredArgsConstructor;
import md.manastirli.reservationservice.dto.ReservationRequest;
import md.manastirli.reservationservice.dto.ReservationUpdateRequest;
import md.manastirli.reservationservice.service.ReservationService;
import md.manastirli.reservationservice.exceptions.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequestMapping("/api/reservation")
@RequiredArgsConstructor
public class ReservationController {
    @Autowired
    private final ReservationService reservationService;

    private final WebClient webClient;

    @PostMapping
    public String placeReservation(@RequestBody ReservationRequest reservationRequest) {
        reservationService.placeReservation(reservationRequest);
        return "Reservation placed successfully!";
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
