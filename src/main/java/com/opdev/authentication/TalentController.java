package com.opdev.authentication;

import java.util.Optional;

import javax.validation.Valid;

import com.opdev.authentication.dto.TalentBasicInfoUpdateDto;
import com.opdev.authentication.dto.TalentViewDto;
import com.opdev.common.events.UserRegisteredEvent;
import com.opdev.config.security.Roles;
import com.opdev.dto.TalentRegistrationDto;
import com.opdev.exception.ApiEntityNotFoundException;
import com.opdev.model.talent.Talent;
import com.opdev.model.user.Role;
import com.opdev.model.user.User;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/talents")
class TalentController {

  private final ApplicationEventPublisher eventPublisher;
  private final TalentService talentService;
  private final RoleService roleService;
  private final PasswordEncoder passwordEncoder;
  private final UserService userService;

  @PostMapping
  @PreAuthorize("permitAll()")
  public void add(@Valid @RequestBody final TalentRegistrationDto talentRegistrationDto) {
    LOGGER.info("Registering a new talent: {}", talentRegistrationDto.getUsername());

    final String encodedPassword = passwordEncoder.encode(talentRegistrationDto.getPassword());
    final Role talentRole = roleService.findByName(Roles.TALENT);

    User admin = null;
    if (userService.isAdminLoggedIn()) {
      admin = userService.getLoggedInUser();
    }

    final Talent talent = talentRegistrationDto.asTalent(encodedPassword, talentRole, admin);
    final Talent registeredTalent = talentService.register(talent);

    eventPublisher.publishEvent(new UserRegisteredEvent(this, registeredTalent.getUser()));
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

}
