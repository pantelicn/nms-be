package com.opdev.model.request;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.opdev.model.term.TalentTerm;
import com.opdev.model.term.TermType;
import com.opdev.model.term.UnitOfMeasure;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Embeddable
public class TalentTermSnapshot {

    @Column(name = "talent_term_id", nullable = false)
    private Long id;

    @Column(name = "talent_term_value", nullable = false)
    private String value;

    @Column(name = "talent_term_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TermType termType;

    @Column(name = "talent_term_negotiable", nullable = false)
    private Boolean negotiable;

    @Column(name = "talent_term_term_id", nullable = false)
    private Long termId;

    @Column(name = "talent_term__term_name", nullable = false)
    private String termName;

    @Column(name = "talent_term_term_description")
    private String termDescription;

    @Column(name = "talent_term_term_code", nullable = false)
    private String termCode;

    @Column(name = "talent_term_unit_of_measure")
    private UnitOfMeasure unitOfMeasure;

    public static TalentTermSnapshot fromTalentTerm(@NonNull TalentTerm talentTerm) {
        return TalentTermSnapshot.builder()
                .id(talentTerm.getId())
                .value(talentTerm.getValue())
                .termType(talentTerm.getTermType())
                .negotiable(talentTerm.getNegotiable())
                .termId(talentTerm.getTerm().getId())
                .termName(talentTerm.getTerm().getName())
                .termDescription(talentTerm.getTerm().getDescription())
                .termCode(talentTerm.getTerm().getCode())
                .unitOfMeasure(talentTerm.getUnitOfMeasure())
                .build();
    }

}
