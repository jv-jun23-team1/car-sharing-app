package com.team01.carsharingapp.repository;

import com.team01.carsharingapp.model.Rental;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RentalRepository extends JpaRepository<Rental, Long> {
    @Query("""
           FROM Rental r
           LEFT JOIN FETCH r.user u
           LEFT JOIN FETCH r.car c
           WHERE u.id = :userId
           AND r.id = :id
            """)
    Optional<Rental> findByIdAndUserId(Long id, Long userId);

    @Query("""
           FROM Rental r
           LEFT JOIN FETCH r.user u
           LEFT JOIN FETCH r.car c
           WHERE (:isActive = true AND r.actualReturnDate IS NULL)
           OR (:isActive = false AND r.actualReturnDate IS NOT NULL)
            """)
    List<Rental> findAllByStatus(boolean isActive, Pageable pageable);

    @Query("""
           FROM Rental r
           LEFT JOIN FETCH r.user u
           LEFT JOIN FETCH r.car c
           WHERE u.id = :userId
           AND ((:isActive = true AND r.actualReturnDate IS NULL)
           OR (:isActive = false AND r.actualReturnDate IS NOT NULL))
            """)
    List<Rental> findAllByUserIdAndStatus(Long userId, boolean isActive, Pageable pageable);

    @Query(""" 
           FROM Rental r
           LEFT JOIN FETCH r.car c
           LEFT JOIN FETCH r.user u
           WHERE r.actualReturnDate IS NULL
           AND r.rentalDate > :date
               """)
    List<Rental> findAllOverdue(LocalDate date);
}
