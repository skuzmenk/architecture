package ua.smartparking.data;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.smartparking.domain.Spot;
import java.util.List;

public interface ParkingRepository extends JpaRepository<Spot, Long> {
    List<Spot> findByParkingLotId(Long lotId);
}