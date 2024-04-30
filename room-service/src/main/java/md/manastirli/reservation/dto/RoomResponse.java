package md.manastirli.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomResponse {
    private Long id;
    private int number;
    private String roomType;
    private int capacity;
    private BigDecimal pricePerNight;
    private String description;
    private String photo;
}
