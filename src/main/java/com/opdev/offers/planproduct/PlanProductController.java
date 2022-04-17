package com.opdev.offers.planproduct;

import com.opdev.config.security.Roles;
import com.opdev.model.subscription.PlanProduct;
import com.opdev.offers.planproduct.dto.PlanProductDto;
import com.opdev.offers.planproduct.dto.PlanProductEditDto;
import com.opdev.offers.planproduct.dto.PlanProductViewDto;
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
@RequestMapping("/v1/")
public class PlanProductController {

    private final PlanProductService planProductService;

    @PostMapping("plans/products")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('" + Roles.ADMIN + "')")
    public PlanProductViewDto save(@Valid @RequestBody PlanProductDto planProduct) {
        final PlanProduct newPlanProduct = planProduct.asPlanProduct();
        final PlanProduct createdPlanProduct = planProductService
                .save(planProduct.getPlanId(), planProduct.getProductId(), newPlanProduct);

        return new PlanProductViewDto(createdPlanProduct);
    }

    @GetMapping("plans/products")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<PlanProductViewDto>> getAll() {
        final List<PlanProduct> foundPlanProducts = planProductService.findAll();
        final List<PlanProductViewDto> response = foundPlanProducts
                .stream()
                .map(PlanProductViewDto::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("plans/products/{planProductId}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<PlanProductViewDto> get(@PathVariable Long planProductId) {
        final PlanProduct foundPlanProduct = planProductService.get(planProductId);

        return ResponseEntity.ok(new PlanProductViewDto(foundPlanProduct));
    }

    @GetMapping("plans/{planId}/products")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<PlanProductViewDto>> getByPlanId(@PathVariable Long planId) {
        final List<PlanProduct> foundPlanProducts = planProductService.findAll(planId);
        final List<PlanProductViewDto> response = foundPlanProducts
                .stream()
                .map(PlanProductViewDto::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PutMapping("plans/products")
    @PreAuthorize("hasRole('" + Roles.ADMIN + "')")
    public ResponseEntity<PlanProductViewDto> update(@Valid @RequestBody PlanProductEditDto modified) {
        final PlanProduct updated = planProductService.update(modified.asPlanProduct());

        return ResponseEntity.ok(new PlanProductViewDto(updated));
    }

    @DeleteMapping("plans/products/{planProductId}")
    @PreAuthorize("hasRole('" + Roles.ADMIN + "')")
    public ResponseEntity<Void> delete(@PathVariable Long planProductId) {
        planProductService.delete(planProductId);

        return ResponseEntity.noContent().build();
    }

}
