package it.redhat.demo.rest.global;

import java.util.HashMap;

import org.jbpm.process.workitem.rest.RESTWorkItemHandler;
import org.kie.api.runtime.process.ProcessInstance;

import it.redhat.demo.model.Command;

public class RestGlobalExceptionHandlerIT extends RestGlobalExceptionHandlerTest {

	@Override
	public void before() {
		
		runtimeManager = createRuntimeManager(PROCESSES_BASE_PATH + "rest-global-exception-handler.bpmn2");

		runtimeEngine = getRuntimeEngine();
		kieSession = runtimeEngine.getKieSession();

		kieSession.getWorkItemManager().registerWorkItemHandler("Rest", new RESTWorkItemHandler(this.getClass().getClassLoader()));
		
	}
	
	@Override
	public void test() {
		
		HashMap<String, Object> parameters = new HashMap<>();
		
		Command command = new Command();
		command.setName("Fabio M.");
		command.setValue(739);
		command.setOption(false);
		
		parameters.put("command", command);
		
		ProcessInstance pinstance = kieSession.startProcess("it.redhat.demo.rest-global-exception-handler", parameters);
		assertProcessInstanceAborted(pinstance.getId());
		
	}
	
}
