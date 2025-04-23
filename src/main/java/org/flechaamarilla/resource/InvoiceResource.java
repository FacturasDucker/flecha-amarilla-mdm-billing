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
import org.flechaamarilla.model.StandardInvoice;
import org.flechaamarilla.service.InvoiceProcessorService;

/**
 * REST API for invoice operations
 */
@Path("/api/invoices")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Invoice Operations", description = "Operations related to invoice processing")
@Slf4j
public class InvoiceResource {

    @Inject
    ObjectMapper objectMapper;

    @Inject
    InvoiceProcessorService invoiceProcessorService;

    @Inject
    @Channel("invoice-requests-out")
    Emitter<String> invoiceRequestEmitter;

    /**
     * Processes an invoice request synchronously
     *
     * @param request The invoice request
     * @return The processed standard invoice
     */
    @POST
    @Path("/process")
    @Operation(summary = "Process an invoice request synchronously")
    public Response processInvoice(InvoiceRequest request) {
        try {
            log.info("Processing invoice request synchronously for: {}", request.getNombre());
            StandardInvoice invoice = invoiceProcessorService.processInvoiceRequest(request);

            if (invoice == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Failed to process invoice request")
                        .build();
            }

            return Response.ok(invoice).build();
        } catch (Exception e) {
            log.error("Error processing invoice request", e);
            return Response.serverError()
                    .entity("Error processing invoice: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Queues an invoice request for asynchronous processing
     *
     * @param request The invoice request
     * @return Response indicating the request was queued
     */
    @POST
    @Path("/queue")
    @Operation(summary = "Queue an invoice request for asynchronous processing")
    public Response queueInvoice(InvoiceRequest request) {
        try {
            String jsonRequest = objectMapper.writeValueAsString(request);
            log.info("Queueing invoice request for: {}", request.getNombre());

            invoiceRequestEmitter.send(jsonRequest);

            return Response.accepted()
                    .entity("Invoice request queued for processing")
                    .build();
        } catch (Exception e) {
            log.error("Error queueing invoice request", e);
            return Response.serverError()
                    .entity("Error queueing invoice: " + e.getMessage())
                    .build();
        }
    }
}