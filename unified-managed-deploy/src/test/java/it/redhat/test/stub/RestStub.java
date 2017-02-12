package it.redhat.test.stub;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class RestStub implements WorkItemHandler {
	
	private static final String KIE_SERVER_SERVICES_REST_SERVER_CONTAINERS = "/kie-server/services/rest/server/containers/";
	private static final String BUSINESS_CENTRAL_REST_DEPLOYMENT = "/business-central/rest/deployment/";
	private static final String BUSINESS_CENTRAL_REST_CONTROLLER_MANAGEMENT_SERVERS = "/business-central/rest/controller/management/servers/";
	
	private GetServerTemplateStub getServerTemplate = new GetServerTemplateStub();
	private BusinessCentralDeployStub businessCentralDeployStub = new BusinessCentralDeployStub();
	private ProcessServerDeployStub processServerDeployStub = new ProcessServerDeployStub();
	private ProcessServerContainerStartStub processServerContainerStartStub = new ProcessServerContainerStartStub();
	private VerifyServerStub verifyServerStub = new VerifyServerStub();

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		
		String url = (String) workItem.getParameter("Url");
		String method = (String) workItem.getParameter("Method");
		
		if ("GET".equals(method) && url.contains(BUSINESS_CENTRAL_REST_CONTROLLER_MANAGEMENT_SERVERS)) {
			getServerTemplate.executeWorkItem(workItem, manager);
		} else if ("POST".equals(method) && url.contains(BUSINESS_CENTRAL_REST_DEPLOYMENT)) {
			businessCentralDeployStub.executeWorkItem(workItem, manager);
		} else if ("PUT".equals(method) && url.contains(BUSINESS_CENTRAL_REST_CONTROLLER_MANAGEMENT_SERVERS)) {
			processServerDeployStub.executeWorkItem(workItem, manager);
		} else if ("POST".equals(method) && url.contains(BUSINESS_CENTRAL_REST_CONTROLLER_MANAGEMENT_SERVERS)) {
			processServerContainerStartStub.executeWorkItem(workItem, manager);
		} else if ("GET".equals(method) && url.contains(KIE_SERVER_SERVICES_REST_SERVER_CONTAINERS)) {
			verifyServerStub.executeWorkItem(workItem, manager);
		} else {
			throw new RuntimeException("No Rest Stub for method " + method + " and url " + url );
		}
		
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		
	}

}
