package org.visa.Dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Request body for creating a new reward entry")
public class RewardRequest {

    @NotNull(message = "member_id must not be null")
    @JsonProperty("member_id")
    @Schema(description = "ID of the member", example = "1")
    public Long memberId;

    @NotNull(message = "point_type_id must not be null")
    @JsonProperty("point_type_id")
    @Schema(description = "Point type: 1=Purchase Earning, 2=Referral Bonus, 3=Cashback, 4=Redemption", example = "1", allowableValues = {"1", "2", "3", "4"})
    public Long pointTypeId;

    @NotNull(message = "points must not be null")
    @Positive(message = "points must be a positive number")
    @Schema(description = "Number of points (always positive; sign applied automatically by type)", example = "500")
    public Long points;

    @Schema(description = "Optional description of the reward entry", example = "Purchase at Store A")
    public String description;
}
