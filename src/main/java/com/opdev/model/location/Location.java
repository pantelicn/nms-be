package com.opdev.model.location;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.opdev.model.Audit;
import com.opdev.model.talent.Talent;

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
@ToString(callSuper = true)
@Entity
@Table(name = "location")
public class Location extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @NonNull
    @Column
    private String country;

    @Column
    // TODO: add an enum for provinces / states
    private String province;

    @NonNull
    @Column
    private String city;

    @NonNull
    // TODO: add an enum for country codes
    @Column(name = "country_code")
    private String countryCode;

    @ToString.Exclude
    @NonNull
    @ManyToOne
    @JoinColumn(name = "talent_id", referencedColumnName = "id", nullable = false)
    private Talent talent;

}
