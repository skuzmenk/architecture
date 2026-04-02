package ua.smartparking.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;

@Entity
public class Spot {
    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    private SpotStatus status;

    private String address;

    protected Spot() {
    }

    public Spot(Long id, String address, SpotStatus status) {
        this.id = id;
        this.address = address;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public SpotStatus getStatus() { return status; }
    public void setStatus(SpotStatus status) { this.status = status; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public boolean isFree() { return this.status == SpotStatus.FREE; }
}