package com.opdev.model;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import javax.validation.constraints.PastOrPresent;

import com.opdev.NMSEntityListener;
import com.opdev.model.user.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@MappedSuperclass
@EntityListeners(NMSEntityListener.class)
public abstract class Audit {

    @OneToOne
    @JoinColumn(name = "created_by", referencedColumnName = "id")
    private User createdBy;

    @Column(name = "created_on", nullable = false, updatable = false)
    @PastOrPresent
    private Instant createdOn;

    @Setter
    @OneToOne
    @JoinColumn(name = "modified_by", referencedColumnName = "id")
    private User modifiedBy;

    @Setter
    @Column(name = "modified_on")
    private Instant modifiedOn;

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
        this.modifiedBy = createdBy;
    }

    public void setCreatedOn(Instant createdOn) {
        this.createdOn = createdOn;
        this.modifiedOn = createdOn;
    }

}
