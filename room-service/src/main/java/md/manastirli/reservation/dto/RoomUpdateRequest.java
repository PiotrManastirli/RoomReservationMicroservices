package md.manastirli.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomUpdateRequest {
    private String roomType;
    private BigDecimal pricePerNight;
    private String description;
    private MultipartFile photo;
}