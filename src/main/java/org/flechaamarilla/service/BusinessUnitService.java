package org.flechaamarilla.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.flechaamarilla.dto.BusinessUnitDTO;
import org.flechaamarilla.model.BusinessUnit;
import org.flechaamarilla.model.FieldMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for managing business units and their field mappings
 */
@ApplicationScoped
public class BusinessUnitService {

    /**
     * Retrieves a business unit by its ID
     *
     * @param id Business unit ID
     * @return The BusinessUnit or null if not found
     */
    public BusinessUnit getBusinessUnit(Long id) {
        return BusinessUnit.findById(id);
    }

    /**
     * Creates a new business unit
     *
     * @param businessUnit The business unit to create
     * @return The created business unit with ID
     */
    @Transactional
    public BusinessUnit createBusinessUnit(BusinessUnit businessUnit) {
        businessUnit.persist();
        return businessUnit;
    }

    /**
     * Creates a new business unit with field mappings in a single transaction
     *
     * @param dto The business unit DTO containing mappings
     * @return The created business unit with ID and mappings
     */
    @Transactional
    public BusinessUnit createBusinessUnitWithMappings(BusinessUnitDTO dto) {
        // Create the business unit
        BusinessUnit businessUnit = new BusinessUnit();
        businessUnit.setName(dto.getName());
        businessUnit.setDescription(dto.getDescription());
        businessUnit.setRfcEmitter(dto.getRfcEmitter());
        businessUnit.setEmitterName(dto.getEmitterName());
        businessUnit.setDefaultCurrency(dto.getDefaultCurrency());
        businessUnit.setSeries(dto.getSeries());
        businessUnit.persist();

        // Create field mappings
        List<FieldMapping> mappings = new ArrayList<>();
        if (dto.getFieldMappings() != null) {
            for (BusinessUnitDTO.FieldMappingDTO mappingDto : dto.getFieldMappings()) {
                FieldMapping mapping = new FieldMapping();
                mapping.setSourceFieldName(mappingDto.getSourceFieldName());
                mapping.setStandardFieldName(mappingDto.getStandardFieldName());
                mapping.setBusinessUnit(businessUnit);
                mapping.persist();
                mappings.add(mapping);
            }
        }

        // Set field mappings
        businessUnit.setFieldMappings(mappings);

        return businessUnit;
    }

    /**
     * Updates an existing business unit
     *
     * @param id The business unit ID
     * @param businessUnit The updated business unit data
     * @return The updated business unit
     */
    @Transactional
    public BusinessUnit updateBusinessUnit(Long id, BusinessUnit businessUnit) {
        BusinessUnit entity = BusinessUnit.findById(id);
        if (entity == null) {
            return null;
        }

        entity.setName(businessUnit.getName());
        entity.setDescription(businessUnit.getDescription());
        entity.setRfcEmitter(businessUnit.getRfcEmitter());
        entity.setEmitterName(businessUnit.getEmitterName());
        entity.setDefaultCurrency(businessUnit.getDefaultCurrency());
        entity.setSeries(businessUnit.getSeries());

        return entity;
    }

    /**
     * Deletes a business unit
     *
     * @param id The business unit ID
     * @return True if deleted, false if not found
     */
    @Transactional
    public boolean deleteBusinessUnit(Long id) {
        return BusinessUnit.deleteById(id);
    }

    /**
     * Lists all business units
     *
     * @return List of all business units
     */
    public List<BusinessUnit> listAllBusinessUnits() {
        return BusinessUnit.listAll();
    }

    /**
     * Gets a map of field mappings for a business unit
     *
     * @param businessUnitId The business unit ID
     * @return Map of source field names to standard field names
     */
    public Map<String, String> getFieldMappings(Long businessUnitId) {
        List<FieldMapping> mappings = FieldMapping.findByBusinessUnit(businessUnitId);
        return mappings.stream()
                .collect(Collectors.toMap(
                        FieldMapping::getSourceFieldName,
                        FieldMapping::getStandardFieldName));
    }

    /**
     * Adds a field mapping to a business unit
     *
     * @param businessUnitId The business unit ID
     * @param mapping The field mapping to add
     * @return The created field mapping
     */
    @Transactional
    public FieldMapping addFieldMapping(Long businessUnitId, FieldMapping mapping) {
        BusinessUnit businessUnit = BusinessUnit.findById(businessUnitId);
        if (businessUnit == null) {
            return null;
        }

        mapping.setBusinessUnit(businessUnit);
        mapping.persist();
        return mapping;
    }
}