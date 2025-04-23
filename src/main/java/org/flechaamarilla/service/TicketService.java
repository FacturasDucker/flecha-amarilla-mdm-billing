package org.flechaamarilla.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * Service for fetching and manipulating ticket data
 * This is a mock implementation for the MVP
 */
@ApplicationScoped
@Slf4j
public class TicketService {

    @Inject
    ObjectMapper objectMapper;

    // For the MVP, we'll store some sample data in memory
    private final Map<Long, Map<String, String>> mockTickets = initMockTickets();

    /**
     * Gets ticket data for a given token and business unit
     *
     * @param tokenTicket The ticket token
     * @param businessUnitId The business unit ID
     * @return The ticket data as a JSON string or null if not found
     */
    public String getTicketData(String tokenTicket, Long businessUnitId) {
        // In a real implementation, this would fetch data from a database or API
        try {
            Map<String, String> businessTickets = mockTickets.get(businessUnitId);
            if (businessTickets == null) {
                log.error("No tickets found for business unit: {}", businessUnitId);
                return null;
            }

            String ticketData = businessTickets.get(tokenTicket);
            if (ticketData == null) {
                log.error("Ticket not found for token: {}", tokenTicket);
                return null;
            }

            return ticketData;
        } catch (Exception e) {
            log.error("Error retrieving ticket data", e);
            return null;
        }
    }

    /**
     * Initializes mock ticket data for the MVP
     * Each business unit has different field names to simulate the real scenario
     *
     * @return A map of business unit IDs to ticket tokens and data
     */
    private Map<Long, Map<String, String>> initMockTickets() {
        Map<Long, Map<String, String>> result = new HashMap<>();

        // Business Unit 1 - Standard field names
        Map<String, String> bu1Tickets = new HashMap<>();
        bu1Tickets.put("ticket-123", createTicketData1());
        result.put(1L, bu1Tickets);

        // Business Unit 2 - Different field names
        Map<String, String> bu2Tickets = new HashMap<>();
        bu2Tickets.put("ticket-456", createTicketData2());
        result.put(2L, bu2Tickets);

        // Business Unit 3 - Yet another set of field names
        Map<String, String> bu3Tickets = new HashMap<>();
        bu3Tickets.put("ticket-789", createTicketData3());
        result.put(3L, bu3Tickets);

        return result;
    }

    /**
     * Creates sample ticket data for business unit 1
     */
    private String createTicketData1() {
        try {
            ObjectNode root = objectMapper.createObjectNode();
            root.put("ticketId", "123456");
            root.put("fecha", "2023-04-23");

            ArrayNode items = objectMapper.createArrayNode();

            ObjectNode item1 = objectMapper.createObjectNode();
            item1.put("claveProdServ", "10101501");
            item1.put("descripcion", "Servicio de consultoría");
            item1.put("cantidad", 1);
            item1.put("unidad", "Servicio");
            item1.put("valorUnitario", 5000.0);
            item1.put("importe", 5000.0);
            items.add(item1);

            ObjectNode item2 = objectMapper.createObjectNode();
            item2.put("claveProdServ", "10101501");
            item2.put("descripcion", "Desarrollo de software");
            item2.put("cantidad", 2);
            item2.put("unidad", "Hora");
            item2.put("valorUnitario", 1000.0);
            item2.put("importe", 2000.0);
            items.add(item2);

            root.set("items", items);

            return objectMapper.writeValueAsString(root);
        } catch (Exception e) {
            log.error("Error creating mock ticket data", e);
            return "{}";
        }
    }

    /**
     * Creates sample ticket data for business unit 2 with different field names
     */
    private String createTicketData2() {
        try {
            ObjectNode root = objectMapper.createObjectNode();
            root.put("id", "456789");
            root.put("date", "2023-04-23");

            ArrayNode products = objectMapper.createArrayNode();

            ObjectNode product1 = objectMapper.createObjectNode();
            product1.put("productCode", "10101501");
            product1.put("productName", "Servicio de consultoría");
            product1.put("qty", 1);
            product1.put("unit", "Servicio");
            product1.put("price", 5000.0);
            product1.put("total", 5000.0);
            products.add(product1);

            ObjectNode product2 = objectMapper.createObjectNode();
            product2.put("productCode", "10101501");
            product2.put("productName", "Desarrollo de software");
            product2.put("qty", 2);
            product2.put("unit", "Hora");
            product2.put("price", 1000.0);
            product2.put("total", 2000.0);
            products.add(product2);

            root.set("products", products);

            return objectMapper.writeValueAsString(root);
        } catch (Exception e) {
            log.error("Error creating mock ticket data", e);
            return "{}";
        }
    }

    /**
     * Creates sample ticket data for business unit 3 with yet another set of field names
     */
    private String createTicketData3() {
        try {
            ObjectNode root = objectMapper.createObjectNode();
            root.put("folio", "789012");
            root.put("fechaEmision", "2023-04-23");

            ArrayNode lineas = objectMapper.createArrayNode();

            ObjectNode linea1 = objectMapper.createObjectNode();
            linea1.put("claveProducto", "10101501");
            linea1.put("concepto", "Servicio de consultoría");
            linea1.put("cantidadProducto", 1);
            linea1.put("unidadMedida", "Servicio");
            linea1.put("precioUnitario", 5000.0);
            linea1.put("precioTotal", 5000.0);
            lineas.add(linea1);

            ObjectNode linea2 = objectMapper.createObjectNode();
            linea2.put("claveProducto", "10101501");
            linea2.put("concepto", "Desarrollo de software");
            linea2.put("cantidadProducto", 2);
            linea2.put("unidadMedida", "Hora");
            linea2.put("precioUnitario", 1000.0);
            linea2.put("precioTotal", 2000.0);
            lineas.add(linea2);

            root.set("lineas", lineas);

            return objectMapper.writeValueAsString(root);
        } catch (Exception e) {
            log.error("Error creating mock ticket data", e);
            return "{}";
        }
    }
}
