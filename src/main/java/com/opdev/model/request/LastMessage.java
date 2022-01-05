package com.opdev.model.request;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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

@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@ToString(callSuper = true)
@Entity
@Table(name = "last_message")
public class LastMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @ManyToOne
    @JoinColumn(name="last_message", referencedColumnName = "id", nullable = false)
    @Setter
    @NonNull
    private Message last;

    @ManyToOne
    @JoinColumn(name="talent", referencedColumnName = "id", nullable = false)
    @NonNull
    private User talent;

    @ManyToOne
    @JoinColumn(name="company", referencedColumnName = "id", nullable = false)
    @NonNull
    private User company;

    @Column(name = "modified_on")
    @Setter
    @NonNull
    private Instant modifiedOn;

}
