package ua.smartparking.presentation;

import org.springframework.web.bind.annotation.*;
import ua.smartparking.application.ParkingService;
import ua.smartparking.domain.SpotStatus;

@RestController
@RequestMapping("/api/v1")
public class ParkingController {
    private final ParkingService service;

    public ParkingController(ParkingService service) {
        this.service = service;
    }

    // Сценарій: Бронювання місця (Тепер через POST)
    @PostMapping("/book")
    public String book(@RequestParam Long id) {
        return service.createBooking(id);
    }

    // Сценарій: Оновлення від датчика (Тепер через POST)
    @PostMapping("/sensor")
    public String sensor(@RequestParam Long id, @RequestParam SpotStatus status) {
        service.updateFromSensor(id, status);
        return "Статус оновлено на " + status;
    }

    // Сценарій: Звітність (Залишаємо GET, бо це отримання даних)
    @GetMapping("/revenue")
    public String getRevenue(@RequestHeader(value = "X-Role", required = false) String role) {
        try {
            return "Виручка: " + service.getRevenue(role) + " грн";
        } catch (Exception e) {
            return "Доступ заборонено! Ви не OWNER.";
        }
    }
}