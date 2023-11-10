package org.hibernate.bugs.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

/**
 * Abstract entity class.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class AbstractParentEntity {

    @Id
    @GeneratedValue
    private long id;

    public long getId() {
        return id;
    }
}
