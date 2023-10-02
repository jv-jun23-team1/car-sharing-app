package com.team01.carsharingapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Data
@Entity
@SQLDelete(sql = "UPDATE Payment SET is_deleted = true WHERE id=?")
@Where(clause = "is_deleted=false")
@Table(name = "payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @Column(name = "rental_id")
    private Rental rental;
    @Column(name = "session_url")
    private String urlSession;
    @Column(name = "session_id")
    private Long sessionId;
    @Column(name = "price")
    private BigDecimal price;
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;
}
