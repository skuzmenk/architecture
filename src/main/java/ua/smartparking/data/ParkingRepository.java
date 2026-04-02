package ua.smartparking.data;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.smartparking.domain.Spot;

public interface ParkingRepository extends JpaRepository<Spot, Long> {
    boolean existsByAddressIgnoreCase(String address);
}