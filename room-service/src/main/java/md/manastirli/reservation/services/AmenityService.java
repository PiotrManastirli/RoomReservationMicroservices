package md.manastirli.reservation.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import md.manastirli.reservation.exceptions.BadRequestException;
import md.manastirli.reservation.model.Amenity;
import md.manastirli.reservation.repository.AmenityRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AmenityService {
    private final AmenityRepository amenityRepository;

    public List<Amenity> getAllAmenities() {
        return amenityRepository.findAll();
    }

    public void addAmenity(String amenityName) {
        if(amenityName.trim().isEmpty()){
            throw new BadRequestException("Amenity can't be empty!");
        }
        Optional<Amenity> existingAmenity = Optional.ofNullable(amenityRepository.findByName(amenityName));
        if (existingAmenity.isPresent()) {
            throw new BadRequestException(String.format("Amenity with name: \"%s\" already exists.", amenityName));
        }
        Amenity amenity = new Amenity();
        amenity.setName(amenityName);
        amenityRepository.save(amenity);
        log.info("Amenity with id: " + amenity.getId() + "is saved!");
    }

    public void deleteAmenity(Long id){
        Amenity amenity = amenityRepository.findAmenityById(id);
        amenityRepository.delete(amenity);
        log.info("Amenity with id: " + amenity.getId() + "is deleted!");
    }

    public void updateAmenity(Long id, String amenityName) {
        Amenity amenity = amenityRepository.findAmenityById(id);
        amenity.setName(amenityName);
        amenityRepository.save(amenity);
        log.info("Photo" + amenity.getId() + "is updated!");
    }

}
