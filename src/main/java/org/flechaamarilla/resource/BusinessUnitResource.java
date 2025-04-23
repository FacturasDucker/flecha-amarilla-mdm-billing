package org.flechaamarilla.resource;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.flechaamarilla.model.BusinessUnit;
import org.flechaamarilla.model.FieldMapping;
import org.flechaamarilla.service.BusinessUnitService;

import java.util.List;
import java.util.Map;

/**
 * REST controller for business unit management
 */
@Path("/api/business-units")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Business Unit Management", description = "Operations related to business units")
public class BusinessUnitResource {

    @Inject
    BusinessUnitService businessUnitService;

    /**
     * Gets all business units
     *
     * @return List of business units
     */
    @GET
    @Operation(summary = "Get all business units")
    public Response getAllBusinessUnits() {
        List<BusinessUnit> units = businessUnitService.listAllBusinessUnits();
        return Response.ok(units).build();
    }

    /**
     * Gets a business unit by ID
     *
     * @param id Business unit ID
     * @return The business unit
     */
    @GET
    @Path("/{id}")
    @Operation(summary = "Get business unit by ID")
    public Response getBusinessUnit(@PathParam("id") Long id) {
        BusinessUnit unit = businessUnitService.getBusinessUnit(id);
        if (unit == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(unit).build();
    }

    /**
     * Creates a new business unit
     *
     * @param businessUnit The business unit to create
     * @return The created business unit
     */
    @POST
    @Operation(summary = "Create a new business unit")
    @Transactional
    public Response createBusinessUnit(BusinessUnit businessUnit) {
        BusinessUnit created = businessUnitService.createBusinessUnit(businessUnit);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    /**
     * Updates a business unit
     *
     * @param id Business unit ID
     * @param businessUnit Updated business unit data
     * @return The updated business unit
     */
    @PUT
    @Path("/{id}")
    @Operation(summary = "Update a business unit")
    @Transactional
    public Response updateBusinessUnit(@PathParam("id") Long id, BusinessUnit businessUnit) {
        BusinessUnit updated = businessUnitService.updateBusinessUnit(id, businessUnit);
        if (updated == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(updated).build();
    }

    /**
     * Deletes a business unit
     *
     * @param id Business unit ID
     * @return Response indicating success or failure
     */
    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete a business unit")
    @Transactional
    public Response deleteBusinessUnit(@PathParam("id") Long id) {
        boolean deleted = businessUnitService.deleteBusinessUnit(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }

    /**
     * Gets all field mappings for a business unit
     *
     * @param id Business unit ID
     * @return Map of field mappings
     */
    @GET
    @Path("/{id}/mappings")
    @Operation(summary = "Get all field mappings for a business unit")
    public Response getFieldMappings(@PathParam("id") Long id) {
        BusinessUnit unit = businessUnitService.getBusinessUnit(id);
        if (unit == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Map<String, String> mappings = businessUnitService.getFieldMappings(id);
        return Response.ok(mappings).build();
    }

    /**
     * Adds a field mapping to a business unit
     *
     * @param id Business unit ID
     * @param mapping The field mapping to add
     * @return The created field mapping
     */
    @POST
    @Path("/{id}/mappings")
    @Operation(summary = "Add a field mapping to a business unit")
    @Transactional
    public Response addFieldMapping(@PathParam("id") Long id, FieldMapping mapping) {
        FieldMapping created = businessUnitService.addFieldMapping(id, mapping);
        if (created == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.CREATED).entity(created).build();
    }
}
