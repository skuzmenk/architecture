package ua.smartparking.presentation;
import java.util.Map;
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
// Додати в ParkingController.java
@PostMapping("/spots")
public String addSpot(@RequestParam String number, @RequestParam Long lotId) {
    service.createSpot(number, lotId);
    return "Паркомісце " + number + " успішно додано до майданчика ID: " + lotId;
}
    // Сценарій 1 & 2
    @PostMapping("/book")
    public String book(@RequestParam Long spotId) {
        return service.processBooking(spotId);
    }

    @PostMapping("/validate")
    public String validate(@RequestParam String qrCode) {
        return service.validateQRCode(qrCode) ? "Доступ дозволено" : "QR невірний";
    }

    // Сценарій 3: Інфраструктура
    @PostMapping("/admin/lots")
    public String addLot(@RequestParam String name, @RequestParam Long ownerId) {
        return service.createLot(name, ownerId);
    }
// PATCH: Оновити статус або дані місця
@PatchMapping("/spots/{id}")
public String patchSpot(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
    service.updateSpotPartial(id, updates);
    return "Місце оновлено (PATCH успішний)";
}

// DELETE: Видалити паркомісце
@DeleteMapping("/spots/{id}")
public String deleteSpot(@PathVariable Long id) {
    service.deleteSpot(id);
    return "Паркомісце успішно видалено";
}

// DELETE: Видалити майданчик
@DeleteMapping("/admin/lots/{id}")
public String deleteLot(@PathVariable Long id) {
    service.deleteParkingLot(id);
    return "Паркувальний майданчик та всі його місця видалено";
}
    // Сценарій: Фільтрація виручки
    @GetMapping("/revenue")
    public String getRevenue(@RequestParam Long lotId, @RequestParam(required = false) String date) {
        double amount = service.getFilteredRevenue(lotId, date);
        return "Виручка (відфільтровано): " + amount + " грн";
    }
}