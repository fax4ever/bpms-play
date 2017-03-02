package it.redhat.demo.rest.global;

import org.jbpm.process.workitem.rest.RESTWorkItemHandler;

public class RestGlobalExceptionHandlerIT extends RestGlobalExceptionHandlerTest {

	@Override
	public void before() {
		
		runtimeManager = createRuntimeManager(PROCESSES_BASE_PATH + "rest-global-exception-handler.bpmn2");

		runtimeEngine = getRuntimeEngine();
		kieSession = runtimeEngine.getKieSession();

		kieSession.getWorkItemManager().registerWorkItemHandler("Rest", new RESTWorkItemHandler(this.getClass().getClassLoader()));
		
	}
	
}
