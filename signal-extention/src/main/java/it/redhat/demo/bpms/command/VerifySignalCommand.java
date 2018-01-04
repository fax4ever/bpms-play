package it.redhat.demo.bpms.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.drools.core.command.impl.GenericCommand;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.jbpm.workflow.core.node.BoundaryEventNode;
import org.jbpm.workflow.core.node.EventNode;
import org.jbpm.workflow.core.node.EventNodeInterface;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.kie.api.definition.process.Node;
import org.kie.api.runtime.KieSession;
import org.kie.internal.command.Context;

public class VerifySignalCommand implements GenericCommand<Boolean> {

	private final static Logger LOG = LoggerFactory.getLogger( VerifySignalCommand.class );

	private final Long processInstanceId;
	private final String eventName;
	private final Object eventObject;

	public VerifySignalCommand(Long processInstanceId, String eventName, Object eventObject) {
		this.processInstanceId = processInstanceId;
		this.eventName = eventName;
		this.eventObject = eventObject;
	}

	@Override
	public Boolean execute(Context context) {

		KieSession ksession = ((KnowledgeCommandContext) context).getKieSession();
		WorkflowProcessInstanceImpl processInstance = (WorkflowProcessInstanceImpl) ksession.getProcessInstance( processInstanceId );

		if (processInstance == null) {
			return false;
		}

		List<String> activeNodeIds = processInstance.getActiveNodeIds();


		LOG.debug( "Active Node Ids: {}", activeNodeIds );

		Map<String, String> uniqueIdMap = new HashMap<>(  );

		for (Node node : processInstance.getWorkflowProcess().getNodes()) {
			String uniqueId = (String) node.getMetaData().get( "UniqueId" );
			LOG.debug( "Node {} {} {}", node.getId(), node.getName(), uniqueId );
			uniqueIdMap.put( uniqueId, node.getId() + "" );
		}

		for (Node node : processInstance.getWorkflowProcess().getNodes()) {

			if ( node instanceof EventNodeInterface && ( (EventNodeInterface) node ).acceptsEvent( eventName, eventObject ) ) {

				LOG.debug( "Found node [name {}, id {}, class {}] - accepting [event name {}, payload {}]",
						   node.getName(), node.getId(), node.getClass(), eventName, eventObject );

				if ( node instanceof BoundaryEventNode ) {

					String attachedToNodeId = ( (BoundaryEventNode) node ).getAttachedToNodeId();
					String attachedNodeId = uniqueIdMap.get( attachedToNodeId );
					LOG.debug( "BoundaryEventNode is attached to Node with UniqueID {}, NodeID {}", attachedToNodeId, attachedNodeId );

					if ( activeNodeIds.contains( attachedNodeId )) {
						LOG.debug( "BoundaryEventNode is attached to active node with id {}", attachedNodeId );
						return true;
					}

					LOG.debug( "BoundaryEventNode is attached to NOT active node with id {}", attachedNodeId );

				} else if ( node instanceof EventNode ) {

					if ( activeNodeIds.contains( node.getId() + "" )) {
						LOG.debug( "EventNode with id {} is active", node.getId() );
						return true;
					}

					LOG.debug( "EventNode with id {} is NOT active", node.getId() );

				}

			}
		}

		return false;

	}

}
