package it.redhat.demo.mapper;

import it.redhat.demo.exception.SmartGatewayException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Created by fabio.ercoli@redhat.com on 27/03/17.
 */

@Provider
public class SmartGatewayExceptionMapper implements ExceptionMapper<SmartGatewayException> {

    @Override
    public Response toResponse(SmartGatewayException e) {
        return Response.status(Response.Status.NOT_FOUND).entity(e).build();
    }

}
