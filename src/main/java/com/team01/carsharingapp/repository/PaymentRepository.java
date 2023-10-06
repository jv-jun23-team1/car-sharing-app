package com.team01.carsharingapp.repository;

import com.team01.carsharingapp.model.Payment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface PaymentRepository extends JpaRepository<Payment, Long>,
        JpaSpecificationExecutor<Payment> {
    @Query("SELECT p FROM Payment p JOIN FETCH p.rental r Join FETCH r.user u where u.id = :userId")
    List<Payment> findAllByUserId(Long userId);

    @Query("SELECT p FROM Payment p JOIN FETCH p.rental r where r.id = :rentalId")
    Payment findByRentalId(Long rentalId);

    Optional<Payment> findBySessionId(String sessionId);
}
