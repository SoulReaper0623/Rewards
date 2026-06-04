package org.visa.Dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "A single reward ledger entry")
public class RewardResponse {

    @JsonProperty("reward_id")
    @Schema(description = "Unique reward entry ID", example = "1")
    public Long rewardId;

    @JsonProperty("member_id")
    @Schema(description = "ID of the member", example = "1")
    public Long memberId;

    @JsonProperty("point_type_id")
    @Schema(description = "Point type ID", example = "1")
    public Long pointsTypeId;

    @Schema(description = "Points value (positive for credits, negative for debits)", example = "500")
    public Long points;

    @Schema(description = "Description of the reward entry", example = "Purchase at Store A")
    public String description;

    @JsonProperty("event_date")
    @Schema(description = "Timestamp when the reward entry occurred", example = "2024-02-01T14:22:10Z")
    public OffsetDateTime eventDate;
}
