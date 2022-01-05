package com.opdev.request;

import com.opdev.company.dto.RequestViewDto;
import com.opdev.config.security.Roles;
import com.opdev.model.request.Request;
import com.opdev.request.dto.RequestResponseDto;
import com.opdev.talent.TalentTermRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("v1")
@RequiredArgsConstructor
public class TalentTermRequestController {

    private final TalentTermRequestService service;

    @PutMapping("companies/{username}/talent-term-requests")
    @PreAuthorize("(#username == authentication.name && hasRole('" + Roles.COMPANY + "'))")
    public RequestViewDto editByCompany(@Valid @RequestBody RequestResponseDto requestResponse,
                                        @PathVariable String username) {
        Request modified = service.editByCompany(requestResponse, username);
        return new RequestViewDto(modified);
    }

    @PutMapping("talents/{username}/talent-term-requests")
    @PreAuthorize("(#username == authentication.name && hasRole('" + Roles.TALENT + "'))")
    public RequestViewDto editByTalent(@Valid @RequestBody RequestResponseDto requestResponse,
                                       @PathVariable String username) {
        Request modified = service.editByTalent(requestResponse, username);
        return new RequestViewDto(modified);
    }

}
