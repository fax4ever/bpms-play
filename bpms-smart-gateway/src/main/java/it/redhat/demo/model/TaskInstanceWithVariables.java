package it.redhat.demo.model;

import org.kie.server.api.model.instance.TaskInstance;

import java.util.Map;

/**
 * Created by fabio.ercoli@redhat.com on 18/07/2017.
 */
public class TaskInstanceWithVariables {

    private TaskInstance taskInstance;
    private Map<String, Object> processInstanceVariables;

    public TaskInstanceWithVariables(TaskInstance taskInstance, Map<String, Object> processInstanceVariables) {
        this.taskInstance = taskInstance;
        this.processInstanceVariables = processInstanceVariables;
    }

    public TaskInstance getTaskInstance() {
        return taskInstance;
    }

    public Map<String, Object> getProcessInstanceVariables() {
        return processInstanceVariables;
    }

}
