package org.flechaamarilla.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.flechaamarilla.model.BusinessUnit;
import org.flechaamarilla.model.InvoiceRequest;
import org.flechaamarilla.model.StandardInvoice;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

/**
 * Service that processes invoice requests and transforms them into standardized format
 */
@ApplicationScoped
@Slf4j
public class InvoiceProcessorService {

    @Inject
    BusinessUnitService businessUnitService;

    @Inject
    ObjectMapper objectMapper;

    @Inject
    TicketService ticketService;

    /**
     * Processes an invoice request by:
     * 1. Fetching the business unit data
     * 2. Fetching the ticket data using the token
     * 3. Mapping fields according to the business unit's mappings
     * 4. Creating a standardized invoice
     *
     * @param request The invoice request from the client
     * @return A standardized invoice or null if processing failed
     */
    public StandardInvoice processInvoiceRequest(InvoiceRequest request) {
        try {
            // Get the business unit
            BusinessUnit businessUnit = businessUnitService.getBusinessUnit(request.getUnidadNegocio());
            if (businessUnit == null) {
                log.error("Business unit not found: {}", request.getUnidadNegocio());
                return null;
            }

            // Get ticket data
            String ticketData = ticketService.getTicketData(request.getTokenTicket(), businessUnit.getId());
            if (ticketData == null) {
                log.error("Ticket data not found for token: {}", request.getTokenTicket());
                return null;
            }

            // Get field mappings for this business unit
            Map<String, String> fieldMappings = businessUnitService.getFieldMappings(businessUnit.getId());

            // Parse ticket data
            JsonNode ticketJson = objectMapper.readTree(ticketData);

            // Create standardized invoice
            StandardInvoice invoice = new StandardInvoice();

            // Set emitter data from business unit
            invoice.setRfcEmisor(businessUnit.getRfcEmitter());
            invoice.setNombreEmisor(businessUnit.getEmitterName());

            // Set receiver data from request
            invoice.setRfcReceptor(request.getRfc());
            invoice.setNombreReceptor(request.getNombre());
            invoice.setUsoCfdi(request.getUsoCfdi());
            invoice.setFormaPago(request.getFormaPago());

            // Set standard data
            invoice.setMetodoPago("PUE"); // Default value, can be customized
            invoice.setMoneda(businessUnit.getDefaultCurrency());
            invoice.setSerie(businessUnit.getSeries());
            invoice.setFolio(generateFolio());

            // Process concepts from ticket
            invoice.setConceptos(processTicketConcepts(ticketJson, fieldMappings));

            return invoice;
        } catch (Exception e) {
            log.error("Error processing invoice request", e);
            return null;
        }
    }

    /**
     * Processes the concepts from a ticket JSON
     *
     * @param ticketJson The JSON node containing ticket data
     * @param fieldMappings The field mappings for the business unit
     * @return List of standardized invoice concepts
     */
    private ArrayList<StandardInvoice.InvoiceConcept> processTicketConcepts(
            JsonNode ticketJson, Map<String, String> fieldMappings) {

        ArrayList<StandardInvoice.InvoiceConcept> concepts = new ArrayList<>();

        // Handle concepts/items from the ticket
        JsonNode itemsNode = ticketJson.path("items");
        if (itemsNode.isArray()) {
            for (JsonNode item : itemsNode) {
                StandardInvoice.InvoiceConcept concept = new StandardInvoice.InvoiceConcept();

                // Map fields using the fieldMappings
                for (Map.Entry<String, String> entry : fieldMappings.entrySet()) {
                    String sourceField = entry.getKey();
                    String standardField = entry.getValue();

                    if (item.has(sourceField)) {
                        JsonNode value = item.get(sourceField);
                        setConceptField(concept, standardField, value);
                    }
                }

                // Calculate importe if not set
                if (concept.getImporte() == 0 && concept.getCantidad() > 0 && concept.getValorUnitario() > 0) {
                    concept.setImporte(concept.getCantidad() * concept.getValorUnitario());
                }

                concepts.add(concept);
            }
        }

        return concepts;
    }

    /**
     * Sets a field on the concept based on the standard field name
     *
     * @param concept The concept to update
     * @param standardField The standard field name
     * @param value The value to set
     */
    private void setConceptField(StandardInvoice.InvoiceConcept concept, String standardField, JsonNode value) {
        switch (standardField) {
            case "claveProdServ":
                concept.setClaveProdServ(value.asText());
                break;
            case "descripcion":
                concept.setDescripcion(value.asText());
                break;
            case "cantidad":
                concept.setCantidad(value.asInt());
                break;
            case "unidad":
                concept.setUnidad(value.asText());
                break;
            case "valorUnitario":
                concept.setValorUnitario(value.asDouble());
                break;
            case "importe":
                concept.setImporte(value.asDouble());
                break;
            default:
                log.warn("Unknown standard field: {}", standardField);
        }
    }

    /**
     * Generates a unique folio number
     *
     * @return A unique folio string
     */
    private String generateFolio() {
        // In a real application, this would likely be a sequential number
        // For this example, we'll use a random string
        return "10" + Math.abs(UUID.randomUUID().getLeastSignificantBits() % 10000);
    }
}
