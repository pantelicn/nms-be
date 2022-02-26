package com.opdev.model.post;

import com.opdev.model.Audit;
import com.opdev.model.company.Company;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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
@NoArgsConstructor(access = AccessLevel.PRIVATE)
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
    @Column(nullable = false)
    private String content;

    @NonNull
    @Column(nullable = false)
    private String title;

    @NonNull
    @Column(nullable = false)
    @Builder.Default
    private Integer likes = 0;

    private String url;

    @Column(nullable = false)
    private String country;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "company_id", referencedColumnName = "id", nullable = false)
    private Company company;

    public void addReaction(ReactionType reaction) {
        if (reaction == ReactionType.LIKE) {
            addLike();
        }
    }

    public void removeReaction(ReactionType reaction) {
        if (reaction == ReactionType.LIKE) {
            removeLike();
        }
    }

    private void addLike() {
        this.likes++;
    }

    private void removeLike() {
        this.likes--;
    }


}
