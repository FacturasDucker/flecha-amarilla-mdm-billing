package org.flechaamarilla.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * DTO for raw data coming from different sources
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Raw data from different sources that needs to be processed")
public class RawDataDTO {

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
     * Source identifier (e.g., csv-import, api-call, etc.)
     */
    @Schema(description = "Source identifier (e.g., csv-import, api-call)")
    private String source;

    /**
     * All entity data as key-value pairs
     */
    @Schema(description = "Entity data as key-value pairs")
    private Map<String, String> data;

    /**
     * Batch ID if part of a batch operation
     */
    @Schema(description = "Batch ID if part of a batch operation")
    private String batchId;
}
