package com.opdev.talent.term;

import com.opdev.model.request.Request;
import com.opdev.request.dto.RequestResponseDto;

public interface TalentTermRequestService {

    Request editByCompany(RequestResponseDto requestResponse, String username);

    Request editByTalent(RequestResponseDto requestResponse, String username);

}
