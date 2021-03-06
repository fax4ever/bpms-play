package it.redhat.demo.bpm.process.wid;

import java.util.HashMap;
import java.util.List;

import it.redhat.demo.bpm.process.command.AddPeopleAssignmentsCommand;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Group;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Task;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.internal.task.api.model.InternalOrganizationalEntity;

public class RevokingTask implements WorkItemHandler {
	
	private final RuntimeManager runtimeManager;

	public RevokingTask(RuntimeManager runtimeManager) {
		this.runtimeManager = runtimeManager;
	}

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		
		String agency = (String) workItem.getParameter("agency");

		RuntimeEngine runtimeEngine = runtimeManager.getRuntimeEngine( EmptyContext.get() );

		TaskService taskService = runtimeEngine.getTaskService();

		List<Long> tasksByProcessInstanceId = taskService.getTasksByProcessInstanceId( workItem.getProcessInstanceId());
		Task task = taskService.getTaskById( tasksByProcessInstanceId.get(0));

		Group group = TaskModelProvider.getFactory().newGroup();
		((InternalOrganizationalEntity) group).setId( "INSURANCE_AGENT_ROLE_" + agency);

		OrganizationalEntity[] entities = new OrganizationalEntity[1];
		entities[0] = group;

		taskService.execute( new AddPeopleAssignmentsCommand(task.getId(), AddPeopleAssignmentsCommand.POT_OWNER, entities, true ) );
		manager.completeWorkItem(workItem.getId(), new HashMap<>());
		
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		
	}

}
