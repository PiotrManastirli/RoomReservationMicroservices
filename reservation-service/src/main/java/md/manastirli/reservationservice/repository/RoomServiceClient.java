package md.manastirli.reservationservice.repository;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "RoomService", url = "${room.service.url}") // room-service - это имя вашего сервиса управления номерами
public interface RoomServiceClient {

    @GetMapping("/rooms/{roomNumber}/maxCapacity")
    int getMaxCapacityForRoom(@PathVariable("roomNumber") int roomNumber);
}
