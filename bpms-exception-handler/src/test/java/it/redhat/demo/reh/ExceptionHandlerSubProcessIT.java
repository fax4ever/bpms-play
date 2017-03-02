package it.redhat.demo.reh;

import org.jbpm.process.workitem.rest.RESTWorkItemHandler;

public class ExceptionHandlerSubProcessIT extends ExceptionHandlerSubProcessTest {

	@Override
	public void before() {
		
		runtimeManager = createRuntimeManager(PROCESSES_BASE_PATH + "exception-handler-subprocess.bpmn2");

		runtimeEngine = getRuntimeEngine();
		kieSession = runtimeEngine.getKieSession();

		kieSession.getWorkItemManager().registerWorkItemHandler("Rest", new RESTWorkItemHandler(this.getClass().getClassLoader()));
		
	}
	
}
