package com.opdev.authentication.dto;

import java.util.Objects;

import javax.validation.constraints.NotBlank;

import com.opdev.common.utils.MappingUtils;
import com.opdev.model.talent.Talent;
import com.opdev.model.talent.Talent.TalentBuilder;
import com.opdev.model.user.User;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@ToString(callSuper = true)
public class TalentBasicInfoUpdateDto {

    @NotBlank
    @NonNull
    private String firstName;

    @NotBlank
    @NonNull
    private String lastName;

    public Talent asTalent(final Talent oldTalent, final User admin) {
        Objects.requireNonNull(oldTalent);

        final TalentBuilder oldTalentBuilder = oldTalent.toBuilder();

        if (MappingUtils.shouldUpdate(firstName, oldTalent.getFirstName())) {
            oldTalentBuilder.firstName(firstName);
        }

        if (MappingUtils.shouldUpdate(lastName, oldTalent.getLastName())) {
            oldTalentBuilder.lastName(lastName);
        }

        final Talent updatedTalent = oldTalentBuilder.build();
        if (null != admin) {
            updatedTalent.setModifiedBy(admin);
        } else {
            updatedTalent.setModifiedBy(oldTalent.getUser());
        }
        return updatedTalent;
    }

}
