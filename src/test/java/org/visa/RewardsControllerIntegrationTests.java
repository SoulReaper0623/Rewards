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


@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
public class RewardsControllerIntegrationTests {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
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

    private long memberId;

    @BeforeEach
    void setUp() throws Exception {
        rewardRepository.deleteAll();
        memberRepository.deleteAll();
        memberId = createMember("test@example.com");
    }

    @Test
    void createReward_credit_returns201WithPositivePoints() throws Exception {
        mockMvc.perform(post("/rewards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "member_id": %d,
                                    "point_type_id": 1,
                                    "points": 500,
                                    "description": "Purchase at Store A"
                                }
                                """.formatted(memberId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.points").value(500))
                .andExpect(jsonPath("$.point_type_id").value(1))
                .andExpect(jsonPath("$.member_id").value(memberId));
    }

    @Test
    void createReward_redemption_returns201WithNegativePoints() throws Exception {
        // First credit some points
        mockMvc.perform(post("/rewards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"member_id": %d, "point_type_id": 1, "points": 500}
                                """.formatted(memberId)))
                .andExpect(status().isCreated());

        // Then redeem
        mockMvc.perform(post("/rewards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "member_id": %d,
                                    "point_type_id": 4,
                                    "points": 300,
                                    "description": "Redeem voucher"
                                }
                                """.formatted(memberId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.points").value(-300));
    }

    @Test
    void createReward_redemption_insufficientBalance_returns422() throws Exception {
        mockMvc.perform(post("/rewards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "member_id": %d,
                                    "point_type_id": 4,
                                    "points": 1000,
                                    "description": "Redeem with no balance"
                                }
                                """.formatted(memberId)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("INSUFFICIENT_BALANCE"));
    }

    @Test
    void createReward_invalidPointType_returns400() throws Exception {
        mockMvc.perform(post("/rewards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"member_id": %d, "point_type_id": 99, "points": 100}
                                """.formatted(memberId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("INVALID_POINT_TYPE"));
    }

    @Test
    void createReward_memberNotFound_returns404() throws Exception {
        mockMvc.perform(post("/rewards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"member_id": 9999, "point_type_id": 1, "points": 100}
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("MEMBER_NOT_FOUND"));
    }

    @Test
    void getRewardsForMember_returnsAllEntries() throws Exception {
        mockMvc.perform(post("/rewards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"member_id": %d, "point_type_id": 1, "points": 500, "description": "Purchase"}
                                """.formatted(memberId)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/rewards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"member_id": %d, "point_type_id": 2, "points": 200, "description": "Referral"}
                                """.formatted(memberId)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/members/" + memberId + "/rewards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].points").value(500))
                .andExpect(jsonPath("$[1].points").value(200));
    }

    @Test
    void getRewardsForMember_memberNotFound_returns404() throws Exception {
        mockMvc.perform(get("/members/9999/rewards"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("MEMBER_NOT_FOUND"));
    }

    @Test
    void getMember_balanceReflectsAllEntries() throws Exception {
        mockMvc.perform(post("/rewards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"member_id": %d, "point_type_id": 1, "points": 500}
                                """.formatted(memberId)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/rewards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"member_id": %d, "point_type_id": 4, "points": 200}
                                """.formatted(memberId)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/members/" + memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.points_balance").value(300));
    }

    private long createMember(String email) throws Exception {
        String response = mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Test User", "email": "%s"}
                                """.formatted(email)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) com.jayway.jsonpath.JsonPath.read(response, "$.member_id")).longValue();
    }
}

