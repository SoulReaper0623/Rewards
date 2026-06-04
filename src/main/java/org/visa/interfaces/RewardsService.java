package org.visa.interfaces;

import org.visa.Dtos.MemberRequest;
import org.visa.Dtos.MemberResponse;
import org.visa.Dtos.RewardRequest;
import org.visa.Dtos.RewardResponse;

import java.util.List;

public interface RewardsService {
    RewardResponse addNewReward(RewardRequest memberRequest);

    List<RewardResponse> fetchRewards(Long memberId);
}
