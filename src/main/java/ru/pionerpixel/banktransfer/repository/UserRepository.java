package ru.pionerpixel.banktransfer.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.pionerpixel.banktransfer.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmails_Email(String email);
    Optional<User> findByPhones_Phone(String phone);

    // Для фильтрации
    List<User> findByNameStartingWith(String name, Pageable pageable);
    List<User> findByDateOfBirthAfter(LocalDate dateOfBirth, Pageable pageable);
}
