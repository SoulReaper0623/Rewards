package org.visa.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import org.visa.Dtos.ErrorResponse;
import org.visa.Dtos.RewardRequest;
import org.visa.Dtos.RewardResponse;
import org.visa.interfaces.RewardsService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Rewards", description = "Reward entry management")
public class RewardsController {

    public final RewardsService rewardsService;

    @Operation(summary = "Create a new reward entry",
            description = "Points must be positive. The sign is applied automatically based on point_type_id (1-3 = credit, 4 = debit).")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Reward entry created"),
            @ApiResponse(responseCode = "400", description = "Validation error or invalid point_type_id",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Member not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Insufficient balance for redemption",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/rewards")
    @ResponseStatus(HttpStatus.CREATED)
    public RewardResponse addNewReward(@Valid @RequestBody RewardRequest rewardRequest) {
        return rewardsService.addNewReward(rewardRequest);
    }

    @Operation(summary = "Get all reward entries for a member")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reward entries returned"),
            @ApiResponse(responseCode = "404", description = "Member not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/members/{memberId}/rewards")
    public List<RewardResponse> fetchRewardsForMember(@PathVariable Long memberId) {
        return rewardsService.fetchRewards(memberId);
    }
}
