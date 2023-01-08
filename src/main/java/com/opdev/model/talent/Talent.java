package com.opdev.model.talent;

import com.opdev.model.Audit;
import com.opdev.model.contact.Contact;
import com.opdev.model.location.TalentAvailableLocation;
import com.opdev.model.post.ReactionType;
import com.opdev.model.request.Request;
import com.opdev.model.term.TalentTerm;
import com.opdev.model.user.User;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

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
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
@Getter
@Entity
@ToString(callSuper = true)
@Table(name = "talent")
@TypeDef(name = "json", typeClass = JsonStringType.class)
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
     */
    @NonNull
    @Column(name = "availability_change_date", nullable = false)
    private Instant availabilityChangeDate;

    @NonNull
    @OneToOne
    @JoinColumn(name = "user", referencedColumnName = "id", nullable = false)
    private User user;

    @Setter
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TalentAvailableLocation> availableLocations = new ArrayList<>();

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

    @OneToMany(mappedBy = "talent")
    @Builder.Default
    private List<Request> requests = new ArrayList<>();

    @OneToMany(mappedBy = "talent")
    @Builder.Default
    private List<TalentTerm> talentTerms = new ArrayList<>();

    @ToString.Exclude
    @Type(type = "json")
    @Builder.Default
    private Map<Long, ReactionType> postReactions = new HashMap<>();

    @NonNull
    @NotNull
    @Min(0)
    @Builder.Default
    private Integer experienceYears = 0;

    public boolean alreadyReacted(Long postId) {
        return postReactions.containsKey(postId);
    }

    public void addPostReaction(Long postId, ReactionType reaction) {
        postReactions.put(postId, reaction);
    }

    public void removePostReaction(Long postId, ReactionType reaction) {
        postReactions.remove(postId, reaction);
    }

    public void replacePostReaction(Long postId, ReactionType reaction) {
        postReactions.replace(postId, reaction);
    }

    public ReactionType getReactionForPost(Long postId) {
        return postReactions.get(postId);
    }

    public String getFullName() {
        return String.join(" ", firstName, middleName, lastName);
    }
}
