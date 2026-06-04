package org.visa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.visa.Dtos.RewardResponse;
import org.visa.entities.Rewards;

import java.util.List;

public interface RewardsRepository extends JpaRepository<Rewards, Long> {

    @Query("Select coalesce(sum(r.points), 0) from Rewards r where r.member.id = :memberId")
    Long calculateBalance(@Param("memberId") Long memberId);

    List<Rewards> findByMemberIdOrderByEventDate(Long memberId);
}
