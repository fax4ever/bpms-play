package it.redhat.demo.stub;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class RestStub implements WorkItemHandler {
	
	private static final String DEPLOYMENT = "/business-central/rest/deployment/";
	private static final String UNDEPLOY = "/undeploy";
	private static final String PROCESS = "/processes";
	private static final String MANAGEMENT_SERVERS = "/business-central/rest/controller/management/servers/";
	private static final String QUERY_RUNTIME_PROCESS = "/business-central/rest/query/runtime/process";
	
	private GetServerTemplateStub getServerTemplate = new GetServerTemplateStub();
	private BusinessCentralDeployStub businessCentralDeployStub = new BusinessCentralDeployStub();
	private ProcessServerDeployStub processServerDeployStub = new ProcessServerDeployStub();
	private ProcessServerContainerStartStub processServerContainerStartStub = new ProcessServerContainerStartStub();
	private LoadProcessDefinitionStub loadProcessDefinitionStub = new LoadProcessDefinitionStub();
	private LoadProcessInstancesStub loadProcessInstancesStub = new LoadProcessInstancesStub();
	private DisponseOldContainerStub disponseOldContainerStub = new DisponseOldContainerStub();
	private VerifyBcDeployStub verifyBcDeployStub = new VerifyBcDeployStub();
	private UndeployOldContainerStub undeployOldContainerStub = new UndeployOldContainerStub();

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		
		String url = (String) workItem.getParameter("Url");
		String method = (String) workItem.getParameter("Method");
		
		if ("GET".equals(method) && url.contains(MANAGEMENT_SERVERS)) {
			getServerTemplate.executeWorkItem(workItem, manager);
		} else if ("POST".equals(method) && url.contains(DEPLOYMENT) && !url.contains(UNDEPLOY)) {
			businessCentralDeployStub.executeWorkItem(workItem, manager);
		} else if ("PUT".equals(method) && url.contains(MANAGEMENT_SERVERS)) {
			processServerDeployStub.executeWorkItem(workItem, manager);
		} else if ("POST".equals(method) && url.contains(MANAGEMENT_SERVERS)) {
			processServerContainerStartStub.executeWorkItem(workItem, manager);
		} else if ("GET".equals(method) && url.contains(DEPLOYMENT) && !url.contains(PROCESS)) {
			verifyBcDeployStub.executeWorkItem(workItem, manager);
		} else if ("GET".equals(method) && url.contains(DEPLOYMENT) && url.contains(PROCESS)) {
			loadProcessDefinitionStub.executeWorkItem(workItem, manager);
		} else if ("GET".equals(method) && url.contains(QUERY_RUNTIME_PROCESS)) {
			loadProcessInstancesStub.executeWorkItem(workItem, manager);
		} else if ("POST".equals(method) && url.contains(DEPLOYMENT) && url.contains(UNDEPLOY)) {
			disponseOldContainerStub.executeWorkItem(workItem, manager);
		} else if ("DELETE".equals(method) && url.contains(MANAGEMENT_SERVERS)) {
			undeployOldContainerStub.executeWorkItem(workItem, manager);
		} else {
			throw new RuntimeException("No Rest Stub for method " + method + " and url " + url );
		}
		
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		
	}

}
