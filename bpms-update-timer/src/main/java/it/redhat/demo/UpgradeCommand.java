package it.redhat.demo;

import java.util.ArrayList;
import java.util.List;

import org.drools.core.command.impl.GenericCommand;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.drools.core.common.InternalKnowledgeRuntime;
import org.jbpm.process.instance.InternalProcessRuntime;
import org.jbpm.process.instance.timer.TimerInstance;
import org.jbpm.process.instance.timer.TimerManager;
import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.jbpm.workflow.instance.node.StateBasedNodeInstance;
import org.jbpm.workflow.instance.node.TimerNodeInstance;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.NodeInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpgradeCommand implements GenericCommand<Object> {
	
	private static final Logger LOG = LoggerFactory.getLogger(UpgradeCommand.class);
	
	private static final long serialVersionUID = 1L;
	
	private long processInstanceId;
	
	public UpgradeCommand(long processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	
	public Object execute(org.kie.internal.command.Context context) {
		KieSession ksession = ((KnowledgeCommandContext) context).getKieSession();
		RuleFlowProcessInstance wfp = (RuleFlowProcessInstance) 
			ksession.getProcessInstance(processInstanceId);
		
		List<StateBasedNodeInstance> stateBasedNodeInstance = new ArrayList<>();
		List<TimerNodeInstance> timers = new ArrayList<>();
		
		for (NodeInstance nodeInstance: wfp.getNodeInstances()) {
			if (nodeInstance instanceof TimerNodeInstance) {
				timers.add((TimerNodeInstance) nodeInstance);
			} else if (nodeInstance instanceof StateBasedNodeInstance) {
				stateBasedNodeInstance.add((StateBasedNodeInstance) nodeInstance);
			}
		}
		
		TimerManager timerManager = ((InternalProcessRuntime) ((InternalKnowledgeRuntime) ksession).getProcessRuntime()).getTimerManager();
		for (StateBasedNodeInstance ni : stateBasedNodeInstance) {
			updateStateBasedNode(ni, timerManager, wfp);
		}
		for (TimerNodeInstance ni : timers) {
			updateTimerNode(ni, timerManager, wfp);
		}
		
		
		return null;
	}
	
	public void updateStateBasedNode(StateBasedNodeInstance taskNodeInstance, TimerManager timerManager, RuleFlowProcessInstance wfp) {
		List<Long> newTimerInstances = new ArrayList<Long>();
		for (long timerInstanceId: taskNodeInstance.getTimerInstances()) {
			LOG.info("Found timer {}", timerInstanceId);
			TimerInstance oldTimerInstance = timerManager.getTimerMap().get(timerInstanceId);
			
			// remove old timer
			timerManager.cancelTimer(timerInstanceId);
			
			TimerInstance newTimerInstance = new TimerInstance();
			long delay = 1l;
			
			newTimerInstance.setDelay(delay);
			newTimerInstance.setPeriod(0);
			newTimerInstance.setTimerId(oldTimerInstance.getTimerId());
			
			// register new timer
			timerManager.registerTimer(newTimerInstance, wfp);
			
			LOG.info("Register new time! {}", delay);
			
			newTimerInstances.add(newTimerInstance.getId());
		}
		taskNodeInstance.internalSetTimerInstances(newTimerInstances);
	}
	
	public void updateTimerNode(TimerNodeInstance timerNodeInstance, TimerManager timerManager, RuleFlowProcessInstance wfp) {
		long timerInstanceId = timerNodeInstance.getTimerId();

		LOG.info("Found timer {}", timerInstanceId);
		
		TimerInstance oldTimerInstance = timerManager.getTimerMap().get(timerInstanceId);
		
		// remove old timer
		timerManager.cancelTimer(timerInstanceId);
		
		TimerInstance newTimerInstance = new TimerInstance();
		long delay = 1l;
		
		newTimerInstance.setDelay(delay);
		newTimerInstance.setPeriod(0);
		newTimerInstance.setTimerId(oldTimerInstance.getTimerId());
		
		// register new timer
		timerManager.registerTimer(newTimerInstance, wfp);
		
		LOG.info("Register new time! {}", delay);
		
		timerNodeInstance.internalSetTimerId(newTimerInstance.getId());
		
	}
}