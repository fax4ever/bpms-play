package it.redhat.demo.service;

import java.util.Map;

import org.kie.api.runtime.process.WorkItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionService {

	public static String exceptionParameterName = "my.exception.parameter.name";
	private static Logger log = LoggerFactory.getLogger(ExceptionService.class);
	
	public void handleException(WorkItem workItem) {
		System.out.println(
				"Handling exception caused by work item '" + workItem.getName() + "' (id: " + workItem.getId() + ")");
		Map<String, Object> params = workItem.getParameters();
		Throwable throwable = (Throwable) params.get(exceptionParameterName);
		
		log.info("exception catched {}" , throwable.getMessage());
	}

	public String throwException(String message) {
		throw new RuntimeException("Service failed with input: " + message);
	}

	public static void setExceptionParameterName(String exceptionParam) {
		exceptionParameterName = exceptionParam;
	}

}
