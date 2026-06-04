package org.visa.services;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.visa.Dtos.RewardRequest;
import org.visa.Dtos.RewardResponse;
import org.visa.entities.Member;
import org.visa.entities.PointTypes;
import org.visa.entities.Rewards;
import org.visa.entities.TransactionType;
import org.visa.exceptions.BalanceNotSufficientException;
import org.visa.exceptions.MemberNotFoundException;
import org.visa.exceptions.PointTypeInvalidException;
import org.visa.interfaces.RewardsService;
import org.springframework.transaction.annotation.Transactional;
import org.visa.repository.MembersRepository;
import org.visa.repository.PointTypeRepository;
import org.visa.repository.RewardsRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RewardsServiceImpl implements RewardsService {

    private final RewardsRepository rewardsRepository;
    private final MembersRepository memberRepository;
    private final PointTypeRepository pointTypeRepository;

    @Override
    @Transactional
    public RewardResponse addNewReward(@NotNull RewardRequest rewardRequest) {
        PointTypes pointType = pointTypeRepository.findById(rewardRequest.getPointTypeId()).orElseThrow(
                ()-> new PointTypeInvalidException("Invalid Point Type - "+ rewardRequest.getPointTypeId()));


        Member memberFound = memberRepository.findByIdForUpdate(rewardRequest.getMemberId()).orElseThrow(
                () -> new MemberNotFoundException("Member not found with id: " + rewardRequest.getMemberId())
        );

        // check balance will throw error in case of low balance
        checkBalance(rewardRequest.getMemberId(), pointType.getTransactionType(), rewardRequest.points);

        long signedPoints = rewardRequest.getPoints();
        if (pointType.getTransactionType().equals(TransactionType.DEBIT)) {
            signedPoints = -signedPoints;
        }


        Rewards reward = new Rewards();
        reward.setMember(memberFound);
        reward.setPoints(signedPoints);
        reward.setDescription(rewardRequest.getDescription());
        reward.setPointTypeId(rewardRequest.getPointTypeId());
        rewardsRepository.save(reward);

        return toResponse(reward);
    }


    private void checkBalance(Long memberId, @NotNull TransactionType transactionType, Long requestPoints) {
        if(transactionType.equals(TransactionType.DEBIT)){
            Long balance = rewardsRepository.calculateBalance(memberId);
            if(balance<requestPoints){
                throw new BalanceNotSufficientException("Current Balance is less than redemption amount!! Current balance is "+ balance + " points");
            }
        }
    }

    @Override
    public List<RewardResponse> fetchRewards(Long memberId) {
        Member memberFound = memberRepository.findById(memberId).orElseThrow(
                () -> new MemberNotFoundException("Member not found with id: " + memberId)
        );
        List<Rewards> rewardsList = rewardsRepository.findByMemberIdOrderByEventDate(memberId);
        List<RewardResponse> rewardResponses = new ArrayList<>();
        for(Rewards reward : rewardsList){
            rewardResponses.add(toResponse(reward));
        }
        return rewardResponses;
    }

    private @NotNull RewardResponse toResponse(@NotNull Rewards reward) {
        RewardResponse rewardResponse = new RewardResponse();
        rewardResponse.setRewardId(reward.getRewardId());
        rewardResponse.setPoints(reward.getPoints());
        rewardResponse.setDescription(reward.getDescription());
        rewardResponse.setMemberId(reward.getMember().getId());
        rewardResponse.setEventDate(reward.getEventDate());
        rewardResponse.setPointsTypeId(reward.getPointTypeId());
        return rewardResponse;
    }
}
