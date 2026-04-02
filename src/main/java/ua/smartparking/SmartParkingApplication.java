package ua.smartparking;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ua.smartparking.data.ParkingRepository;
import ua.smartparking.domain.Spot;
import ua.smartparking.domain.SpotStatus;

@SpringBootApplication
public class SmartParkingApplication {
    public static void main(String[] args) {
        SpringApplication.run(SmartParkingApplication.class, args);
    }

    @Bean
    CommandLineRunner seedParkingData(ParkingRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.save(new Spot(101L, "вул. Шевченка, 1", SpotStatus.FREE));
                repository.save(new Spot(102L, "вул. Шевченка, 1", SpotStatus.OCCUPIED));
            }
        };
    }
}