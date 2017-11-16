package it.redhat.demo.test;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;

/**
 * @author Fabio Massimo Ercoli (C) 2017 Red Hat Inc.
 */
public class TimerTest extends JbpmJUnitBaseTestCase {

	private static final String PROCESS_FOLDER = "it/redhat/demo";

	private RuntimeManager runtimeManager;
	private RuntimeEngine runtimeEngine;
	private KieSession kieSession;

	public TimerTest() {
		super(true, true);
	}



}
