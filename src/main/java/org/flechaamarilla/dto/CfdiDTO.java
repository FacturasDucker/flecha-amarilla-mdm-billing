package org.flechaamarilla.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

/**
 * DTO for CFDI invoice data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "CFDI invoice data")
public class CfdiDTO {
    @Schema(description = "RFC of the issuer")
    private String issuerRfc;

    @Schema(description = "Business name of the issuer")
    private String issuerName;

    @Schema(description = "RFC of the receiver")
    private String receiverRfc;

    @Schema(description = "Business name of the receiver")
    private String receiverName;

    @Schema(description = "CFDI usage code")
    private String cfdiUsage;

    @Schema(description = "Payment method code")
    private String paymentMethod;

    @Schema(description = "Payment form code")
    private String paymentForm;

    @Schema(description = "Currency code")
    private String currency;

    @Schema(description = "Invoice series")
    private String series;

    @Schema(description = "Invoice number")
    private String folio;

    @Schema(description = "Line items for the invoice")
    private List<ConceptDTO> concepts;
}