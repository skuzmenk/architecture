package ua.smartparking.domain;

import java.time.LocalDateTime;

public class Booking {
    private Long id;
    private Long spotId;
    private String qrCodeData;
    private LocalDateTime startTime;

    public Booking(Long id, Long spotId, String qrCodeData) {
        this.id = id;
        this.spotId = spotId;
        this.qrCodeData = qrCodeData;
        this.startTime = LocalDateTime.now();
    }

    public String getQrCodeData() { return qrCodeData; }
}