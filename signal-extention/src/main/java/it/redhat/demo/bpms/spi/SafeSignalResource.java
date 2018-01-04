package it.redhat.demo.bpms.spi;

import static org.kie.server.remote.rest.common.util.RestUtils.*;


import java.text.MessageFormat;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;

import it.redhat.demo.bpms.exception.SignalNotAccepted;
import org.jbpm.services.api.DeploymentNotFoundException;
import org.jbpm.services.api.ProcessInstanceNotFoundException;
import org.kie.server.remote.rest.common.Header;
import org.kie.server.services.api.KieServerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("ext/server/containers/{id}/processes")
public class SafeSignalResource {
	
	public static final Logger logger = LoggerFactory.getLogger(SafeSignalResource.class);
	
	private final SafeSignalService safeSignalService;
	private final KieServerRegistry context;
	
	public SafeSignalResource(SafeSignalService safeSignalService, KieServerRegistry context) {
		this.safeSignalService = safeSignalService;
		this.context = context;
	}
	
	@POST
    @Path("instances/{pInstanceId}/signal/{sName}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response signalProcessInstance(@javax.ws.rs.core.Context HttpHeaders headers, @PathParam("id") String containerId,
            @PathParam("pInstanceId") Long processInstanceId, @PathParam("sName") String signalName, String eventPayload) {

        Variant v = getVariant(headers);
        String type = getContentType(headers);
        Header conversationIdHeader = buildConversationIdHeader(containerId, context, headers);
        try {

        	safeSignalService.signalProcessInstance(containerId, processInstanceId, signalName, eventPayload, type);

            return createResponse(null, v, Response.Status.OK, conversationIdHeader);

        } catch (ProcessInstanceNotFoundException e) {
            return notFound(MessageFormat.format("Could not find process instance with id \"{0}\"", processInstanceId), v, conversationIdHeader);
        } catch (DeploymentNotFoundException e) {
			return notFound(MessageFormat.format("Could not find container \"{0}\"", containerId), v, conversationIdHeader);
		} catch (SignalNotAccepted e) {
			return notFound(e.getMessage(), v, conversationIdHeader);
		} catch (Exception e) {
            logger.error("Unexpected error during processing {}", e.getMessage(), e);
            return internalServerError(MessageFormat.format("Unexpected error during processing: {0}", e.getMessage()), v, conversationIdHeader);
        }
    }
	

}
