package org.flechaamarilla.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object for creating a business unit with field mappings in a single operation
 */
@Data
public class BusinessUnitDTO {

    // Business Unit fields
    private String name;
    private String description;
    private String rfcEmitter;
    private String emitterName;
    private String defaultCurrency;
    private String series;

    // Field mappings to be created with the business unit
    private List<FieldMappingDTO> fieldMappings = new ArrayList<>();

    /**
     * Field mapping DTO for creating mappings
     */
    @Data
    public static class FieldMappingDTO {
        private String sourceFieldName;
        private String standardFieldName;
    }
}