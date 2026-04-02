package ua.smartparking.domain;
import jakarta.persistence.*;
import java.util.List;

@Entity
public class Owner {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<ParkingLot> parkingLots;

    public Owner() {}
    public Owner(String name) { this.name = name; }
    public Long getId() { return id; }
}