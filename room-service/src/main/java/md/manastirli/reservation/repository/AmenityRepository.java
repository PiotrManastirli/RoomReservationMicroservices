package md.manastirli.reservation.repository;

import md.manastirli.reservation.model.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmenityRepository extends JpaRepository<Amenity,Long> {

    Amenity findAmenityById(Long id);

    Amenity findByName(String name);
}

