package org.flechaamarilla.model;

import lombok.Data;
import java.util.List;

/**
 * The standardized invoice format to be sent to the invoice generation service
 */
@Data
public class StandardInvoice {
    private String rfcEmisor;
    private String nombreEmisor;
    private String rfcReceptor;
    private String nombreReceptor;
    private String usoCfdi;
    private String formaPago;
    private String metodoPago;
    private String moneda;
    private String serie;
    private String folio;
    private List<InvoiceConcept> conceptos;

    @Data
    public static class InvoiceConcept {
        private String claveProdServ;
        private String descripcion;
        private int cantidad;
        private String unidad;
        private double valorUnitario;
        private double importe;
    }
}
