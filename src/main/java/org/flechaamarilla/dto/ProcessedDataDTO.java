package org.flechaamarilla.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * DTO for processed data after MDM standardization
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Processed data after MDM standardization")
public class ProcessedDataDTO {

    /**
     * Type of entity (issuer, receiver, product)
     */
    @Schema(description = "Type of entity (ISSUER, RECEIVER, PRODUCT)")
    private String entityType;

    /**
     * Tenant ID for multi-tenancy
     */
    @Schema(description = "Tenant ID for multi-tenancy")
    private String tenantId;

    /**
     * Entity ID in the database
     */
    @Schema(description = "Entity ID in the database")
    private Long entityId;

    /**
     * Standardized entity data
     */
    @Schema(description = "Standardized entity data")
    private Map<String, String> data;

    /**
     * Quality score for the processed data (0.0 to 1.0)
     */
    @Schema(description = "Quality score for the processed data (0.0 to 1.0)")
    private Double qualityScore;

    /**
     * Processing timestamp
     */
    @Schema(description = "Processing timestamp")
    private Long timestamp;

    /**
     * Original batch ID if part of a batch operation
     */
    @Schema(description = "Original batch ID if part of a batch operation")
    private String batchId;
}
