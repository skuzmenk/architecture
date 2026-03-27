package org.example.flybooking.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.Instant;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FlyBookingDto {
    private Long id;
    private String clientName;
    private String flyNumber;
    private String fromCity;
    private String toCity;
    private String fromDate;
    private Long accountId;
    private Double price;
}
