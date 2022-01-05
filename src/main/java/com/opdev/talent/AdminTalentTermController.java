package com.opdev.talent;

import com.opdev.config.security.Roles;
import com.opdev.exception.ApiEntityNotFoundException;
import com.opdev.model.term.TalentTerm;
import com.opdev.talent.dto.TalentTermViewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("v1/admin/talents/{username}/terms")
@RequiredArgsConstructor
public class AdminTalentTermController {

    private final TalentTermService service;

    @GetMapping
    @PreAuthorize("hasRole('" + Roles.ADMIN + "')")
    public List<TalentTermViewDto> getAll(@PathVariable String username) {
        final List<TalentTerm> found = service.getByTalent(username);
        return found.stream()
                .map(TalentTermViewDto::new)
                .collect(Collectors.toList());
    }

    @GetMapping("{id}")
    @PreAuthorize("hasRole('" + Roles.ADMIN + "')")
    public TalentTermViewDto get(@PathVariable String username, @PathVariable Long id) {
        TalentTerm found = service.getByIdAndTalent(id, username);
        return new TalentTermViewDto(found);
    }

}
