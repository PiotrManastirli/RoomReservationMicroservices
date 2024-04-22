package md.manastirli.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import md.manastirli.reservation.model.Amenity;
import md.manastirli.reservation.model.Room;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomResponse {
    private int number;
    private Room.RoomType type;
    private int capacity;
    private BigDecimal pricePerNight;
    private String description;
    private List<Amenity> amenities;
    private String photo;
}
