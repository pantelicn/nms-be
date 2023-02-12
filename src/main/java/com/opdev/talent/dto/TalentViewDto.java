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

    private Long id;

    private String firstName;

    private String lastName;

    private Integer experienceYears;

    private UserViewDto user;

    private LocationDto location;

    private List<AvailableLocationViewDto> availableLocations;

    private List<Long> likedPosts = new ArrayList<>();

    private List<Long> awardsGiven = new ArrayList<>();

    private Boolean available;

    private List<ProjectViewDto> projects = new ArrayList<>();

    public TalentViewDto(Talent talent) {
        asView(talent);
    }

    private void asView(@NonNull Talent talent) {
        this.id = talent.getId();
        this.firstName = talent.getFirstName();
        this.lastName = talent.getLastName();
        this.experienceYears = talent.getExperienceYears();
        this.user = new UserViewDto(talent.getUser());
        this.availableLocations = talent.getAvailableLocations().stream()
                .map(AvailableLocationViewDto::new)
                .collect(Collectors.toList());
        talent.getPostReactions().forEach( (k,v) -> {
            if (v.contains(ReactionType.LIKE)) {
                likedPosts.add(k);
            }
            if (v.contains(ReactionType.AWARD)) {
                awardsGiven.add(k);
            }
        });
        this.available = talent.getAvailable();
        talent.getProjects().forEach(project -> projects.add(new ProjectViewDto(project)));
    }

}
