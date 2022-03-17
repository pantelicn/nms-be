package com.opdev.model.term;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.opdev.model.Audit;

import com.opdev.model.user.User;
import com.opdev.util.CodeGenerator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.Instant;
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
public class Term extends Audit {

    public Term(@NonNull String code) {
        this.code = code;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @NonNull
    @Column(nullable = false)
    private String name;

    @NonNull
    @Column(nullable = false)
    private String description;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TermType type;

    @NonNull
    @Column(nullable = false, unique = true)
    private String code;

    private boolean availableForSearch;

    @ToString.Exclude
    @OneToMany(mappedBy = "talent")
    @Builder.Default
    private List<TalentTerm> talentTerms = new ArrayList<>();

    public void update(Term modified, User modifiedBy) {
        name = modified.getName();
        description = modified.getDescription();
        type = modified.getType();
        code = CodeGenerator.generate(name);
        setModifiedBy(modifiedBy);
        setModifiedOn(Instant.now());
    }

}
