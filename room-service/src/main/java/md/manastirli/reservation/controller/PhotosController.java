package md.manastirli.reservation.controller;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import md.manastirli.reservation.model.Photo;
import md.manastirli.reservation.services.PhotoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/photo")
@RequiredArgsConstructor
@Transactional
public class PhotosController {
    private final PhotoService photoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addPhoto(@RequestBody String photoUrl){
        photoService.addPhoto(photoUrl);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deleteAmenity(@RequestBody Long id){
        photoService.deletePhoto(id);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    public void updateAmenity(@RequestBody Long id, @RequestBody String url){
        photoService.updatePhoto(id,url);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Photo> getAllPhotos() {
        return photoService.getAllPhotos();
    }
}
