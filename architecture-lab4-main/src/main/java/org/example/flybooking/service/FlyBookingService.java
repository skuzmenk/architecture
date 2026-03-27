package org.example.flybooking.service;

import org.example.account.service.AccountService;
import org.example.account.service.exception.AccountException;
import org.example.account.service.exception.AccountNotFoundException;
import org.example.flybooking.repository.FlyBooking;
import org.example.flybooking.repository.FlyBookingRepository;
import org.example.flybooking.service.exception.FlyBookingException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FlyBookingService {

    @Autowired
    private FlyBookingRepository flyBookingRepository;

    @Autowired
    private AccountService accountService;

    @Transactional
    public void bookFly(Long accountId, Double price, FlyBooking flyBooking) throws FlyBookingException {
        try {
            accountService.debit(accountId, price);
        } catch (AccountNotFoundException e) {
            throw new FlyBookingException(e.getMessage());
        } catch (AccountException e) {
            throw new FlyBookingException(e.getMessage());
        }
        flyBookingRepository.save(flyBooking);
    }

    public List<FlyBooking> getFlyBookings() {
        List<FlyBooking> result = new ArrayList<>();
        for (FlyBooking flyBooking : flyBookingRepository.findAll()) {
            result.add(flyBooking);
        }

        return result;
    }
}
