package ru.pionerpixel.banktransfer.model;

import jakarta.persistence.*;

@Entity
@Table(name = "phone_data")
public class PhoneData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(length = 13, nullable = false, unique = true)
    private String phone;
}
