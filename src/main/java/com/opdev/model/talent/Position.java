package com.opdev.model.talent;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import com.opdev.model.Audit;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@ToString(exclude = { "talentPositions", "positionSkills" }, callSuper = true)
@Entity
@Table(name = "position")
public class Position extends Audit {

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
    @Column(nullable = false, unique = true)
    private String code;

    @OneToMany(mappedBy = "talent", cascade = CascadeType.ALL)
    @Builder.Default
    private List<TalentPosition> talentPositions = new ArrayList<>();

    @OneToMany(mappedBy = "position", cascade = CascadeType.ALL)
    @Builder.Default
    private List<PositionSkill> positionSkills = new ArrayList<>();

}