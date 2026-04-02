package ua.smartparking.domain;
import jakarta.persistence.*;
import java.util.List;

@Entity
public class ParkingLot {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToOne @JoinColumn(name = "owner_id")
    private Owner owner;

    @OneToMany(mappedBy = "parkingLot", cascade = CascadeType.ALL)
    private List<Spot> spots;

    public ParkingLot() {}
    public ParkingLot(String name, Owner owner) {
        this.name = name;
        this.owner = owner;
    }
    public Long getId() { return id; }
    public String getName() { return name; }
}