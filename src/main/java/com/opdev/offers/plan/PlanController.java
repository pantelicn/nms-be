package com.opdev.offers.plan;

import com.opdev.config.security.Roles;
import com.opdev.model.subscription.Plan;
import com.opdev.offers.plan.dto.PlanDto;
import com.opdev.offers.plan.dto.PlanEditDto;
import com.opdev.offers.plan.dto.PlanViewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/plans")
public class PlanController {

    private final PlanService planService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('" + Roles.ADMIN + "')")
    public PlanViewDto create(@Valid @RequestBody PlanDto plan) {
        final Plan newPlan = plan.asPlan();
        final Plan createdPlan = planService.save(newPlan);

        return new PlanViewDto(createdPlan);
    }

    @GetMapping
    @PreAuthorize("hasRole('" + Roles.ADMIN + "')")
    public ResponseEntity<List<PlanViewDto>> findAll() {
        final List<Plan> foundPlans = planService.findAll();
        final List<PlanViewDto> response = foundPlans
                .stream()
                .map(PlanViewDto::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("{planId}")
    @PreAuthorize("hasRole('" + Roles.ADMIN + "')")
    public ResponseEntity<PlanViewDto> get(@PathVariable Long planId) {
        final Plan foundPlan = planService.getById(planId);

        return ResponseEntity.ok(new PlanViewDto(foundPlan));
    }

    @PutMapping
    @PreAuthorize("hasRole('" + Roles.ADMIN + "')")
    public ResponseEntity<PlanViewDto> update(@Valid @RequestBody PlanEditDto modified) {
        final Plan updated = planService.update(modified.asPlan());

        return ResponseEntity.ok(new PlanViewDto(updated));
    }

    @DeleteMapping("{planId}")
    @PreAuthorize("hasRole('" + Roles.ADMIN + "')")
    public ResponseEntity<Void> remove(@PathVariable Long planId) {
        planService.delete(planId);

        return ResponseEntity.noContent().build();
    }

}
