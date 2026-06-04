package org.visa.Dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.OffsetDateTime;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Schema(description = "Member details including current points balance")
public class MemberResponse {

    @JsonProperty("member_id")
    @Schema(description = "Unique member ID", example = "1")
    Long memberId;

    @Schema(description = "Full name of the member", example = "Alice Johnson")
    String name;

    @Schema(description = "Email address", example = "alice@example.com")
    String email;

    @JsonProperty("points_balance")
    @Schema(description = "Current points balance (sum of all reward entries)", example = "450")
    Long pointsBalance;

    @JsonProperty("created_at")
    @Schema(description = "Account creation timestamp", example = "2024-01-15T09:00:00Z")
    OffsetDateTime createdAt;
}
