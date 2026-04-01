package ua.smartparking.domain;

public class Spot {
    private Long id;
    private SpotStatus status;
    private String address;

    public Spot(Long id, String address, SpotStatus status) {
        this.id = id;
        this.address = address;
        this.status = status;
    }

    // Геттери та сеттери
    public Long getId() { return id; }
    public SpotStatus getStatus() { return status; }
    public void setStatus(SpotStatus status) { this.status = status; }
    public String getAddress() { return address; }
    public boolean isFree() { return this.status == SpotStatus.FREE; }
}