package md.manastirli.reservation.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(schema = "Rooms")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private int number;
    private RoomType type;
    private int capacity;
    private BigDecimal pricePerNight;
    private String description;
    @ManyToMany
    @JoinTable(
            name = "room_amenity",
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "amenity_id")
    )
    private List<Amenity> amenities;
    private RoomStatus status;
    @ManyToMany
    @JoinTable(
            name = "room_photo",
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "photo_id")
    )
    private List<Photo> photos;
        public enum RoomType {
            STANDARD,
            LUXURY,
            APARTMENT
        }

        public enum RoomStatus {
            AVAILABLE,
            OCCUPIED,
            BEING_CLEANED
        }

}
