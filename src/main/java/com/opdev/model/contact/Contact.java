package com.opdev.model.contact;

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

@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@ToString(callSuper = true, exclude = { "company", "talent" })
@Entity
@Table(name = "contact")
public class Contact extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContactType type;

    @NonNull
    @Column(nullable = false)
    private String value;

    @ManyToOne
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    @Setter
    private Company company;

    @ManyToOne
    @JoinColumn(name = "talent_id", referencedColumnName = "id")
    @Setter
    private Talent talent;

}
