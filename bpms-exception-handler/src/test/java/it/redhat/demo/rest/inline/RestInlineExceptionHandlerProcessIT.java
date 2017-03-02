package it.redhat.demo.rest.inline;

import org.jbpm.process.workitem.rest.RESTWorkItemHandler;

public class RestInlineExceptionHandlerProcessIT extends RestInlineExceptionHandlerProcessTest {

	@Override
	public void before() {
		
		runtimeManager = createRuntimeManager(PROCESSES_BASE_PATH + "rest-inline-exception-handler.bpmn2");

		runtimeEngine = getRuntimeEngine();
		kieSession = runtimeEngine.getKieSession();

		kieSession.getWorkItemManager().registerWorkItemHandler("Rest", new RESTWorkItemHandler(this.getClass().getClassLoader()));
		
	}
	
}
