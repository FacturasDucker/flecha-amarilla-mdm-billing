package org.flechaamarilla.model;

import lombok.Data;

/**
 * Represents the data sent by a client requesting an invoice
 */
@Data
public class InvoiceRequest {
    private String rfc;
    private String nombre;
    private String correo;
    private String cp;
    private String formaPago;
    private String tokenTicket;
    private String regimenFiscal;
    private String usoCfdi;
    private Long unidadNegocio; // Business unit ID
}