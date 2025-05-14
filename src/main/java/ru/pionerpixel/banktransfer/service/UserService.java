package ru.pionerpixel.banktransfer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pionerpixel.banktransfer.dto.EmailDto;
import ru.pionerpixel.banktransfer.dto.PhoneDto;
import ru.pionerpixel.banktransfer.dto.UserDto;
import ru.pionerpixel.banktransfer.dto.UserSearchRequest;
import ru.pionerpixel.banktransfer.exception.AppException;
import ru.pionerpixel.banktransfer.mapper.UserMapper;
import ru.pionerpixel.banktransfer.model.EmailData;
import ru.pionerpixel.banktransfer.model.PhoneData;
import ru.pionerpixel.banktransfer.model.User;
import ru.pionerpixel.banktransfer.repository.EmailDataRepository;
import ru.pionerpixel.banktransfer.repository.PhoneDataRepository;
import ru.pionerpixel.banktransfer.repository.UserRepository;
import ru.pionerpixel.banktransfer.specification.UserSpecification;
import ru.pionerpixel.banktransfer.utils.AuthUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AuthUtils authUtils;
    private final EmailDataRepository emailDataRepository;
    private final PhoneDataRepository phoneDataRepository;
    private final UserMapper userMapper;

    /**
     * Получение пользователя по ID
     *
     * @param userId - ID пользователя
     * @return - объект {@link User}
     */
    @Cacheable(value = "userById", key = "#userId")
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new AppException("Пользователь не найден", HttpStatus.NOT_FOUND));
    }

    /**
     * Получение информации о текущем пользователе
     *
     * @param userId - ID пользователя
     * @return - объект {@link UserDto}
     */
    @Cacheable(value = "getMe", key = "#userId")
    public UserDto getMe(Long userId) {
        User user = getUserById(userId);
        if (user == null) {
            throw new AppException("Пользователь не найден", HttpStatus.NOT_FOUND);
        }
        return userMapper.toDto(user);
    }

    @Transactional
    public void addEmail(EmailDto emailDto) {
        if (emailDataRepository.existsByEmail(emailDto.getEmail())) {
            throw new AppException("Данный email уже существует", HttpStatus.CONFLICT);
        }
        User user = getCurrentUser();

        EmailData email = new EmailData();
        email.setEmail(emailDto.getEmail());
        email.setUser(user);
        user.getEmails().add(email);
        emailDataRepository.save(email);
        userRepository.save(user);
    }

    @Transactional
    public void deleteEmail(EmailDto dto) {
        User user = getCurrentUser();

        List<EmailData> emails = user.getEmails();
        if (emails.size() <= 1) {
            throw new IllegalArgumentException("У пользователя должен быть хотя бы один email");
        }
        EmailData toRemove = emails.stream()
                .filter(email -> email.getEmail().equals(dto.getEmail()))
                .findFirst()
                .orElseThrow(() -> new AppException("Такой email не существует", HttpStatus.NOT_FOUND));


        emails.remove(toRemove);
        emailDataRepository.delete(toRemove);
    }

    @Transactional
    public void addPhone(PhoneDto phoneDto) {
        if (phoneDataRepository.existsByPhone(phoneDto.getPhone())) {
            throw new AppException("Данный телефонный номер занят", HttpStatus.CONFLICT);

        }
        User user = getCurrentUser();
        PhoneData phone = new PhoneData();
        phone.setPhone(phoneDto.getPhone());
        phone.setUser(user);
        user.getPhones().add(phone);
        phoneDataRepository.save(phone);
        userRepository.save(user);
    }

    @Transactional
    public void deletePhone(PhoneDto dto) {
        User user = getCurrentUser();
        List<PhoneData> phones = user.getPhones();
        if (phones.size() <= 1) {
            throw new AppException("У пользователя должен быть указан хотя бы один телефонный номер", HttpStatus.FORBIDDEN);
        }
        PhoneData toRemove = phones.stream()
                .filter(phone -> phone.getPhone().equals(dto.getPhone()))
                .findFirst()
                .orElseThrow(() -> new AppException("Такой номер не существует", HttpStatus.NOT_FOUND));

        phones.remove(toRemove);
        phoneDataRepository.delete(toRemove);
    }

    private User getCurrentUser() {
        Long userId = authUtils.getCurrentUserId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new AppException("Пользователь не найден", HttpStatus.NOT_FOUND));
    }

    @Cacheable(value = "UserSearch", key = "{" +
            "#request.name, " +
            "#request.email, " +
            "#request.phone, " +
            "#request.dateOfBirthAfter, " +
            "#request.page, " +
            "#request.size}"
    )
    public Page<UserDto> search(UserSearchRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Specification<User> spec = UserSpecification.build(request);

        return userRepository.findAll(spec, pageable).map(userMapper::toDto);
    }
}


