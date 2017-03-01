package it.redhat.demo.reh;

import org.jbpm.process.workitem.rest.RESTWorkItemHandler;

public class ExceptionHandlerProcessIT extends ExceptionHandlerProcessTest {

	@Override
	public void before() {
		
		runtimeManager = createRuntimeManager(PROCESSES_BASE_PATH + "exception-handler.bpmn2");

		runtimeEngine = getRuntimeEngine();
		kieSession = runtimeEngine.getKieSession();

		kieSession.getWorkItemManager().registerWorkItemHandler("Rest", new RESTWorkItemHandler(this.getClass().getClassLoader()));
		
	}
	
}
