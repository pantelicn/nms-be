package com.opdev.model.company;

import com.opdev.model.Audit;
import com.opdev.model.contact.Contact;
import com.opdev.model.location.CompanyLocation;
import com.opdev.model.post.Post;
import com.opdev.model.post.ReactionType;
import com.opdev.model.request.Request;
import com.opdev.model.search.SearchTemplate;
import com.opdev.model.subscription.Prepaid;
import com.opdev.model.subscription.ProductUsage;
import com.opdev.model.subscription.Subscription;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.annotations.Type;

@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
@Getter
@ToString(callSuper = true)
@Entity
@Table(name = "company")
public class Company extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @NonNull
    @Column(nullable = false)
    private String name;

    @NonNull
    @Column(nullable = false, length = 1000)
    private String description;

    @Setter
    private String profileImage;

    @NonNull
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "company_location", referencedColumnName = "id", nullable = false)
    private CompanyLocation location;

    @NonNull
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "company")
    @Builder.Default
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "company")
    @Builder.Default
    private List<Contact> contacts = new ArrayList<>();

    @OneToMany(mappedBy = "company")
    @Builder.Default
    private List<SearchTemplate> searchTemplates = new ArrayList<>();

    @OneToMany(mappedBy = "company")
    @Builder.Default
    @ToString.Exclude
    private List<Benefit> benefits = new ArrayList<>();

    @OneToMany(mappedBy = "company")
    @Builder.Default
    private List<Request> requests = new ArrayList<>();

    @OneToMany(mappedBy = "company")
    @Builder.Default
    private List<Subscription> subscriptions = new ArrayList<>();

    @OneToMany(mappedBy = "company")
    @Builder.Default
    private List<Prepaid> prepaids = new ArrayList<>();

    @OneToMany(mappedBy = "company")
    @Builder.Default
    private List<ProductUsage> productUsages = new ArrayList<>();

    @ToString.Exclude
    @Type(type = "json")
    @Builder.Default
    private Map<Long, Set<ReactionType>> postReactions = new HashMap<>();

    public boolean alreadyReacted(Long postId, ReactionType reactionType) {
        Set<ReactionType> reactions = postReactions.get(postId);
        if (reactions != null) {
            return reactions.contains(reactionType);
        }
        return false;
    }

    public void addPostReaction(Long postId, ReactionType reaction) {
        postReactions.computeIfAbsent(postId, k -> new HashSet<>()).add(reaction);
    }

    public void removePostReaction(Long postId, ReactionType reaction) {
        Set<ReactionType> reactions = postReactions.get(postId);
        reactions.remove(reaction);
    }

}
