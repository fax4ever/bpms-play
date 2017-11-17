package it.redhat.demo.bpm.process.command;

import org.drools.core.command.impl.GenericCommand;
import org.kie.internal.command.Context;

public class ResetTaskCommand implements GenericCommand<Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4752750777704268452L;
	
	private final long taskInstanceId;
	
	public ResetTaskCommand(long taskInstanceId) {
		this.taskInstanceId = taskInstanceId;
	}

	@Override
	public Object execute(Context context) {
		// TODO Auto-generated method stub
		return null;
	}

}
