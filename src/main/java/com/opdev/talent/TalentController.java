package com.opdev.talent;

import com.opdev.dto.CompanyViewDto;
import com.opdev.dto.TalentRegistrationDto;
import com.opdev.mail.NullHireMailSender;
import com.opdev.model.location.TalentAvailableLocation;
import com.opdev.model.talent.Talent;
import com.opdev.model.user.User;
import com.opdev.talent.availablelocation.AvailableLocationService;
import com.opdev.talent.dto.AvailableLocationCreateDto;
import com.opdev.talent.dto.AvailableLocationViewDto;
import com.opdev.talent.dto.AvailableUpdateDto;
import com.opdev.talent.dto.TalentAvaliableLocationAddCityDto;
import com.opdev.talent.dto.TalentBasicInfoUpdateDto;
import com.opdev.talent.dto.TalentViewDto;
import com.opdev.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.opdev.config.security.SpELAuthorizationExpressions.AS_MATCHING_TALENT_OR_ADMIN;
import static com.opdev.config.security.SpELAuthorizationExpressions.IS_ADMIN;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/talents")
public class TalentController {

    private final TalentService talentService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final NullHireMailSender nullHireMailSender;
    private final AvailableLocationService availableLocationService;

    @PostMapping
    @PreAuthorize("permitAll()")
    @ResponseStatus(HttpStatus.CREATED)
    public TalentViewDto add(@Valid @RequestBody final TalentRegistrationDto talentRegistrationDto) {
        LOGGER.info("Registering a new talent: {}", talentRegistrationDto.getUsername());

        final Talent talent = talentRegistrationDto.asTalent(passwordEncoder);
        final Talent registeredTalent = talentService.register(talent);

        nullHireMailSender.sendRegistrationEmail(talentRegistrationDto.getUsername(), talent.getUser().getVerificationToken());

        return new TalentViewDto(registeredTalent);
    }

    @GetMapping("/{username}")
    @PreAuthorize(AS_MATCHING_TALENT_OR_ADMIN)
    public ResponseEntity<TalentViewDto> view(@PathVariable final String username) {
        LOGGER.info("Viewing the talent: {}", username);

        final Talent talent = talentService.view(username);
        return ResponseEntity.ok(new TalentViewDto(talent));
    }

    @GetMapping
    @PreAuthorize(IS_ADMIN)
    public Page<TalentViewDto> viewMultiple(@PageableDefault Pageable pageable) {
        LOGGER.info("Viewing all talents");
        return talentService.findAll(pageable).map(TalentViewDto::new);
    }

    @PutMapping("/{username}")
    @PreAuthorize(AS_MATCHING_TALENT_OR_ADMIN)
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
    @PreAuthorize(AS_MATCHING_TALENT_OR_ADMIN)
    public void delete(@PathVariable final String username, @RequestParam(required = false) final boolean disable) {
        if (disable) {
            LOGGER.info("Disabling the talent: {}", username);
            talentService.disable(username);
            return;
        }

        LOGGER.info("Deleting the talent: {}", username);
        talentService.delete(username);
    }

    @PostMapping("/{username}/available-locations")
    @PreAuthorize(AS_MATCHING_TALENT_OR_ADMIN)
    public AvailableLocationViewDto addAvailableLocation(@PathVariable String username,
                                       @Valid @RequestBody AvailableLocationCreateDto availableLocation) {
        Talent foundTalent = talentService.getByUsername(username);
        TalentAvailableLocation created = availableLocationService.create(availableLocation.asAvailableLocation(foundTalent));
        return new AvailableLocationViewDto(created);
    }

    @PatchMapping("/{username}/available-locations/{id}")
    @PreAuthorize(AS_MATCHING_TALENT_OR_ADMIN)
    public void addAvailableCity(@PathVariable String username, @PathVariable Long id, @Valid @RequestBody TalentAvaliableLocationAddCityDto addCityDto) {
        Talent foundTalent = talentService.getByUsername(username);
        availableLocationService.addCity(id, addCityDto.getCity(), foundTalent);
    }

    @DeleteMapping("/{username}/available-locations/{id}")
    @PreAuthorize(AS_MATCHING_TALENT_OR_ADMIN)
    public void removeAvailableLocation(@PathVariable String username,
                                          @PathVariable Long id) {
        Talent foundTalent = talentService.getByUsername(username);
        availableLocationService.remove(id, foundTalent);
    }

    @DeleteMapping("/{username}/available-locations/{id}/cities/{cityName}")
    @PreAuthorize(AS_MATCHING_TALENT_OR_ADMIN)
    public void removeAvailableCity(@PathVariable String username,
                                    @PathVariable Long id,
                                    @PathVariable String cityName) {
        Talent foundTalent = talentService.getByUsername(username);
        availableLocationService.removeCity(id, cityName, foundTalent);
    }

    @PatchMapping("/{username}/available")
    @PreAuthorize(AS_MATCHING_TALENT_OR_ADMIN)
    public void setAvailableForSearch(@PathVariable String username, @RequestBody AvailableUpdateDto availableUpdateDto) {
        Talent foundTalent = talentService.getByUsername(username);
        talentService.updateAvailability(foundTalent, availableUpdateDto.isAvailable());
    }

}
