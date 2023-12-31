package com.opdev.model.post;

import com.opdev.model.Audit;
import com.opdev.model.company.Company;
import com.opdev.model.location.Country;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@ToString(callSuper = true, exclude = {"company"})
@Entity
@Table(name = "post")
public class Post extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @NonNull
    @Column(nullable = false, length = 1000)
    private String content;

    @NonNull
    @Column(nullable = false)
    private String title;

    @NonNull
    @Column(nullable = false)
    @Builder.Default
    private Integer likes = 0;

    @NonNull
    @Column(nullable = false)
    @Builder.Default
    private Integer awards = 0;

    private String url;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "country_id", referencedColumnName = "id", nullable = false)
    private Country country;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "company_id", referencedColumnName = "id", nullable = false)
    private Company company;

    @Setter
    private boolean awardEarned;

    public void addReaction(ReactionType reaction) {
        if (reaction == ReactionType.LIKE) {
            addLike();
        }
        if (reaction == ReactionType.AWARD) {
            addAward();
        }
    }

    public void removeReaction(ReactionType reaction) {
        if (reaction == ReactionType.LIKE) {
            removeLike();
        }

        if (reaction == ReactionType.AWARD) {
            removeAward();
        }
    }

    private void addLike() {
        this.likes++;
    }

    private void removeLike() {
        this.likes--;
    }

    private void addAward() {
        this.awards++;
    }

    private void removeAward() {
        this.awards--;
    }

}
