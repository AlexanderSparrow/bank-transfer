package ru.pionerpixel.banktransfer.model;

import jakarta.persistence.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@Table(name = "email_data")
public class EmailData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 200, nullable = false, unique = true)
    private String email;
}
