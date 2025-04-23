package org.flechaamarilla.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * DTO for invoice generation request
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Invoice generation request")
public class InvoiceRequestDTO {
    @Schema(description = "RFC of the customer")
    private String customerRfc;

    @Schema(description = "Customer name")
    private String name;

    @Schema(description = "Customer email")
    private String email;

    @Schema(description = "Postal code")
    private String postalCode;

    @Schema(description = "Ticket token for retrieving products")
    private String ticketToken;

    @Schema(description = "Payment form code")
    private String paymentForm;

    @Schema(description = "Invoice date")
    private String date;

    @Schema(description = "Invoice time")
    private String time;

    @Schema(description = "Tax regime code")
    private String taxRegime;

    @Schema(description = "CFDI usage code")
    private String cfdiUsage;
}