package com.opdev;

import java.time.Instant;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import com.opdev.model.Audit;

/**
 *
 * Triggered by {@link javax.persistence.EntityListeners} on the {@link Audit}
 * class. Sets timestamps for certain events.
 *
 */
public class NMSEntityListener {

  @PrePersist
  public void prePersist(final Audit audit) {
    audit.setCreatedOn(Instant.now());
    audit.setModifiedOn(Instant.now());
  }

  @PreUpdate
  public void preUpdate(final Audit audit) {
    audit.setModifiedOn(Instant.now());
  }

}
