package org.example.flybooking.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlyBookingRepository extends CrudRepository<FlyBooking, Long> {
}
