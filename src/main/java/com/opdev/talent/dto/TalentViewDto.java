package com.opdev.talent.dto;

import com.opdev.dto.LocationDto;
import com.opdev.dto.UserViewDto;
import com.opdev.model.post.ReactionType;
import com.opdev.model.talent.Talent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Builder
@Getter
@ToString(callSuper = true)
public class TalentViewDto {

    private String firstName;

    private String lastName;

    private Integer experienceYears;

    private UserViewDto user;

    private LocationDto location;

    private List<AvailableLocationViewDto> availableLocations;

    private List<Long> likedPosts = new ArrayList<>();

    public TalentViewDto(Talent talent) {
        asView(talent);
    }

    private void asView(@NonNull Talent talent) {
        this.firstName = talent.getFirstName();
        this.lastName = talent.getLastName();
        this.experienceYears = talent.getExperienceYears();
        this.user = new UserViewDto(talent.getUser());
        this.location = new LocationDto(talent.getCurrentLocation());
        this.availableLocations = talent.getAvailableLocations().stream()
                .map(AvailableLocationViewDto::new)
                .collect(Collectors.toList());
        talent.getPostReactions().forEach( (k,v) -> {
            if (v == ReactionType.LIKE) {
                likedPosts.add(k);
            }
        });
    }

}
