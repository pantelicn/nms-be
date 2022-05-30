package com.opdev.talent;

import com.opdev.company.service.CompanyService;
import com.opdev.config.security.Roles;
import com.opdev.dto.TalentRegistrationDto;
import com.opdev.model.company.Company;
import com.opdev.model.talent.Talent;
import com.opdev.model.user.User;
import com.opdev.talent.dto.FacetSpecifierDto;
import com.opdev.talent.dto.TalentBasicInfoUpdateDto;
import com.opdev.talent.dto.TalentViewDto;
import com.opdev.talent.dto.TalentViewSearchDto;
import com.opdev.talent.search.TalentSpecification;
import com.opdev.user.UserService;
import com.opdev.util.encoding.TalentIdEncoder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/talents")
public class TalentController {

    private final TalentService talentService;
    private final UserService userService;
    private final CompanyService companyService;
    private final TalentIdEncoder encoder;

    @PostMapping
    @PreAuthorize("permitAll()")
    @ResponseStatus(HttpStatus.CREATED)
    public TalentViewDto add(@Valid @RequestBody final TalentRegistrationDto talentRegistrationDto) {
        LOGGER.info("Registering a new talent: {}", talentRegistrationDto.getUsername());

        final Talent talent = talentRegistrationDto.asTalent();
        final Talent registeredTalent = talentService.register(talent);

        return new TalentViewDto(registeredTalent);
    }

    @GetMapping("/{username}")
    @PreAuthorize("(#username == authentication.name && hasRole('" + Roles.TALENT + "')) " + //
            "|| hasRole('" + Roles.ADMIN + "')")
    public ResponseEntity<TalentViewDto> view(@PathVariable final String username) {
        LOGGER.info("Viewing the talent: {}", username);

        final Talent talent = talentService.view(username);
        return ResponseEntity.ok(new TalentViewDto(talent));
    }

    @PutMapping("/{username}")
    @PreAuthorize("(#username == authentication.name && hasRole('" + Roles.TALENT + "')) " + //
            "|| hasRole('" + Roles.ADMIN + "')")
    public ResponseEntity<TalentViewDto> updateBasicInfo(@PathVariable final String username,
                                                         @Valid @RequestBody final TalentBasicInfoUpdateDto talentBasicInfoUpdateDto) {
        LOGGER.info("Updating the talent: {}", username);

        final Talent oldTalent = talentService.getByUsername(username);
        User admin = null;
        if (userService.isAdminLoggedIn()) {
            admin = userService.getLoggedInUser();
        }

        final Talent talent = talentService.updateBasicInfo(talentBasicInfoUpdateDto.asTalent(oldTalent, admin));
        return ResponseEntity.ok(new TalentViewDto(talent));
    }

    @DeleteMapping("/{username}")
    @PreAuthorize("(#username == authentication.name && hasRole('" + Roles.TALENT + "')) " + //
            "|| hasRole('" + Roles.ADMIN + "')")
    public void delete(@PathVariable final String username, @RequestParam(required = false) final boolean disable) {
        if (disable) {
            LOGGER.info("Disabling the talent: {}", username);
            talentService.disable(username);
            return;
        }

        LOGGER.info("Deleting the talent: {}", username);
        talentService.delete(username);
    }

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
        //final Company foundCompany = companyService.getByUsername(userService.getLoggedInUser().getUsername());
        final Page<TalentViewSearchDto> response = talentService.find(talentSpecification, pageable)
                .map(talent -> new TalentViewSearchDto(talent, encoder, 1L));

        return ResponseEntity.ok(response);
    }

}
