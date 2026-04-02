package ua.smartparking.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.smartparking.data.*;
import ua.smartparking.domain.*;
import java.util.*;

@Service
public class ParkingService {
    private final ParkingRepository spotRepo;
    private final ParkingLotRepository lotRepo;
    private final OwnerRepository ownerRepo;
    private final BookingRepository bookingRepo; // Додано репозиторій для бронювань

    public ParkingService(ParkingRepository spotRepo, 
                          ParkingLotRepository lotRepo, 
                          OwnerRepository ownerRepo,
                          BookingRepository bookingRepo) {
        this.spotRepo = spotRepo;
        this.lotRepo = lotRepo;
        this.ownerRepo = ownerRepo;
        this.bookingRepo = bookingRepo;
    }

    // Сценарій 1: Бронювання (Оплата + QR + Збереження в БД)
    @Transactional
    public String processBooking(Long spotId) {
        Spot spot = spotRepo.findById(spotId)
                .orElseThrow(() -> new RuntimeException("Місце не знайдено"));
        
        if (spot.getStatus() != SpotStatus.FREE) {
            return "Помилка: Місце вже зайняте";
        }

        // Імітація <<include>> Оплатити послугу
        System.out.println("Оплата проведена успішно...");

        // Генеруємо унікальний QR
        String generatedQr = "QR_" + UUID.randomUUID().toString().substring(0, 8);
        
        // ЗБЕРІГАЄМО ЗАПИС У ТАБЛИЦЮ BOOKING
        Booking booking = new Booking(spotId, generatedQr);
        bookingRepo.save(booking); 

        // Оновлюємо статус місця
        spot.setStatus(SpotStatus.RESERVED);
        spotRepo.save(spot);
        
        return generatedQr;
    }

    // Сценарій 2: Використання (Справжня валідація через базу)
   public boolean validateQRCode(String code) {
    // Тепер ми йдемо в базу даних
    boolean exists = bookingRepo.existsByQrCode(code);
    
    if (exists) {
        System.out.println("Валідація успішна для коду: " + code);
    } else {
        System.out.println("Код не знайдено в базі: " + code);
    }
    
    return exists;
}
// Додати в ParkingService.java
@Transactional
public Spot createSpot(String spotNumber, Long lotId) {
    ParkingLot lot = lotRepo.findById(lotId)
            .orElseThrow(() -> new RuntimeException("Паркувальний майданчик не знайдено"));
    
    Spot spot = new Spot(spotNumber, lot);
    return spotRepo.save(spot);
}
    // Сценарій 3: Керування інфраструктурою (Перевірка дублікатів)
    @Transactional
    public String createLot(String name, Long ownerId) {
        if (lotRepo.existsByName(name)) {
            return "Помилка: Такий майданчик вже існує!";
        }
        Owner owner = ownerRepo.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Власника не знайдено"));
        
        ParkingLot lot = new ParkingLot(name, owner);
        lotRepo.save(lot);
        return "Майданчик '" + name + "' успішно створено";
    }

    @Transactional
    public Spot updateSpotPartial(Long id, Map<String, Object> updates) {
        Spot spot = spotRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Місце не знайдено"));

        updates.forEach((key, value) -> {
            switch (key) {
                case "status" -> spot.setStatus(SpotStatus.valueOf((String) value));
                case "spotNumber" -> System.out.println("Номер місця змінено на: " + value);
            }
        });
        return spotRepo.save(spot);
    }

    @Transactional
    public void deleteSpot(Long id) {
        if (!spotRepo.existsById(id)) {
            throw new RuntimeException("Місце з ID " + id + " не існує");
        }
        spotRepo.deleteById(id);
    }

    @Transactional
    public void deleteParkingLot(Long id) {
        lotRepo.deleteById(id);
    }

    public double getFilteredRevenue(Long lotId, String date) {
        System.out.println("Фільтрація за майданчиком: " + lotId + " на дату: " + date);
        return 1500.0; 
    }
}