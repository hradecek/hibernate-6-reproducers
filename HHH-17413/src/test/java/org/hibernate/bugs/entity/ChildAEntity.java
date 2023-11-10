package org.hibernate.bugs.entity;

import jakarta.persistence.Entity;

/**
 * First child entity of an {@link AbstractParentEntity} with a specific attribute.
 */
@Entity
public class ChildAEntity extends AbstractParentEntity {

    private String attributeA;

    public String getAttributeA() {
        return attributeA;
    }

    public void setAttributeA(String attributeA) {
        this.attributeA = attributeA;
    }
}
