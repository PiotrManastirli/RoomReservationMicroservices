package md.manastirli.reservation.repository;

import md.manastirli.reservation.model.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoRepository extends JpaRepository<Photo,Long> {

    Photo findPhotoById(Long id);

    Photo findByUrl(String url);
}

