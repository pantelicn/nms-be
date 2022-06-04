package com.opdev.talent;

import com.opdev.config.security.Roles;
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

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/talents")
public class TalentSearchController {

    private final TalentService talentService;
    private final TalentIdEncoder encoder;

    @PostMapping("/find")
    @PreAuthorize("(#username == authentication.name && hasRole('" + Roles.COMPANY + "')) " + //
            "|| hasRole('" + Roles.ADMIN + "')")
    public ResponseEntity<Page<TalentViewSearchDto>> find(@RequestBody List<FacetSpecifierDto> facetSpecifiers, final Pageable pageable) {
        TalentSpecification talentSpecification = new TalentSpecification(
                facetSpecifiers
                        .stream()
                        .map(FacetSpecifierDto::asFacet)
                        .collect(Collectors.toList())
        );
        final Page<TalentViewSearchDto> response = talentService.find(talentSpecification, pageable)
                .map(talent -> new TalentViewSearchDto(talent, encoder, 1L));

        return ResponseEntity.ok(response);
    }

}
