package md.manastirli.reservation.controller;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import md.manastirli.reservation.dto.RoomRequest;
import md.manastirli.reservation.dto.RoomResponse;
import md.manastirli.reservation.dto.RoomUpdateRequest;
import md.manastirli.reservation.exceptions.InternalServerException;
import md.manastirli.reservation.services.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/room")
@RequiredArgsConstructor
@Transactional
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addRoom(@RequestBody RoomRequest roomRequest, @RequestParam MultipartFile photo){
        roomService.addRoom(roomRequest, photo);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<Optional<RoomResponse>> getRoomById(@PathVariable Long roomId) throws SQLException {
        RoomResponse theRoom = roomService.getRoomById(roomId);
        return  ResponseEntity.ok(Optional.of(theRoom));
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<RoomResponse> getAllRooms() throws SQLException {
        return roomService.getAllRooms();
    }

    @GetMapping("/types")
    public List<String> getRoomTypes() {
        return roomService.getAllRoomTypes();
    }


    @GetMapping("/availableRooms")
    @ResponseStatus(HttpStatus.OK)
    public List<RoomResponse> getAllAvailableRooms(@RequestParam LocalDate checkInDate,
                                                   @RequestParam LocalDate checkOutDate,
                                                   @RequestParam String roomType) throws SQLException {
        return roomService.getAllAvailableRooms(checkInDate, checkOutDate, roomType);
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

//    @PostMapping("/{roomId}/amenities/{TypeId}")
//    public ResponseEntity<?> addTypeToRoom(@PathVariable Long roomId, @PathVariable Long TypeId) {
//        roomService.addTypeToRoom(roomId, TypeId);
//        return ResponseEntity.ok().build();
//    }



}
