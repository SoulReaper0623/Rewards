package org.visa.Dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@AllArgsConstructor
@Schema(description = "Response after creating a new member")
public class MemberCreateResponse {

    @JsonProperty("member_id")
    @Schema(description = "Unique member ID", example = "1")
    private Long memberId;

    @Schema(description = "Full name of the member", example = "Alice Johnson")
    private String name;

    @Schema(description = "Email address", example = "alice@example.com")
    private String email;

    @JsonProperty("created_at")
    @Schema(description = "Account creation timestamp", example = "2024-01-15T09:00:00Z")
    private OffsetDateTime createdAt;
}
