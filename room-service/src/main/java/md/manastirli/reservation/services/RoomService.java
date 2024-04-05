package md.manastirli.reservation.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import md.manastirli.reservation.dto.RoomRequest;
import md.manastirli.reservation.dto.RoomResponse;
import md.manastirli.reservation.dto.RoomUpdateRequest;
import md.manastirli.reservation.exceptions.BadRequestException;
import md.manastirli.reservation.exceptions.InternalServerException;
import md.manastirli.reservation.exceptions.NotFoundException;
import md.manastirli.reservation.model.Amenity;
import md.manastirli.reservation.model.Room;
import md.manastirli.reservation.repository.AmenityRepository;
import md.manastirli.reservation.repository.RoomRepository;
import org.apache.commons.codec.binary.Base64;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomService {
    private final RoomRepository roomRepository;

    private final AmenityRepository amenityRepository;

    private final WebClient.Builder webClientBuilder;

    public void addRoom(RoomRequest roomRequest){
        Room room = Room.builder()
                .number(roomRequest.getNumber())
                .type(roomRequest.getType())
                .capacity(roomRequest.getCapacity())
                .pricePerNight(roomRequest.getPricePerNight())
                .description(roomRequest.getDescription())
                .amenities(roomRequest.getAmenities())
                .build();

        MultipartFile photoFile = roomRequest.getPhoto();
        if (photoFile != null && !photoFile.isEmpty()) {
            try {
                byte[] photoBytes = photoFile.getBytes();
                Blob photoBlob = new SerialBlob(photoBytes);
                room.setPhoto(photoBlob);
            } catch (IOException | SQLException e) {
                log.error("Failed to process photo file: " + e.getMessage());
            }
        }

        roomRepository.saveAndFlush(room);
        log.info("Room " + room.getId() + " is saved!");
    }


    public List<RoomResponse> getAllRooms() throws SQLException {
        List<Room> rooms = roomRepository.findAll();
        List<RoomResponse> roomResponses = new ArrayList<>();
        for (Room room : rooms) {
            RoomResponse roomResponse = mapToRoomResponse(room);
            roomResponses.add(roomResponse);
        }
        return roomResponses;
    }

    private RoomResponse mapToRoomResponse(Room room) throws SQLException {
        String base64Photo = null;
        Blob photoBlob = room.getPhoto();
        if (photoBlob != null) {
            try {
                byte[] photoBytes = photoBlob.getBytes(1, (int) photoBlob.length());
                base64Photo = Base64.encodeBase64String(photoBytes);
            } catch (SQLException ex) {
                throw new SQLException("Failed to get photo bytes for room: " + room.getId());
            }
        }
        return RoomResponse.builder()
                .number(room.getNumber())
                .type(room.getType())
                .capacity(room.getCapacity())
                .pricePerNight(room.getPricePerNight())
                .description(room.getDescription())
                .amenities(room.getAmenities())
                .photo(base64Photo)
                .build();
    }


    public void addAmenityToRoom(Long roomId, Long amenityId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("Room not found with id: " + roomId));

        Amenity amenity = amenityRepository.findById(amenityId)
                .orElseThrow(() -> new NotFoundException("Amenity not found with id: " + amenityId));

        if (room.getAmenities().contains(amenity)) {
            throw new BadRequestException("Amenity is already added to the room.");
        }
        room.getAmenities().add(amenity);
        roomRepository.save(room);
    }

    public void removeAmenityFromRoom(Long roomId, Long amenityId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("Room not found with id: " + roomId));

        Amenity amenity = amenityRepository.findById(amenityId)
                .orElseThrow(() -> new NotFoundException("Amenity not found with id: " + amenityId));
        if (room.getAmenities().contains(amenity)) {
            room.getAmenities().remove(amenity);
            log.info("Amenity with id: " + amenity.getId() + " was successfully removed from room!");
        }
        roomRepository.save(room);
    }

    public void deleteRoom(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("Room not found with id: " + roomId));

        roomRepository.delete(room);
        log.info("room with id: " + room.getId() + " was successfully removed from room!");
    }
    @Transactional
    public void updateRoom(Long roomId, RoomUpdateRequest request) throws InternalServerException {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Room not found"));
        if (request.getPricePerNight() != null) {
            room.setPricePerNight(request.getPricePerNight());
        }
        if (request.getDescription() != null) {
            room.setDescription(request.getDescription());
        }
        if (request.getType() != null) {
            room.setType(request.getType());
        }
        if (request.getAmenities() != null) {
            List<Amenity> amenities = amenityRepository.findAllById(request.getAmenities()
                    .stream()
                    .map(Amenity::getId)
                    .collect(Collectors.toList()));
            room.setAmenities(amenities);
        }
        if (request.getPhoto() != null) {
            try {
                byte[] photoBytes = request.getPhoto().getBytes();
                Blob photoBlob = new SerialBlob(photoBytes);
                room.setPhoto(photoBlob);
            } catch (IOException | SQLException ex) {
                throw new InternalServerException("Fail updating room");
            }
        }
        roomRepository.save(room);
    }

    public List<RoomResponse> getAllAvailableRooms(LocalDate startDate, LocalDate endDate, Optional<List<Amenity>> amenities) throws SQLException {
        List<Room> rooms = roomRepository.findAll();
        List<Integer> occupiedRoomNumbers = webClientBuilder.build().get()
                .uri(uriBuilder -> uriBuilder
                        .path("http://reservation-service/api/reservation")
                        .queryParam("startDate", startDate)
                        .queryParam("endDate", endDate)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Integer>>() {})
                .block();

        List<Room> availableRooms = rooms.stream()
                .filter(room -> {
                    assert occupiedRoomNumbers != null;
                    return !occupiedRoomNumbers.contains(room.getNumber());
                })
                .filter(room -> {
                    if (amenities.isPresent()) {
                        return room.getAmenities().containsAll(amenities.get());
                    } else {
                        return true;
                    }
                })
                .toList();
        List<RoomResponse> collect = new ArrayList<>();
        for (Room availableRoom : availableRooms) {
            RoomResponse roomResponse = mapToRoomResponse(availableRoom);
            collect.add(roomResponse);
        }
        return collect;
    }



}
