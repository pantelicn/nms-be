package com.opdev.model.location;

import com.opdev.model.Audit;
import com.opdev.model.talent.Talent;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@ToString(callSuper = true)
@Entity
@Table(name = "talent_available_location")
public class TalentAvailableLocation extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @NonNull
    @Column
    private String country;

    @NonNull
    @Column
    private Long countryId;

    @Builder.Default
    @Column
    @ElementCollection
    private Set<String> cities = new HashSet<>();

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "talent_id", referencedColumnName = "id")
    private Talent talent;

    public void removeCity(String toRemoveCity) {
        cities = cities.stream().filter(city -> !city.equals(toRemoveCity)).collect(Collectors.toSet());
    }

    public void addCity(String toAddCity) {
        cities.add(toAddCity);
    }

}
