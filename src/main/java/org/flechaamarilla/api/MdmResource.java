package org.flechaamarilla.api;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import org.flechaamarilla.dto.CfdiDTO;
import org.flechaamarilla.dto.InvoiceRequestDTO;
import org.flechaamarilla.model.Issuer;
import org.flechaamarilla.model.Receiver;
import org.flechaamarilla.service.MdmService;
import org.flechaamarilla.service.MessageProducerService;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.*;
import lombok.extern.slf4j.Slf4j;

/**
 * REST API for MDM operations and invoice generation
 */
@Path("/mdm")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "MDM API", description = "Operations for Master Data Management")
@Slf4j
public class MdmResource {

    @Inject
    MdmService mdmService;

    @Inject
    MessageProducerService messageProducer;

    /**
     * Register an issuer through the event-driven pipeline
     */
    @POST
    @Path("/issuer/{tenantId}")
    @Operation(summary = "Register issuer", description = "Register a new issuer entity or update an existing one")
    @APIResponse(responseCode = "202", description = "Issuer data accepted for processing",
            content = @Content(mediaType = MediaType.APPLICATION_JSON))
    @APIResponse(responseCode = "500", description = "Internal server error")
    public Response registerIssuer(
            @Parameter(description = "Tenant ID", required = true) @PathParam("tenantId") String tenantId,
            @RequestBody(description = "Issuer data", required = true) Map<String, String> issuerData) {
        try {
            String batchId = messageProducer.sendIssuerData(tenantId, issuerData);
            return Response.status(Response.Status.ACCEPTED)
                    .entity(Map.of(
                            "message", "Issuer data accepted for processing",
                            "batchId", batchId
                    ))
                    .build();
        } catch (Exception e) {
            log.error("Error registering issuer: {}", e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Error registering issuer: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Register a receiver through the event-driven pipeline
     */
    @POST
    @Path("/receiver/{tenantId}")
    @Operation(summary = "Register receiver", description = "Register a new receiver entity or update an existing one")
    @APIResponse(responseCode = "202", description = "Receiver data accepted for processing",
            content = @Content(mediaType = MediaType.APPLICATION_JSON))
    @APIResponse(responseCode = "500", description = "Internal server error")
    public Response registerReceiver(
            @Parameter(description = "Tenant ID", required = true) @PathParam("tenantId") String tenantId,
            @RequestBody(description = "Receiver data", required = true) Map<String, String> receiverData) {
        try {
            String batchId = messageProducer.sendReceiverData(tenantId, receiverData);
            return Response.status(Response.Status.ACCEPTED)
                    .entity(Map.of(
                            "message", "Receiver data accepted for processing",
                            "batchId", batchId
                    ))
                    .build();
        } catch (Exception e) {
            log.error("Error registering receiver: {}", e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Error registering receiver: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Upload CSV file - Using Reactive FileUpload instead of the deprecated MultipartForm
     */
    @POST
    @Path("/upload-csv/{entityType}/{tenantId}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Operation(summary = "Upload CSV file", description = "Upload a CSV file with entity data for batch processing")
    @APIResponse(responseCode = "202", description = "CSV data accepted for processing",
            content = @Content(mediaType = MediaType.APPLICATION_JSON))
    @APIResponse(responseCode = "400", description = "Bad request")
    @APIResponse(responseCode = "500", description = "Internal server error")
    public Response uploadCsv(
            @Parameter(description = "Entity type (issuer, receiver, product)", required = true)
            @PathParam("entityType") String entityType,
            @Parameter(description = "Tenant ID", required = true)
            @PathParam("tenantId") String tenantId,
            @Parameter(description = "CSV File") FileUpload file) {

        try (InputStream inputStream = Files.newInputStream(file.uploadedFile())) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            // Read headers
            String headerLine = reader.readLine();
            if (headerLine == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Empty CSV file"))
                        .build();
            }

            String[] headers = headerLine.split(",");

            // Process each line
            List<Map<String, String>> dataList = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");

                // Create data map
                Map<String, String> data = new HashMap<>();
                for (int i = 0; i < Math.min(headers.length, values.length); i++) {
                    data.put(headers[i].trim(), values[i].trim());
                }

                dataList.add(data);
            }

            // Send batch to message producer
            String batchId = messageProducer.sendDataBatch(tenantId, entityType, dataList);

            return Response.status(Response.Status.ACCEPTED)
                    .entity(Map.of(
                            "message", "CSV data accepted for processing",
                            "records_processed", dataList.size(),
                            "batchId", batchId
                    ))
                    .build();

        } catch (IOException e) {
            log.error("Error processing CSV file: {}", e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Error processing CSV file: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Generate CFDI invoice
     */
    @POST
    @Path("/generate-cfdi/{tenantId}")
    @Operation(summary = "Generate CFDI", description = "Generate CFDI invoice data from customer information")
    @APIResponse(responseCode = "200", description = "CFDI generated successfully",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = CfdiDTO.class)))
    @APIResponse(responseCode = "500", description = "Internal server error")
    public Response generateCfdi(
            @Parameter(description = "Tenant ID", required = true)
            @PathParam("tenantId") String tenantId,
            @RequestBody(description = "Invoice request data", required = true)
            InvoiceRequestDTO request) {
        try {
            CfdiDTO cfdi = mdmService.generateCfdi(tenantId, request);
            return Response.ok(cfdi).build();
        } catch (Exception e) {
            log.error("Error generating CFDI: {}", e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Error generating CFDI: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Get all issuers for a tenant
     */
    @GET
    @Path("/issuers/{tenantId}")
    @Operation(summary = "Get issuers", description = "Get all issuers for a tenant")
    @APIResponse(responseCode = "200", description = "List of issuers",
            content = @Content(mediaType = MediaType.APPLICATION_JSON))
    public Response getIssuers(
            @Parameter(description = "Tenant ID", required = true)
            @PathParam("tenantId") String tenantId) {
        List<Issuer> issuers = Issuer.list("tenantId", tenantId);
        return Response.ok(issuers).build();
    }

    /**
     * Get all receivers for a tenant
     */
    @GET
    @Path("/receivers/{tenantId}")
    @Operation(summary = "Get receivers", description = "Get all receivers for a tenant")
    @APIResponse(responseCode = "200", description = "List of receivers",
            content = @Content(mediaType = MediaType.APPLICATION_JSON))
    public Response getReceivers(
            @Parameter(description = "Tenant ID", required = true)
            @PathParam("tenantId") String tenantId) {
        List<Receiver> receivers = Receiver.list("tenantId", tenantId);
        return Response.ok(receivers).build();
    }

    /**
     * Health check endpoint
     */
    @GET
    @Path("/health")
    @Operation(summary = "Health check", description = "Check if the MDM service is healthy")
    @APIResponse(responseCode = "200", description = "Service is healthy")
    public Response healthCheck() {
        return Response.ok(Map.of(
                "status", "healthy",
                "timestamp", new Date().toString()
        )).build();
    }
}