package md.manastirli.reservation.controller;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import md.manastirli.reservation.dto.RoomRequest;
import md.manastirli.reservation.dto.RoomResponse;
import md.manastirli.reservation.dto.RoomUpdateRequest;
import md.manastirli.reservation.services.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

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

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<RoomResponse> getAllRooms() {
        return roomService.getAllRooms();
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<RoomResponse> getAllAvailableRooms(@RequestParam LocalDate startDate,
                                                   @RequestParam LocalDate endDate) {
        return roomService.getAllAvailableRooms();
    }
    @DeleteMapping("/{roomId}")
    public ResponseEntity<?> removeRoom(@PathVariable Long roomId) {
        roomService.deleteRoom(roomId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{roomId}")
    @ResponseStatus(HttpStatus.OK)
    public void updateRoom(@PathVariable Long roomId,
            @RequestBody RoomUpdateRequest request) {
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

    @PostMapping("/{roomId}/photos/{photoId}")
    public ResponseEntity<?> addPhotoToRoom(@PathVariable Long roomId, @PathVariable Long photoId) {
        roomService.addPhotoToRoom(roomId, photoId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{roomId}/photos/{photoId}")
    public ResponseEntity<?> removePhotoFromRoom(@PathVariable Long roomId, @PathVariable Long photoId) {
        roomService.removePhotoFromRoom(roomId, photoId);
        return ResponseEntity.ok().build();
    }

}
