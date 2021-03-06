package it.redhat.demo.bpm.process.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.redhat.demo.bpm.process.exception.SignalNotAvilableException;
import it.redhat.demo.bpm.process.query.GatewaySettings;
import it.redhat.demo.bpm.process.query.SignalService;
import org.kie.server.api.model.instance.ProcessInstance;

public class SafeSignalMain {

	private static final Logger LOG = LoggerFactory.getLogger(SafeSignalMain.class);
	private static final String PROCESS_DEF = "it.redhat.demo.bpm.process.signal";

	public static void main(String[] args) {
		
		//TODO change these values
        String username = "fabio";
        String password = "fabio$739";
        
        boolean exceptionRised = false;
        
        SignalService signalService = new SignalService(GatewaySettings.create(username, password));
        signalService.startConverasation();
        
        Long processInstanceId = signalService.startProcess(PROCESS_DEF);
        
        try {
        	signalService.sendSignalSafe(processInstanceId, "anotherSignal", "myanotherSignalContent2");
        } catch (SignalNotAvilableException e) {
        	LOG.info("If the signal is not available an exception will be rised");
        	exceptionRised = true;
		}
        assert(exceptionRised);
        
        signalService.sendSignalSafe(processInstanceId, "mySignal", "mySignalContent");

		ProcessInstance processInstance = signalService.getProcessInstance( processInstanceId );

		Integer state = processInstance.getState();
		LOG.info( "state after 1 signals {}", state);
        
        try {
        	signalService.sendSignalSafe(processInstanceId, "mySignal", "mySignalContent2");
        } catch (SignalNotAvilableException e) {
        	LOG.info("If the signal is not available an exception will be rised");
        	exceptionRised = true;
        }
        assert(exceptionRised);
        
        signalService.sendSignalSafe(processInstanceId, "anotherSignal", "anotherSignalContent");

		processInstance = signalService.getProcessInstance( processInstanceId );

		state = processInstance.getState();
		LOG.info( "state after 2 signals {}", state);

	}

}
