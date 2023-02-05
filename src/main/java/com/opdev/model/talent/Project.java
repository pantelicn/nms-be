package com.opdev.model.talent;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
@Builder
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @NonNull
    @Column(length = 1000)
    @Setter
    private String description;

    @NonNull
    @Column(name = "technologies_used")
    @Setter
    private String technologiesUsed;

    @NonNull
    @Column(name = "my_role")
    @Setter
    private String myRole;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "talent_id", referencedColumnName = "id", nullable = false)
    private Talent talent;

}
