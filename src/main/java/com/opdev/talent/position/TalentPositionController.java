package com.opdev.talent.position;

import com.opdev.config.security.Roles;
import com.opdev.position.dto.PositionViewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("v1/talents/{username}/positions")
@RequiredArgsConstructor
public class TalentPositionController {

    private final TalentPositionService service;

    @GetMapping
    @PreAuthorize("(#username == authentication.name && hasRole('" + Roles.TALENT + "'))" +
            "|| hasRole('" + Roles.ADMIN + "')")
    public List<PositionViewDto> getAll(@PathVariable String username) {
        return service.getByTalent(username).stream()
                .map(PositionViewDto::new)
                .collect(Collectors.toList());
    }

}
