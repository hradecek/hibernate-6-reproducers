package org.hibernate.bugs.entity;

import jakarta.persistence.Entity;

/**
 * Second child entity of an {@link AbstractParentEntity} with a specific attribute.
 */
@Entity
public class ChildBEntity extends AbstractParentEntity {

    private String attributeB;

    public String getAttributeB() {
        return attributeB;
    }

    public void setAttributeB(String attributeB) {
        this.attributeB = attributeB;
    }
}
