package org.flechaamarilla.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.flechaamarilla.model.BusinessUnit;

import java.util.List;

/**
 * Maps source field names from business units to standard field names.
 * This allows handling different JSON structures from different businesses.
 */
@Entity
@Table(name = "field_mappings")
@Getter
@Setter
public class FieldMapping extends PanacheEntity {

    /**
     * The standard field name used in the system
     */
    @Column(nullable = false)
    private String standardFieldName;

    /**
     * The original field name used by the business unit
     */
    @Column(nullable = false)
    private String sourceFieldName;

    /**
     * The business unit this mapping belongs to
     */
    @ManyToOne
    @JsonBackReference // Indicates that this side of the relationship should not be serialized
    private BusinessUnit businessUnit;

    /**
     * Finds all mappings for a specific business unit
     *
     * @param businessUnitId The business unit ID
     * @return List of field mappings
     */
    public static List<FieldMapping> findByBusinessUnit(Long businessUnitId) {
        return list("businessUnit.id", businessUnitId);
    }
}