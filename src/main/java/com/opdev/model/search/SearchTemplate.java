package com.opdev.model.search;

import com.opdev.model.Audit;
import com.opdev.model.company.Company;
import com.opdev.model.location.SearchTemplateAvailableLocation;
import com.opdev.model.location.TalentAvailableLocation;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.lang.NonNull;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@ToString(callSuper = true)
@Entity
@Table
public class SearchTemplate extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @NonNull
    @Setter
    @Column(nullable = false)
    private String name;

    @NonNull
    @Setter
    private Integer experienceYears;

    @Setter
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "searchTemplate")
    private List<SearchTemplateAvailableLocation> availableLocations = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "searchTemplate", orphanRemoval = true)
    @Builder.Default
    private List<Facet> facets = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "company_id", referencedColumnName = "id", nullable = false)
    @Setter
    private Company company;

    public boolean containsFacet(Facet facet) {
        return facets
                .stream()
                .anyMatch(f -> f.getId().equals(facet.getId()));
    }

    public void removeAllFacets(List<Facet> facets) {
        this.facets.removeAll(facets);
    }
}
