package org.flechaamarilla.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.flechaamarilla.model.InvoiceRequest;
import org.flechaamarilla.model.StandardInvoice;
import org.flechaamarilla.service.InvoiceProcessorService;

/**
 * Consumes messages from RabbitMQ, processes them, and sends the result to another queue
 */
@ApplicationScoped
@Slf4j
public class InvoiceRequestConsumer {

    @Inject
    ObjectMapper objectMapper;

    @Inject
    InvoiceProcessorService invoiceProcessorService;

    @Inject
    @Channel("invoice-data")
    Emitter<String> invoiceDataEmitter;

    /**
     * Receives message from the invoice-requests queue, processes it, and sends result to invoice-data queue
     *
     * @param message The JSON message containing an invoice request
     */
    @Incoming("invoice-requests-in")
    public void processInvoiceRequest(String message) {
        try {
            log.info("Received invoice request: {}", message);

            // Deserialize the message
            InvoiceRequest request = objectMapper.readValue(message, InvoiceRequest.class);

            // Process the request
            StandardInvoice invoice = invoiceProcessorService.processInvoiceRequest(request);

            if (invoice == null) {
                log.error("Failed to process invoice request");
                return;
            }

            // Serialize the result
            String invoiceJson = objectMapper.writeValueAsString(invoice);

            // Send to the output queue
            invoiceDataEmitter.send(invoiceJson);
            log.info("Processed invoice sent to queue: {}", invoiceJson);

        } catch (JsonProcessingException e) {
            log.error("Error processing JSON message", e);
        } catch (Exception e) {
            log.error("Unexpected error processing message", e);
        }
    }
}