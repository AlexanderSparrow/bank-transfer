package ru.pionerpixel.banktransfer.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter @Setter
@Table(name = "account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "initial_balance", precision = 19, scale = 2, nullable = false)
    private BigDecimal initialBalance;

    @Column(name = "current_balance", precision = 19, scale = 2, nullable = false)
    private BigDecimal currentBalance;
}
