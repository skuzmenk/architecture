package ua.smartparking.data;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.smartparking.domain.ParkingLot;

public interface ParkingLotRepository extends JpaRepository<ParkingLot, Long> {
    boolean existsByName(String name);
}