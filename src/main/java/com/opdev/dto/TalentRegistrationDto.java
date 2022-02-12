package com.opdev.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import com.opdev.dto.paging.RegistrationDto;
import com.opdev.model.location.Location;
import com.opdev.model.talent.Talent;
import com.opdev.model.talent.Talent.TalentBuilder;
import com.opdev.model.user.User;
import com.opdev.model.user.UserType;
import com.opdev.validation.Password;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@Password
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ToString(callSuper = true)
public class TalentRegistrationDto implements RegistrationDto {

  @NotBlank
  @NonNull
  private String firstName;

  @NonNull
  private String lastName;

  private String middleName;

  private LocalDate dateOfBirth;

  @NonNull
  @NotBlank
  @Email
  private String username;

  @NonNull
  @NotBlank
  private String password;

  @NonNull
  @NotBlank
  private String passwordConfirmed;

  private LocationDto location;

  public Talent asTalent() {

    final User user = User.builder()
            .username(username)
            .password(password)
            .type(UserType.TALENT)
            .build();

    final TalentBuilder builder = Talent.builder()
            .user(user)
            .firstName(firstName)
            .lastName(lastName)
            .middleName(middleName)
            .dateOfBirth(dateOfBirth)
            .availabilityChangeDate(Instant.now());

    final Optional<Location> currentLocation = createLocation();
    currentLocation.ifPresent(builder::currentLocation);

    final Talent talent = builder.build();
    talent.setCreatedBy(user);

    return talent;
  }

  private Optional<Location> createLocation() {
    Location currentLocation = null;
    if (!Objects.isNull(this.location)) {
      currentLocation = Location.builder().city(this.location.getCity()).country(this.location.getCountry())
          .province(this.location.getProvince()).countryCode(this.location.getCountryCode()).build();
    }

    return Optional.ofNullable(currentLocation);
  }

}
