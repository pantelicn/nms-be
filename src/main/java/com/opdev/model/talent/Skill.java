package com.opdev.model.talent;

import java.util.ArrayList;
import java.util.List;

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

import lombok.*;

@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@ToString(exclude = {"skillPositions", "skillTalents"}, callSuper = true)
@Entity
@Table(name = "skill")
public class Skill extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @NonNull
    @Column(nullable = false)
    private String name;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Setter
    private SkillStatus status;

    @NonNull
    @Column(nullable = false, unique = true)
    private String code;

    @OneToMany(mappedBy = "skill")
    @Builder.Default
    private List<PositionSkill> skillPositions = new ArrayList<>();

    @OneToMany(mappedBy = "skill")
    @Builder.Default
    private List<TalentSkill> skillTalents = new ArrayList<>();

}
