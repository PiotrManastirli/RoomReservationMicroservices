package md.manastirli.reservation.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import md.manastirli.reservation.exceptions.BadRequestException;
import md.manastirli.reservation.model.Amenity;
import md.manastirli.reservation.model.Photo;
import md.manastirli.reservation.repository.PhotoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class PhotoService {
    private final PhotoRepository photoRepository;


    public List<Photo> getAllPhotos() {
        return photoRepository.findAll();
    }

    public void addPhoto(String url) {
        if(url.trim().isEmpty()){
            throw new BadRequestException("URL can't be empty!");
        }
        Optional<Photo> existingPhoto = Optional.ofNullable(photoRepository.findByUrl(url));
        if (existingPhoto.isPresent()) {
            throw new BadRequestException(String.format("Photo with url: \"%s\" already exists.", url));
        }
        Photo photo = new Photo();
        photo.setUrl(url);
        photoRepository.save(photo);
        log.info("Photo " + photo.getId() + " is saved!");
    }

    public void deletePhoto(Long id){
        Photo photo = photoRepository.findPhotoById(id);
        photoRepository.delete(photo);
        log.info("Photo " + photo.getId() + " is deleted!");
    }

    public void updatePhoto(Long id, String url) {
        Photo photo = photoRepository.findPhotoById(id);
        photo.setUrl(url);
        photoRepository.save(photo);
        log.info("Photo " + photo.getId() + " is updated!");
    }


}
