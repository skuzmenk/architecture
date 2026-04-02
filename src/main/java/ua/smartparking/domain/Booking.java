package ua.smartparking.domain;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long spotId;
    private String qrCode;
    private LocalDateTime time = LocalDateTime.now();

    public Booking() {}
    public Booking(Long spotId, String qrCode) {
        this.spotId = spotId;
        this.qrCode = qrCode;
    }
}