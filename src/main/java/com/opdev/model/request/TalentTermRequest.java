package com.opdev.model.request;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.opdev.model.Audit;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@ToString(callSuper = true)
@Entity
@Table(name = "talent_term_request")
public class TalentTermRequest extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Embedded
    private TalentTermSnapshot talentTermSnapshot;

    @ToString.Exclude
    @NonNull
    @ManyToOne
    @JoinColumn(name = "request_id", referencedColumnName = "id", nullable = false)
    private Request request;

    @Setter
    @Column(name = "counter_offer")
    private String counterOffer;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Setter
    private TalentTermRequestStatus status;

    public boolean isStatusCounterOffer() {
        return status == TalentTermRequestStatus.COUNTER_OFFER_TALENT || status == TalentTermRequestStatus.COUNTER_OFFER_COMPANY;
    }

}
