package com.opdev.model.term;

import java.time.Instant;

import javax.persistence.Column;
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
import com.opdev.model.talent.Talent;

import com.opdev.model.user.User;
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
@Table(name = "talent_term")
public class TalentTerm extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @NonNull
    @Column(nullable = false)
    private String value;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TermType termType;

    @NonNull
    @Column(nullable = false)
    private Boolean negotiable;

    @ToString.Exclude
    @ManyToOne
    @Setter
    @JoinColumn(name = "talent_id", referencedColumnName = "id", nullable = false)
    private Talent talent;

    @ManyToOne
    @Setter
    @JoinColumn(name = "term_id", referencedColumnName = "id", nullable = false)
    private Term term;

    @Enumerated(EnumType.STRING)
    @Column
    private UnitOfMeasure unitOfMeasure;

    public void update(TalentTerm modified, User modifiedBy) {
        value = modified.getValue();
        negotiable = modified.getNegotiable();
        setModifiedBy(modifiedBy);
        setModifiedOn(Instant.now());
    }

}
