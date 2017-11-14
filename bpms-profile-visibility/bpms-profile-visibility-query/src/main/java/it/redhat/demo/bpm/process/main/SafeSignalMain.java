package it.redhat.demo.bpm.process.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.redhat.demo.bpm.process.exception.SignalNotAvilableException;
import it.redhat.demo.bpm.process.query.GatewaySettings;
import it.redhat.demo.bpm.process.query.SignalService;

public class SafeSignalMain {

	private static final Logger LOG = LoggerFactory.getLogger(AdvancedQueryMain.class);

	public static void main(String[] args) {
		
		//TODO change these values
        String username = "fabio";
        String password = "fabio$739";
        
        boolean exceptionRised = false;
        
        SignalService signalService = new SignalService(GatewaySettings.create(username, password));
        signalService.startConverasation();
        
        Long processInstanceId = signalService.startProcess();
        
        try {
        	signalService.sendSignalSafe(processInstanceId, "anotherSignal", "myanotherSignalContent2");
        } catch (SignalNotAvilableException e) {
        	LOG.info("If the signal is not available an exception will be rised");
        	exceptionRised = true;
		}
        assert(exceptionRised);
        
        signalService.sendSignalSafe(processInstanceId, "mySignal", "mySignalContent");
        
        try {
        	signalService.sendSignalSafe(processInstanceId, "mySignal", "mySignalContent2");
        } catch (SignalNotAvilableException e) {
        	LOG.info("If the signal is not available an exception will be rised");
        	exceptionRised = true;
        }
        assert(exceptionRised);
        
        signalService.sendSignalSafe(processInstanceId, "anotherSignal", "anotherSignalContent");

	}

}
