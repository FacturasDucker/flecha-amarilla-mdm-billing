package org.flechaamarilla.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;

/**
 * Represents a business unit that can handle invoice generation.
 * Each business unit may have different field mappings.
 */
@Entity
@Table(name = "business_units")
@Getter
@Setter
public class BusinessUnit extends PanacheEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String rfcEmitter;

    @Column(nullable = false)
    private String emitterName;

    @Column(nullable = false)
    private String defaultCurrency;

    @Column(nullable = false)
    private String series;

    @OneToMany(mappedBy = "businessUnit")
    @JsonManagedReference // Marks this side of the relationship as the one to serialize
    private List<FieldMapping> fieldMappings;

    public Long getId(){
        return this.id;
    }

    /**
     * Finds a business unit by its unique identifier.
     *
     * @param id The business unit ID
     * @return The business unit or null if not found
     */
    public static BusinessUnit findByIdOptional(Long id) {
        return findByIdOptional(id);
    }
}