package org.flechaamarilla.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * DTO for invoice line items
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Invoice line item")
public class ConceptDTO {
    @Schema(description = "SAT product/service code")
    private String prodServCode;

    @Schema(description = "Item description")
    private String description;

    @Schema(description = "Quantity")
    private int quantity;

    @Schema(description = "Unit of measure")
    private String unit;

    @Schema(description = "Unit price")
    private double unitPrice;

    @Schema(description = "Total amount")
    private double amount;
}