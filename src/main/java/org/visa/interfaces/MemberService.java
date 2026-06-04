package org.visa.interfaces;

import org.visa.Dtos.MemberCreateResponse;
import org.visa.Dtos.MemberRequest;
import org.visa.Dtos.MemberResponse;

public interface MemberService {

    MemberCreateResponse createNewMember(MemberRequest memberRequest);

    MemberResponse fetchMember(Long memberId);
}
