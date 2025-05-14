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

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthApiIntegrationTest {

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

    @Autowired MockMvc mockMvc;
    @Autowired UserRepository userRepository;
    @Autowired EmailDataRepository emailRepository;
    @Autowired AccountRepository accountRepository;

    private final String testEmail = "login_test@example.com";
    private final String testPassword = "Test-password";

    @BeforeEach
    void setup() {
        userRepository.deleteAll();

        User user = new User();
        user.setName("Integration Test User");
        user.setPassword(testPassword);
        user.setDateOfBirth(LocalDate.of(1990, 1, 1));
        user = userRepository.save(user);

        EmailData email = new EmailData();
        email.setUser(user);
        email.setEmail(testEmail);
        emailRepository.save(email);

        Account account = new Account();
        account.setUser(user);
        account.setInitialBalance(BigDecimal.valueOf(500.00));
        account.setCurrentBalance(BigDecimal.valueOf(500.00));
        accountRepository.save(account);
    }

    @Test
    @Order(1)
    void shouldLoginWithCorrectCredentials() throws Exception {
        String payload = """
                {
                  "email": "%s",
                  "password": "%s"
                }
                """.formatted(testEmail, testPassword);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    @Order(2)
    void shouldFailLoginWithWrongPassword() throws Exception {
        String payload = """
                {
                  "email": "%s",
                  "password": "wrong-password"
                }
                """.formatted(testEmail);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest());
    }
}
