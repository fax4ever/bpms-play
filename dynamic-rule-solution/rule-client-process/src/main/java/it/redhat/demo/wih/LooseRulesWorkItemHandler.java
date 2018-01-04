package it.redhat.demo.wih;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.command.BatchExecutionCommand;
import org.kie.api.command.Command;
import org.kie.api.command.KieCommands;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.redhat.demo.model.ContainerKey;

public class LooseRulesWorkItemHandler implements WorkItemHandler {
	public final static String WORKITEMHANDLER_NAME = "LOOSE_RULES_WIH";
	
    private final KieServices kieServices = KieServices.Factory.get();
    private final KieCommands commandsFactory = kieServices.getCommands();
    private final KieContainer kieContainer;

	private final static Logger log = LoggerFactory.getLogger( LooseRulesWorkItemHandler.class );

	private KieBase kieBase;

    public LooseRulesWorkItemHandler(String groupId, String artifactId, String version) {
        this(groupId, artifactId, version, -1);
    }
    
	public LooseRulesWorkItemHandler(String groupId, String artifactId, String version, long scannerInterval) {
		kieContainer = ContainerHolder.getInstance().getContainer( new ContainerKey( groupId, artifactId, version ), scannerInterval );
	}
	
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {

		try {
			Map<String, Object> parameters = new HashMap<>(workItem.getParameters());
			
			log.trace("{} executeWorkItem parameters = {}", WORKITEMHANDLER_NAME, parameters);
			
			Map<String, Object> results = new HashMap<>();
			
			parameters.remove("TaskName");
			String kbaseName = (String) parameters.remove("kbaseName");
			
			kieBase = kieContainer.getKieBase(kbaseName);
			
			handleStateless(workItem, parameters, results);
			
			manager.completeWorkItem(workItem.getId(), results);
		} catch (Exception e) {
			e.printStackTrace();
			manager.abortWorkItem(workItem.getId());
		}
	}

	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		log.trace("{} abortWIH", WORKITEMHANDLER_NAME);
		manager.abortWorkItem(workItem.getId());
	}
	
    protected void handleStateless(WorkItem workItem, Map<String, Object> parameters, Map<String, Object> results) {
        log.debug("Evalating rules in stateless session");
        
        StatelessKieSession kieSession = kieBase.newStatelessKieSession();
        log.debug( "Create session {} {}", System.identityHashCode(kieSession), kieSession );
        
        List<Command<?>> commands = new ArrayList<Command<?>>();
        
        for (Entry<String, Object> entry : parameters.entrySet()) {
            String inputKey = workItem.getId() + "_" + entry.getKey();

            commands.add(commandsFactory.newInsert(entry.getValue(), inputKey, true, null));
        }
        commands.add(commandsFactory.newFireAllRules("Fired"));
        BatchExecutionCommand executionCommand = commandsFactory.newBatchExecution(commands);
        ExecutionResults executionResults = kieSession.execute(executionCommand);
        log.debug("{} rules fired", executionResults.getValue("Fired"));
        System.out.println("rules fired: "+executionResults.getValue("Fired"));
        for (Entry<String, Object> entry : parameters.entrySet()) {
            String inputKey = workItem.getId() + "_" + entry.getKey();
            String key = entry.getKey().replaceAll(workItem.getId() + "_", "");
            results.put(key, executionResults.getValue(inputKey));
        }
    }
}
