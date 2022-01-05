package com.opdev.benefit;

import com.opdev.benefit.dto.BenefitEditDto;
import com.opdev.benefit.dto.BenefitViewDto;
import com.opdev.benefit.dto.CompanyBenefitAddDto;
import com.opdev.config.security.Roles;
import com.opdev.exception.ApiUnauthorizedException;
import com.opdev.model.company.Benefit;
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
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/companies/{username}/benefits")
@RequiredArgsConstructor
public class CompanyBenefitController {

    private final BenefitService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("(#username == authentication.name && hasRole('" + Roles.COMPANY + "'))")
    public BenefitViewDto add(@PathVariable final String username, @Valid @RequestBody final CompanyBenefitAddDto newBenefit) {
        final Benefit created = service.addBenefitToCompany(username, newBenefit.asBenefit());
        return new BenefitViewDto(created);
    }

    @GetMapping("{id}")
    @PreAuthorize("hasRole('" + Roles.COMPANY + "')")
    public BenefitViewDto get(@PathVariable final Long id, Principal principal) {
        final Benefit found = service.get(id);
        if (!found.getCompany().getUser().getUsername().equals(principal.getName())) {
            throw new ApiUnauthorizedException();
        }
        return new BenefitViewDto(found);
    }

    @GetMapping
    @PreAuthorize("(#username == authentication.name && hasRole('" + Roles.COMPANY + "'))")
    public List<BenefitViewDto> find(@PathVariable final String username) {
        final List<Benefit> found = service.getByCompany(username);
        return found.stream()
                .map(BenefitViewDto::new)
                .collect(Collectors.toList());
    }

    @PutMapping
    @PreAuthorize("(#username == authentication.name && hasRole('" + Roles.COMPANY + "'))")
    public BenefitViewDto edit(@PathVariable final String username, @Valid @RequestBody BenefitEditDto modified) {
        authorizeCompany(username, modified.getId());
        final Benefit updated = service.edit(modified.asBenefit());
        return new BenefitViewDto(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("(#username == authentication.name && hasRole('" + Roles.COMPANY + "'))")
    public void remove(@PathVariable final String username, @PathVariable final Long id) {
        authorizeCompany(username, id);
        service.remove(id);
    }

    private void authorizeCompany(final String companyUsername, final Long benefitId) {
        Benefit found = service.get(benefitId);
        if (!found.getCompany().getUser().getUsername().equals(companyUsername)) {
            throw new ApiUnauthorizedException();
        }
    }

}
