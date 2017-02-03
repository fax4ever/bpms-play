package it.redhat.demo;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;

import it.redhat.demo.handler.JndiLookupHandler;

public class JndiProcTest extends JbpmJUnitBaseTestCase {
	
	private KieSession kieSession;
	
	public JndiProcTest() {
		super(true, true);
	}
	
	@Before
	public void before() {
	
		createRuntimeManager("it/redhat/demo/jndi-proc.bpmn2");
		
		RuntimeEngine runtimeEngine = getRuntimeEngine();
		kieSession = runtimeEngine.getKieSession();
		kieSession.getWorkItemManager().registerWorkItemHandler("JndiLookupHandler", new JndiLookupHandler());
		
		InitialContext context;
		try {
			context = new InitialContext();
			context.bind("java:/jboss/env/myresource", "CIAO");
		} catch (NamingException e) {
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void test() {
		
		kieSession.startProcess("it.redhat.demo.jndi-proc");
		
	}

}
