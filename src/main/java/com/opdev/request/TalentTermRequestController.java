package com.opdev.request;

import com.opdev.company.dto.RequestDetailViewDto;
import com.opdev.config.security.Roles;
import com.opdev.model.request.Request;
import com.opdev.notification.NotificationFactory;
import com.opdev.notification.NotificationService;
import com.opdev.request.dto.RequestResponseDto;
import com.opdev.talent.term.TalentTermRequestService;
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
    private final NotificationService notificationService;

    @PutMapping("companies/{username}/talent-term-requests")
    @PreAuthorize("(#username == authentication.name && hasRole('" + Roles.COMPANY + "'))")
    public RequestDetailViewDto editByCompany(@Valid @RequestBody RequestResponseDto requestResponse,
                                              @PathVariable String username) {
        Request modified = service.editByCompany(requestResponse, username);
        notificationService.createOrUpdate(NotificationFactory.editRequestByCompany(modified.getId(), modified.getTalent().getUser(), modified.getCompany().getName()));
        return new RequestDetailViewDto(modified);
    }

    @PutMapping("talents/{username}/talent-term-requests")
    @PreAuthorize("(#username == authentication.name && hasRole('" + Roles.TALENT + "'))")
    public RequestDetailViewDto editByTalent(@Valid @RequestBody RequestResponseDto requestResponse,
                                       @PathVariable String username) {
        Request modified = service.editByTalent(requestResponse, username);
        notificationService.createOrUpdate(NotificationFactory.editRequestByTalent(modified.getId(), modified.getCompany().getUser(), modified.getNote()));
        return new RequestDetailViewDto(modified);
    }

}
