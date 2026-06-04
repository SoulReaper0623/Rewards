package org.visa;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.visa.repository.MembersRepository;
import org.visa.repository.RewardsRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = RewardSystem.class)
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
public class MemberControllerIntegrationTests {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("rewards_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;
    @Autowired private RewardsRepository rewardRepository;
    @Autowired private MembersRepository memberRepository;

    @BeforeEach
    void cleanDatabase() {
        rewardRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    void createMember_success_returns201() throws Exception {
        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Alice Johnson",
                                    "email": "alice@example.com"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Alice Johnson"))
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andExpect(jsonPath("$.member_id").isNumber())
                .andExpect(jsonPath("$.created_at").isString())
                .andExpect(jsonPath("$.points_balance").doesNotExist())
                .andExpect(jsonPath("$.updated_at").doesNotExist());
    }

    @Test
    void createMember_duplicateEmail_returns409() throws Exception {
        String body = """
                {
                    "name": "Alice Johnson",
                    "email": "dup@example.com"
                }
                """;

        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("DUPLICATE_EMAIL"));
    }

    @Test
    void createMember_missingName_returns400() throws Exception {
        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "alice@example.com"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    @Test
    void getMember_success_returnsWithBalance() throws Exception {
        String createResponse = mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Bob",
                                    "email": "bob@example.com"
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        // extract member_id from response
        long memberId = ((Number) com.jayway.jsonpath.JsonPath.read(createResponse, "$.member_id")).longValue();

        mockMvc.perform(get("/members/" + memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.member_id").value(memberId))
                .andExpect(jsonPath("$.points_balance").value(0));
    }

    @Test
    void getMember_notFound_returns404() throws Exception {
        mockMvc.perform(get("/members/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("MEMBER_NOT_FOUND"));
    }
}
