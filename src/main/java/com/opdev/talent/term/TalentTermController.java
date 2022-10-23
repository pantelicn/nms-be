package com.opdev.talent.term;

import com.opdev.config.security.Roles;
import com.opdev.model.term.TalentTerm;
import com.opdev.talent.dto.TalentTermAddDto;
import com.opdev.talent.dto.TalentTermEditDto;
import com.opdev.talent.dto.TalentTermViewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("v1/talents/{username}/terms")
@RequiredArgsConstructor
public class TalentTermController {

    private final TalentTermService service;

    @GetMapping
    @PreAuthorize("#username == authentication.name && hasRole('" + Roles.TALENT + "')")
    public List<TalentTermViewDto> getAll(@PathVariable String username) {
        final List<TalentTerm> found = service.getByTalent(username);
        return found.stream()
                .map(TalentTermViewDto::new)
                .collect(Collectors.toList());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("#username == authentication.name && hasRole('" + Roles.TALENT + "')")
    public List<TalentTermViewDto> add(@PathVariable String username,
                                       @RequestBody List<TalentTermAddDto> newTalentTerms) {
        List<TalentTerm> mapped = newTalentTerms.stream()
                .map(TalentTermAddDto::asTalentTerm)
                .collect(Collectors.toList());
        final List<TalentTerm> created = service.addTermsToTalent(username, mapped);
        return created.stream()
                .map(TalentTermViewDto::new)
                .collect(Collectors.toList());
    }

    @PutMapping
    @PreAuthorize("#username == authentication.name && hasRole('" + Roles.TALENT + "')")
    public List<TalentTermViewDto> edit(@PathVariable String username, @Valid @RequestBody final List<TalentTermEditDto> modified) {
        final List<TalentTerm> updated = service.edit(modified.stream()
                .map(TalentTermEditDto::asTalentTerm)
                .collect(Collectors.toList()), username);
        return updated.stream().map(TalentTermViewDto::new).collect(Collectors.toList());
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("#username == authentication.name && hasRole('" + Roles.TALENT + "')")
    public void remove(@PathVariable String username, @PathVariable Long id) {
        service.remove(id, username);
    }

}
