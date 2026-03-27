package org.example.flybooking.api;

import org.example.flybooking.api.dto.FlyBookingDto;
import org.example.flybooking.repository.FlyBooking;
import org.example.flybooking.service.FlyBookingService;
import org.example.flybooking.service.exception.FlyBookingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("fly-bookings")
public class FlyRestApiController {

    @Autowired
    private FlyBookingService flyBookingService;

    @PostMapping(produces = "application/json")
    public @ResponseBody
    ResponseEntity bookFly(@RequestBody FlyBookingDto flyBookingDto) {

        FlyBooking flyBooking = FlyBooking.builder()
                .flyNumber(flyBookingDto.getFlyNumber())
                .clientName(flyBookingDto.getClientName())
                .fromCity(flyBookingDto.getFromCity())
                .toCity(flyBookingDto.getToCity())
                .fromDate(flyBookingDto.getFromDate())
                .build();

        try {
            flyBookingService.bookFly(flyBookingDto.getAccountId(), flyBookingDto.getPrice(), flyBooking);
            return new ResponseEntity(HttpStatus.CREATED);
        } catch (FlyBookingException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(produces = "application/json")
    public @ResponseBody
    List<FlyBookingDto> getFlies() {
        return flyBookingService.getFlyBookings().stream()
                .map(in -> FlyBookingDto.builder()
                        .id(in.getId())
                        .clientName(in.getClientName())
                        .flyNumber(in.getFlyNumber())
                        .fromCity(in.getFromCity())
                        .toCity(in.getToCity())
                        .fromDate(in.getFromDate().toString())
                        .build())
                .collect(Collectors.toList());
    }
}
