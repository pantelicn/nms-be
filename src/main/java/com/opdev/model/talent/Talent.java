package com.opdev.model.talent;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.opdev.model.Audit;
import com.opdev.model.contact.Contact;
import com.opdev.model.location.Location;
import com.opdev.model.request.Request;
import com.opdev.model.term.TalentTerm;
import com.opdev.model.user.User;

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
@Builder(toBuilder = true)
@Getter
@Entity
@ToString(callSuper = true)
@Table(name = "talent")
public class Talent extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @NonNull
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NonNull
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @NonNull
    @Column(nullable = false)
    @Builder.Default
    private Boolean available = Boolean.TRUE;

    /**
     * This field will be updated when user change it location or change
     * availability from false to true. It's used by search template when cron job
     * starts.
     *
     */
    @NonNull
    @Column(name = "availability_change_date", nullable = false)
    private Instant availabilityChangeDate;

    @NonNull
    @OneToOne
    @JoinColumn(name = "user", referencedColumnName = "id", nullable = false)
    private User user;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "location", referencedColumnName = "id")
    private Location currentLocation;

    @OneToMany(mappedBy = "talent")
    @Builder.Default
    private List<Location> availableLocations = new ArrayList<>();

    @OneToMany(mappedBy = "talent")
    @Builder.Default
    private List<Contact> contacts = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "talent")
    @Builder.Default
    private List<TalentPosition> talentPositions = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "talent")
    @Builder.Default
    private List<TalentSkill> talentSkills = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "talent")
    @Builder.Default
    private List<TalentTerm> talentTerms = new ArrayList<>();

    @OneToMany(mappedBy = "talent")
    @Builder.Default
    private List<Request> requests = new ArrayList<>();

    @OneToMany(mappedBy = "talent")
    @Builder.Default
    private List<TalentTerm> terms = new ArrayList<>();

}
