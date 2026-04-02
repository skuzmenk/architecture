package ua.smartparking.application;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ua.smartparking.data.BookingRepository;
import ua.smartparking.data.ParkingRepository;
import ua.smartparking.domain.Booking;
import ua.smartparking.domain.BookingStatus;
import ua.smartparking.domain.PaymentMethod;
import ua.smartparking.domain.Spot;
import ua.smartparking.domain.SpotStatus;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ParkingService {
    private final ParkingRepository repository;
    private final BookingRepository bookingRepository;

    public ParkingService(ParkingRepository repository, BookingRepository bookingRepository) {
        this.repository = repository;
        this.bookingRepository = bookingRepository;
    }

    // Сценарій 1: Бронювання місця + видача QR
    public Booking createBooking(Long spotId, Double amount, PaymentMethod paymentMethod) {
        Spot spot = repository.findById(spotId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Місце не знайдено"));

        if (!spot.isFree()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Місце вже зайняте");
        }

        spot.setStatus(SpotStatus.RESERVED);
        repository.save(spot);

        Booking booking = new Booking(nextBookingId(), spotId, amount, paymentMethod);
        return bookingRepository.save(booking);
    }

    // Сценарій 1: Перегляд карти/фільтрація місць
    public List<Spot> getSpots(String address, SpotStatus status) {
        if (address != null && !address.isBlank() && status != null) {
            return repository.findByAddressContainingIgnoreCaseAndStatus(address, status);
        }
        if (address != null && !address.isBlank()) {
            return repository.findByAddressContainingIgnoreCase(address);
        }
        if (status != null) {
            return repository.findByStatus(status);
        }
        return repository.findAll();
    }

    // Сценарій 2: Використання парковки, скан QR
    public Booking scanQr(String qrCode) {
        Booking booking = bookingRepository.findByQrCodeData(qrCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "QR-код не знайдено"));

        Spot spot = repository.findById(booking.getSpotId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Місце для бронювання не знайдено"));

        if (booking.getStatus() == BookingStatus.CANCELLED || booking.getStatus() == BookingStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Бронювання вже завершене або скасоване");
        }

        booking.setStatus(BookingStatus.IN_USE);
        spot.setStatus(SpotStatus.OCCUPIED);
        repository.save(spot);
        return bookingRepository.save(booking);
    }

    // Сценарій 2: Нарахування бонусів
    public Booking addBonus(Long bookingId, Integer points) {
        Booking booking = getBookingOrThrow(bookingId);
        booking.addBonusPoints(points);
        return bookingRepository.save(booking);
    }

    // Сценарій 2: Обробка порушення
    public Booking addViolation(Long bookingId, String reason) {
        Booking booking = getBookingOrThrow(bookingId);
        booking.setViolationNote(reason);
        return bookingRepository.save(booking);
    }

    // Сценарій 3: IoT/операційні оновлення статусу
    public void updateFromSensor(Long spotId, SpotStatus status) {
        repository.findById(spotId).ifPresent(spot -> {
            spot.setStatus(status);
            repository.save(spot);
        });
    }

    // Сценарій 3: Моніторинг зайнятості
    public OccupancyStats getOccupancyStats() {
        long total = repository.count();
        long occupied = repository.findByStatus(SpotStatus.OCCUPIED).size();
        long reserved = repository.findByStatus(SpotStatus.RESERVED).size();
        long free = repository.findByStatus(SpotStatus.FREE).size();
        return new OccupancyStats(total, free, occupied, reserved);
    }

    // Сценарій 3: Валідація QR-коду
    public ValidationResult validateQr(String qrCode) {
        return bookingRepository.findByQrCodeData(qrCode)
                .map(b -> new ValidationResult(true, "QR валідний", b.getStatus().name()))
                .orElseGet(() -> new ValidationResult(false, "QR не знайдено", null));
    }

    // Сценарій 3: Оплата готівкою (PATCH)
    public Booking setPaymentMethod(Long bookingId, PaymentMethod paymentMethod) {
        Booking booking = getBookingOrThrow(bookingId);
        booking.setPaymentMethod(paymentMethod);
        return bookingRepository.save(booking);
    }

    // Сценарій 4: Створення паркомісця
    public Spot createSpot(Spot spot) {
        if (repository.existsById(spot.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Місце з таким ID вже існує");
        }
        return repository.save(spot);
    }

    // Сценарій 4: Оновлення паркомісця (PUT)
    public Spot replaceSpot(Long id, Spot updated) {
        Spot spot = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Місце не знайдено"));
        spot.setAddress(updated.getAddress());
        spot.setStatus(updated.getStatus());
        return repository.save(spot);
    }

    // Сценарій 4: Видалення паркомісця (DELETE)
    public void deleteSpot(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Місце не знайдено");
        }
        repository.deleteById(id);
    }

    // Сценарій 5: Перегляд виручки з фільтрами
    public RevenueSummary getRevenue(String role, LocalDateTime from, LocalDateTime to, String address) {
        validateOwnerRole(role);
        List<Booking> bookings = bookingRepository.findAll();

        double total = 0.0;
        long count = 0;
        for (Booking booking : bookings) {
            if (booking.getPaidAmount() == null) {
                continue;
            }

            LocalDateTime started = booking.getStartTime();
            if (from != null && started.isBefore(from)) {
                continue;
            }
            if (to != null && started.isAfter(to)) {
                continue;
            }

            if (address != null && !address.isBlank()) {
                Spot spot = repository.findById(booking.getSpotId()).orElse(null);
                if (spot == null || !spot.getAddress().toLowerCase().contains(address.toLowerCase())) {
                    continue;
                }
            }

            total += booking.getPaidAmount();
            count++;
        }

        return new RevenueSummary(total, count, from, to, address);
    }

    // Сценарій 5: Деталізований фінзвіт
    public String getRevenueReport(String role) {
        validateOwnerRole(role);
        RevenueSummary summary = getRevenue(role, null, null, null);
        return "Звіт по виручці: транзакцій=" + summary.transactions()
                + ", сума=" + summary.totalAmount() + " грн";
    }

    public Booking completeBooking(Long bookingId) {
        Booking booking = getBookingOrThrow(bookingId);
        booking.setStatus(BookingStatus.COMPLETED);
        booking.setEndTime(LocalDateTime.now());

        repository.findById(booking.getSpotId()).ifPresent(spot -> {
            spot.setStatus(SpotStatus.FREE);
            repository.save(spot);
        });

        return bookingRepository.save(booking);
    }

    private Booking getBookingOrThrow(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Бронювання не знайдено"));
    }

    private void validateOwnerRole(String role) {
        if (!"OWNER".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Немає доступу");
        }
    }

    private long nextBookingId() {
        return bookingRepository.count() + 1;
    }

    public record OccupancyStats(long total, long free, long occupied, long reserved) {
    }

    public record ValidationResult(boolean valid, String message, String bookingStatus) {
    }

    public record RevenueSummary(double totalAmount,
                                 long transactions,
                                 LocalDateTime from,
                                 LocalDateTime to,
                                 String addressFilter) {
    }
}