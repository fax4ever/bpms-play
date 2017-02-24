package it.redhat.demo.abort;

import java.util.ArrayList;
import java.util.HashMap;

import it.redhat.demo.jaxb.JaxbProcessInstance;
import it.redhat.demo.jaxb.JaxbQueryProcessInstanceResult;
import it.redhat.demo.stub.NoActionWorkItemHandlerStub;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import it.redhat.demo.jaxb.JaxbQueryProcessInstanceInfo;

public class AbortRestStub implements WorkItemHandler {
	
	private NoActionWorkItemHandlerStub noActionWid = new NoActionWorkItemHandlerStub();

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		
		HashMap<String, Object> results = new HashMap<>();
		
		String url = (String) workItem.getParameter("Url");
		String method = (String) workItem.getParameter("Method");
		
		System.out.println(url);
		System.out.println(method);
		
		if (method.equals("POST")) {
			noActionWid.executeWorkItem(workItem, manager);
			return;
		}
		
		JaxbQueryProcessInstanceResult jaxbQueryProcessInstanceResult = new JaxbQueryProcessInstanceResult();
		ArrayList<JaxbQueryProcessInstanceInfo> processInstanceList = new ArrayList<JaxbQueryProcessInstanceInfo>();
		
		addPi(processInstanceList, 1l);
		addPi(processInstanceList, 2l);
		addPi(processInstanceList, 3l);
		addPi(processInstanceList, 4l);
		addPi(processInstanceList, 5l);
		addPi(processInstanceList, 6l);
		addPi(processInstanceList, 7l);
		
		jaxbQueryProcessInstanceResult.setProcessInstanceInfoList(processInstanceList);
		results.put("Result", jaxbQueryProcessInstanceResult);
		manager.completeWorkItem(workItem.getId(), results);
		
	}

	private void addPi(ArrayList<JaxbQueryProcessInstanceInfo> processInstanceList, Long id) {
		
		JaxbQueryProcessInstanceInfo queryProcessInstance = new JaxbQueryProcessInstanceInfo();
		JaxbProcessInstance processInstance = new JaxbProcessInstance();
		processInstance.setId(id);
		queryProcessInstance.setProcessInstance(processInstance);
		processInstanceList.add(queryProcessInstance);
		
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		
	}

}
