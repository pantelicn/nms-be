package com.opdev.model.chat;

import com.opdev.model.Audit;
import com.opdev.model.user.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
@Getter
@Setter
@Entity
@Table(name = "available_chat")
public class AvailableChat extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String companyUsername;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String talentUsername;

    @Column(nullable = false)
    private String talentName;

    @ManyToOne(optional = false)
    private User company;

    @ManyToOne(optional = false)
    private User talent;

}
