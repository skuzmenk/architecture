package ua.smartparking.data;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.smartparking.domain.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    boolean existsByQrCode(String qrCode); // Для валідації
}