package com.opdev.model.company;

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
import com.opdev.model.location.CompanyLocation;
import com.opdev.model.post.Post;
import com.opdev.model.request.Request;
import com.opdev.model.search.SearchTemplate;
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
    @Column(nullable = false)
    private String description;

    @NonNull
    @Column(nullable = false)
    private String address1;

    @Column
    private String address2;

    @NonNull
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "company_location", referencedColumnName = "id", nullable = false)
    private CompanyLocation location;

    @NonNull
    @OneToOne
    @JoinColumn(name = "user", referencedColumnName = "id", nullable = false)
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

}
