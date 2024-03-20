package md.manastirli.reservation.controller;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import md.manastirli.reservation.dto.RoomRequest;
import md.manastirli.reservation.dto.RoomResponse;
import md.manastirli.reservation.model.Amenity;
import md.manastirli.reservation.services.AmenityService;
import md.manastirli.reservation.services.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/amenity")
@RequiredArgsConstructor
@Transactional
public class AmenityController {
    private final AmenityService amenityService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addAmenity(@RequestBody String amenityName){
        amenityService.addAmenity(amenityName);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deleteAmenity(@RequestParam Long id){
        amenityService.deleteAmenity(id);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    public void updateAmenity(@RequestParam Long id, @RequestParam String name){
        amenityService.updateAmenity(id,name);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Amenity> getAllAmenities() {
        return amenityService.getAllAmenities();
    }
}
