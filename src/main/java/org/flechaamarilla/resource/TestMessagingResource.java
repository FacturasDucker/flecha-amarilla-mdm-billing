package org.flechaamarilla.resource;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import lombok.extern.slf4j.Slf4j;
import org.flechaamarilla.model.InvoiceRequest;

/**
 * Resource for testing the messaging system during development
 * This endpoint allows sending test messages to the RabbitMQ queue
 */
@Path("/api/test")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Test Endpoints", description = "Endpoints for testing purposes")
@Slf4j
public class TestMessagingResource {

    @Inject
    ObjectMapper objectMapper;

    @Inject
    @Channel("invoice-requests-out")
    Emitter<String> invoiceRequestEmitter;

    /**
     * Sends a test invoice request to the RabbitMQ queue
     *
     * @param request The invoice request to send
     * @return Response indicating success or failure
     */
    @POST
    @Path("/send-invoice-request")
    @Operation(summary = "Send a test invoice request to the queue")
    public Response sendInvoiceRequest(InvoiceRequest request) {
        try {
            String jsonRequest = objectMapper.writeValueAsString(request);
            log.info("Sending test invoice request: {}", jsonRequest);

            invoiceRequestEmitter.send(jsonRequest);

            return Response.ok()
                    .entity("Message sent successfully")
                    .build();
        } catch (Exception e) {
            log.error("Error sending test message", e);
            return Response.serverError()
                    .entity("Error sending message: " + e.getMessage())
                    .build();
        }
    }
}