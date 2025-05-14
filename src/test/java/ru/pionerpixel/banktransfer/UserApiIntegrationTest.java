package ru.pionerpixel.banktransfer;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.pionerpixel.banktransfer.model.Account;
import ru.pionerpixel.banktransfer.model.EmailData;
import ru.pionerpixel.banktransfer.model.User;
import ru.pionerpixel.banktransfer.repository.AccountRepository;
import ru.pionerpixel.banktransfer.repository.EmailDataRepository;
import ru.pionerpixel.banktransfer.repository.UserRepository;
import ru.pionerpixel.banktransfer.security.JwtTokenUtil;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserApiIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("bt-db")
            .withUsername("user")
            .withPassword("secret");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
    }

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    UserRepository userRepository;

    @Autowired
    EmailDataRepository emailRepository;

    @Autowired
    AccountRepository accountRepository;

    private static Long testUserId;

    private final String testEmail = "integration@example.com";

    private String bearerToken() {
        return "Bearer " + jwtTokenUtil.generateToken(testUserId);
    }

    @BeforeEach
    void setupUser() {
        userRepository.deleteAll();

        User user = new User();
        user.setName("Test User");
        user.setPassword("Test-password");
        user.setDateOfBirth(LocalDate.of(1990, 1, 1));
        user = userRepository.save(user);
        testUserId = user.getId();

        Account account = new Account();
        account.setUser(user);
        account.setInitialBalance(new BigDecimal("1000.00"));
        account.setCurrentBalance(new BigDecimal("1000.00"));
        accountRepository.save(account);
    }

    @Test
    @Order(1)
    void shouldAddEmailSuccessfully() throws Exception {
        String json = """
                {
                  "email": "%s"
                }
                """.formatted(testEmail);

        mockMvc.perform(post("/users/email")
                        .header("Authorization", bearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    @Order(2)
    void shouldGetCurrentUser() throws Exception {
        // Добавим email заранее
        EmailData email = new EmailData();
        email.setUser(userRepository.findById(testUserId).orElseThrow());
        email.setEmail(testEmail);
        emailRepository.save(email);

        mockMvc.perform(get("/users/me")
                        .header("Authorization", bearerToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    @Order(3)
    void shouldDeleteEmail() throws Exception {
        EmailData email = new EmailData();
        email.setUser(userRepository.findById(testUserId).orElseThrow());
        email.setEmail(testEmail);
        emailRepository.save(email);

        String json = """
                {
                  "email": "%s"
                }
                """.formatted(testEmail);

        mockMvc.perform(delete("/users/email")
                        .header("Authorization", bearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }
}
