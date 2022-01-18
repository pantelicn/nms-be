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

  private LocationDto currentLocation;

  public Talent asTalent(final String encryptedPassword, final User admin) {
    Objects.requireNonNull(encryptedPassword);

    final User user = User.builder().username(username).password(encryptedPassword).type(UserType.TALENT).build();

    final TalentBuilder builder = Talent.builder().user(user).firstName(firstName).lastName(lastName)
        .middleName(middleName).dateOfBirth(dateOfBirth).availabilityChangeDate(Instant.now());

    final Optional<Location> location = createLocation();
    if (location.isPresent()) {
      builder.currentLocation(location.get());
    }

    final Talent talent = builder.build();
    if (null != admin) {
      talent.setCreatedBy(admin);
    }

    return talent;
  }

  private Optional<Location> createLocation() {
    Location location = null;
    if (!Objects.isNull(currentLocation)) {
      location = Location.builder().city(currentLocation.getCity()).country(currentLocation.getCountry())
          .province(currentLocation.getProvince()).countryCode(currentLocation.getCountryCode()).build();
    }

    return Optional.ofNullable(location);
  }

}
