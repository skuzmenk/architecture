package ua.smartparking.data;

import org.springframework.stereotype.Repository;
import ua.smartparking.domain.Spot;
import ua.smartparking.domain.SpotStatus;
import java.util.*;

@Repository
public class ParkingRepository {
    // Імітація бази даних
    private final Map<Long, Spot> spots = new HashMap<>();

    public ParkingRepository() {
        // Початкові дані для тестування
        spots.put(101L, new Spot(101L, "вул. Шевченка, 1", SpotStatus.FREE));
        spots.put(102L, new Spot(102L, "вул. Шевченка, 1", SpotStatus.OCCUPIED));
    }

    public Optional<Spot> findById(Long id) {
        return Optional.ofNullable(spots.get(id));
    }

    public boolean existsByAddress(String address) {
        return spots.values().stream().anyMatch(s -> s.getAddress().equalsIgnoreCase(address));
    }
}