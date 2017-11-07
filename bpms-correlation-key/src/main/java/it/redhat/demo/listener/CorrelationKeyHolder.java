package it.redhat.demo.listener;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jbpm.process.audit.ProcessInstanceLog;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.audit.AuditService;
import org.kie.internal.runtime.manager.context.EmptyContext;

/**
 * @author Fabio Massimo Ercoli (C) 2017 Red Hat Inc.
 */
public class CorrelationKeyHolder {

	private static final Logger LOG = LoggerFactory.getLogger( CorrelationKeyHolder.class );

	private final RuntimeManager runtimeManager;

	private Map<Long, String> correlationKeys = new HashMap<>(  );

	public CorrelationKeyHolder(RuntimeManager runtimeManager) {
		this.runtimeManager = runtimeManager;
	}

	public String getCorrelationKey(Long piid) {
		int identityHashCode = System.identityHashCode(this);

		if (!correlationKeys.containsKey( piid)) {
			LOG.trace("CACHE MISS. Identity: {}. KEY NOT FOUND. Process instance id {}", identityHashCode, piid);

			addCorrelationKey( piid );

		} else {
			LOG.trace("CACHE HIT. Identity: {}. KEY FOUND. Process instance id {}", identityHashCode, piid);
		}

		return correlationKeys.get( piid );
	}

	private void addCorrelationKey(Long piid) {
		RuntimeEngine runtimeEngine = runtimeManager.getRuntimeEngine( EmptyContext.get() );
		AuditService auditService = runtimeEngine.getAuditService();

		ProcessInstanceLog pil = (ProcessInstanceLog) auditService.findProcessInstance( piid );
		if (pil == null) {
			return;
		}

		while (pil.getParentProcessInstanceId() >= 1) {
			pil = (ProcessInstanceLog) auditService.findProcessInstance( pil.getParentProcessInstanceId() );
		}
		correlationKeys.put( piid, pil.getCorrelationKey() );
	}

}
