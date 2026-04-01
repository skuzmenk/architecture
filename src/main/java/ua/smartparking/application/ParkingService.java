package ua.smartparking.application;

import org.springframework.stereotype.Service;
import ua.smartparking.data.ParkingRepository;
import ua.smartparking.domain.Spot;
import ua.smartparking.domain.SpotStatus;
import java.util.UUID;

@Service
public class ParkingService {
    private final ParkingRepository repository;

    public ParkingService(ParkingRepository repository) {
        this.repository = repository;
    }

    // Сценарій 1: Бронювання (Reliability)
    public String createBooking(Long spotId) {
        Spot spot = repository.findById(spotId)
                .orElseThrow(() -> new RuntimeException("Місце не знайдено"));

        if (!spot.isFree()) {
            return "Помилка: Місце вже зайняте!";
        }

        spot.setStatus(SpotStatus.RESERVED);
        return "Бронювання успішне! Ваш QR: QR_" + UUID.randomUUID().toString().substring(0, 8);
    }

    // Сценарій 2: IoT Оновлення (Availability)
    public void updateFromSensor(Long spotId, SpotStatus status) {
        repository.findById(spotId).ifPresent(s -> s.setStatus(status));
        System.out.println("Датчик оновив статус місця " + spotId + " на " + status);
    }

    // Сценарій 3: Перевірка прав (Security/Logic)
    public double getRevenue(String role) {
        if (!"OWNER".equals(role)) throw new RuntimeException("Немає доступу!");
        return 5400.0; // Тестова сума
    }
}