package com.opdev.benefit;

import com.opdev.benefit.dto.BenefitAddDto;
import com.opdev.benefit.dto.BenefitEditDto;
import com.opdev.config.security.Roles;
import com.opdev.model.company.Benefit;
import com.opdev.benefit.dto.BenefitViewDto;
import lombok.RequiredArgsConstructor;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.LikeIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/benefits")
@RequiredArgsConstructor
public class BenefitController {

    private final BenefitService service;

    @GetMapping
    @PreAuthorize("hasRole('" + Roles.ADMIN + "')")
    public List<BenefitViewDto> find(
            @And({
                    @Spec(path = "name", spec = LikeIgnoreCase.class),
                    @Spec(path = "isDefault", spec = Equal.class)
            }) Specification<Benefit> benefitSpec,
            final Pageable pageable) {
        final Page<Benefit> found = service.get(benefitSpec, pageable);
        return found.get()
                .map(BenefitViewDto::new)
                .collect(Collectors.toList());
    }

    @GetMapping("{id}")
    @PreAuthorize("hasRole('" + Roles.ADMIN + "')")
    public BenefitViewDto get(@PathVariable Long id) {
        final Benefit found = service.get(id);
        return new BenefitViewDto(found);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('" + Roles.ADMIN + "')")
    public BenefitViewDto add(@Valid @RequestBody final BenefitAddDto newBenefit) {
        final Benefit created = service.addBenefitToCompany(newBenefit.getCompanyUsername(), newBenefit.asBenefit());
        return new BenefitViewDto(created);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('" + Roles.ADMIN + "')")
    public void remove(@PathVariable final Long id) {
        service.remove(id);
    }

}
