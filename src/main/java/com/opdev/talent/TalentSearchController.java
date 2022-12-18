package com.opdev.talent;

import com.opdev.company.dto.RequestViewDto;
import com.opdev.company.service.CompanyService;
import com.opdev.config.security.Roles;
import com.opdev.model.company.Company;
import com.opdev.model.talent.Talent;
import com.opdev.request.RequestService;
import com.opdev.talent.dto.FacetSpecifierDto;
import com.opdev.talent.dto.LocationFilterDto;
import com.opdev.talent.dto.TalentSearchDto;
import com.opdev.talent.dto.TalentViewSearchDto;
import com.opdev.talent.search.TalentSpecification;
import com.opdev.util.encoding.TalentIdEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/talents/find")
public class TalentSearchController {

    private final TalentService talentService;
    private final TalentIdEncoder encoder;
    private final CompanyService companyService;
    private final RequestService requestService;

    private static final int MAX_TALENTS_PER_PAGE = 3;

    @PostMapping
    @PreAuthorize("hasRole('" + Roles.COMPANY + "')")
    public Page<TalentViewSearchDto> find(@Valid @RequestBody TalentSearchDto talentSearch,
                                                          @RequestParam(defaultValue = "0") Integer page,
                                                          final Principal user) {
        Pageable pageable = PageRequest.of(page, MAX_TALENTS_PER_PAGE);
        TalentSpecification talentSpecification = new TalentSpecification(
                talentSearch.getFacets()
                        .stream()
                        .map(FacetSpecifierDto::asFacet)
                        .collect(Collectors.toList()),
                talentSearch.getLocations() == null ? null : talentSearch.getLocations().stream()
                        .map(LocationFilterDto::asLocationFilter)
                        .collect(Collectors.toList())
        );
        Company foundCompany = companyService.getByUsername(user.getName());
        return talentService.find(talentSpecification, pageable).map(talent -> mapTalentSearch(foundCompany, talent));
    }

    private TalentViewSearchDto mapTalentSearch(Company foundCompany, Talent talent) {
        TalentViewSearchDto talentSearch = new TalentViewSearchDto(talent, encoder, foundCompany.getId());
        requestService.findPreviousByTalentAndCompany(talent.getId(), foundCompany.getId())
                .ifPresent(request -> talentSearch.setPreviousRequest(new RequestViewDto(request)));
        return talentSearch;
    }

}
