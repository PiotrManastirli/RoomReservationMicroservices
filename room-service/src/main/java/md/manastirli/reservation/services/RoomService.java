package md.manastirli.reservation.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import md.manastirli.reservation.dto.RoomRequest;
import md.manastirli.reservation.dto.RoomResponse;
import md.manastirli.reservation.dto.RoomUpdateRequest;
import md.manastirli.reservation.exceptions.BadRequestException;
import md.manastirli.reservation.exceptions.NotFoundException;
import md.manastirli.reservation.model.Amenity;
import md.manastirli.reservation.model.Photo;
import md.manastirli.reservation.model.Room;
import md.manastirli.reservation.repository.AmenityRepository;
import md.manastirli.reservation.repository.PhotoRepository;
import md.manastirli.reservation.repository.RoomRepository;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomService {
    private final RoomRepository roomRepository;

    private final AmenityRepository amenityRepository;

    private final PhotoRepository photoRepository;

    private final WebClient.Builder webClientBuilder;
    public void addRoom(RoomRequest roomRequest){
        Room room = Room.builder()
                .number(roomRequest.getNumber())
                .type(roomRequest.getType())
                .capacity(roomRequest.getCapacity())
                .pricePerNight(roomRequest.getPricePerNight())
                .description(roomRequest.getDescription())
                .amenities(roomRequest.getAmenities())
                .photos(roomRequest.getPhotos())
                .build();
        roomRepository.saveAndFlush(room);
        log.info("Room" + room.getId() + "is saved!");
    }

    public List<RoomResponse> getAllRooms() {
        List<Room> rooms = roomRepository.findAll();
        return rooms.stream().map(this::mapToRoomResponse).toList();
    }

    private RoomResponse mapToRoomResponse(Room room) {
        return RoomResponse.builder()
                .number(room.getNumber())
                .type(room.getType())
                .capacity(room.getCapacity())
                .pricePerNight(room.getPricePerNight())
                .description(room.getDescription())
                .amenities(room.getAmenities())
                .photos(room.getPhotos())
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

    public void addPhotoToRoom(Long roomId, Long photoId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("Room not found with id: " + roomId));

        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new NotFoundException("Photo not found with id: " + photoId));
        if (room.getPhotos().contains(photo)) {
            throw new BadRequestException("Photo is already added to the room.");
        }
        room.getPhotos().add(photo);
        roomRepository.save(room);
    }

    public void removePhotoFromRoom(Long roomId, Long photoId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("Room not found with id: " + roomId));

        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new NotFoundException("Photo not found with id: " + photoId));
        if (room.getPhotos().contains(photo)) {
            room.getPhotos().remove(photo);
            log.info("Photo with id: " + photo.getId() + " was successfully removed from room!");
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
    public void updateRoom(Long roomId, RoomUpdateRequest request) {
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
        if (request.getPhotos() != null) {
            List<Photo> photos = photoRepository.findAllById(request.getPhotos()
                    .stream()
                    .map(Photo::getId)
                    .collect(Collectors.toList()));
            room.setPhotos(photos);
        }
        roomRepository.save(room);
    }

    public List<RoomResponse> getAllAvailableRooms(LocalDate startDate, LocalDate endDate, Optional<List<Amenity>> amenities) {
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
        return availableRooms.stream()
                .map(this::mapToRoomResponse)
                .collect(Collectors.toList());
    }



}
