package com.opdev.model.request;

import com.opdev.model.Audit;
import com.opdev.model.company.Company;
import com.opdev.model.talent.Talent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
@Table(name = "request")
public class Request extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @ToString.Exclude
    @NonNull
    @ManyToOne
    @JoinColumn(name = "company_id", referencedColumnName = "id", nullable = false)
    private Company company;

    @ToString.Exclude
    @NonNull
    @ManyToOne
    @JoinColumn(name = "talent_id", referencedColumnName = "id", nullable = false)
    private Talent talent;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Setter
    private RequestStatus status;

    @NonNull
    @Column(nullable = false)
    @Setter
    private String note;

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL)
    @Builder.Default
    @Setter
    private List<TalentTermRequest> talentTermRequests = new ArrayList<>();

    @Setter
    private boolean seenByCompany;

    @Setter
    private boolean seenByTalent;

    @Column(length = 3000)
    private String jobDescription;

    public boolean isFinal() {
        return status == RequestStatus.ACCEPTED || status == RequestStatus.REJECTED;
    }

}
