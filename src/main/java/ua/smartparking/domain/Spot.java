package ua.smartparking.domain;
import jakarta.persistence.*;

@Entity
public class Spot {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String spotNumber;
    @Enumerated(EnumType.STRING)
    private SpotStatus status = SpotStatus.FREE;

    @ManyToOne @JoinColumn(name = "parking_lot_id")
    private ParkingLot parkingLot;

    public Spot() {}
    public Spot(String spotNumber, ParkingLot parkingLot) {
        this.spotNumber = spotNumber;
        this.parkingLot = parkingLot;
    }
    public Long getId() { return id; }
    public SpotStatus getStatus() { return status; }
    public void setStatus(SpotStatus status) { this.status = status; }
}