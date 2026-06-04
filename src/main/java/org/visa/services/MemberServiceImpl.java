package org.visa.services;

import lombok.RequiredArgsConstructor;
import org.visa.Dtos.MemberCreateResponse;
import org.visa.Dtos.MemberRequest;
import org.visa.Dtos.MemberResponse;
import org.visa.entities.Member;
import org.visa.exceptions.DuplicateEmailException;
import org.visa.exceptions.MemberNotFoundException;
import org.visa.interfaces.MemberService;
import org.visa.repository.MembersRepository;
import org.visa.repository.RewardsRepository;

import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MembersRepository membersRepository;
    private final RewardsRepository rewardsRepository;

    @Override
    public MemberCreateResponse createNewMember(MemberRequest memberRequest) {
        if(membersRepository.existsByEmail(memberRequest.getEmail())){
            throw new DuplicateEmailException("Member with same email already exists");
        }
        Member member = new Member();
        member.setName(memberRequest.getName());
        member.setEmail(memberRequest.getEmail());
        membersRepository.save(member);
        return new MemberCreateResponse(member.getId(), member.getName(), member.getEmail(), member.getCreatedAt());
    }

    @Override
    public MemberResponse fetchMember(Long memberId) {
        if(Objects.isNull(memberId)){
            throw new NullPointerException("Member Id is null");
        }

        Member foundMember = membersRepository.findById(memberId).orElseThrow(
                () -> new MemberNotFoundException("Member not found with id: " + memberId)
        );

//        Balance

        Long balance = rewardsRepository.calculateBalance(memberId);
        return toResponse(foundMember, balance);
    }

    private MemberResponse toResponse(Member member, Long balance){
        return new MemberResponse(
                member.getId(),
                member.getName(),
                member.getEmail(),
                balance,
                member.getCreatedAt()
        );
    }
}
