package it.redhat.demo.command;

import org.kie.api.executor.Command;
import org.kie.api.executor.CommandContext;
import org.kie.api.executor.ExecutionResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloWorldCommand implements Command {
	
	private static Logger log = LoggerFactory.getLogger(HelloWorldCommand.class);

	@Override
	public ExecutionResults execute(CommandContext ctx) throws Exception {
		log.info("Hello World from Business Command!");
		return new ExecutionResults();
	}

}
