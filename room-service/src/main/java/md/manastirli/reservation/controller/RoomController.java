package md.manastirli.reservation.controller;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import md.manastirli.reservation.dto.RoomRequest;
import md.manastirli.reservation.dto.RoomResponse;
import md.manastirli.reservation.dto.RoomUpdateRequest;
import md.manastirli.reservation.exceptions.InternalServerException;
import md.manastirli.reservation.model.Amenity;
import md.manastirli.reservation.services.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/room")
@RequiredArgsConstructor
@Transactional
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addRoom(@RequestBody RoomRequest roomRequest){
        roomService.addRoom(roomRequest);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<RoomResponse> getAllRooms() throws SQLException {
        return roomService.getAllRooms();
    }


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<RoomResponse> getAllAvailableRooms(@RequestParam LocalDate startDate,
                                                   @RequestParam LocalDate endDate,
    @RequestParam(value = "amenities", required = false) Optional<List<Amenity>> optionalAmenities ) throws SQLException {
        return roomService.getAllAvailableRooms(startDate, endDate, optionalAmenities);
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<?> removeRoom(@PathVariable Long roomId) {
        roomService.deleteRoom(roomId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{roomId}")
    @ResponseStatus(HttpStatus.OK)
    public void updateRoom(@PathVariable Long roomId,
            @RequestBody RoomUpdateRequest request) throws InternalServerException {
        roomService.updateRoom(roomId, request);
    }

    @PostMapping("/{roomId}/amenities/{amenityId}")
    public ResponseEntity<?> addAmenityToRoom(@PathVariable Long roomId, @PathVariable Long amenityId) {
        roomService.addAmenityToRoom(roomId, amenityId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{roomId}/amenities/{amenityId}")
    public ResponseEntity<?> removeAmenityFromRoom(@PathVariable Long roomId, @PathVariable Long amenityId) {
        roomService.removeAmenityFromRoom(roomId, amenityId);
        return ResponseEntity.ok().build();
    }


}
