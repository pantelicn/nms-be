package com.opdev.talent;

import com.opdev.company.service.CompanyService;
import com.opdev.config.security.Roles;
import com.opdev.model.company.Company;
import com.opdev.talent.dto.FacetSpecifierDto;
import com.opdev.talent.dto.TalentViewSearchDto;
import com.opdev.talent.search.TalentSpecification;
import com.opdev.util.encoding.TalentIdEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/talents/find")
public class TalentSearchController {

    private final TalentService talentService;
    private final TalentIdEncoder encoder;
    private final CompanyService companyService;

    @PostMapping
    @PreAuthorize("hasRole('" + Roles.COMPANY + "')")
    public ResponseEntity<Page<TalentViewSearchDto>> find(@RequestBody List<FacetSpecifierDto> facetSpecifiers,
                                                          final Pageable pageable,
                                                          final Principal user) {
        TalentSpecification talentSpecification = new TalentSpecification(
                facetSpecifiers
                        .stream()
                        .map(FacetSpecifierDto::asFacet)
                        .collect(Collectors.toList())
        );
        Company foundCompany = companyService.getByUsername(user.getName());
        final Page<TalentViewSearchDto> response = talentService.find(talentSpecification, pageable)
                .map(talent -> new TalentViewSearchDto(talent, encoder, foundCompany.getId()));

        return ResponseEntity.ok(response);
    }

}
