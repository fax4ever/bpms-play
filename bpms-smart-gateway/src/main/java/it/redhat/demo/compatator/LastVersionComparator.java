package it.redhat.demo.compatator;

import org.kie.server.api.model.definition.ProcessDefinition;

import java.util.Comparator;

/**
 * Created by fabio.ercoli@redhat.com on 27/03/17.
 */
public class LastVersionComparator implements Comparator<ProcessDefinition> {

    @Override
    public int compare(ProcessDefinition e1, ProcessDefinition e2) {
        String containerId1 = e1.getContainerId();
        String containerId2 = e2.getContainerId();

        String version1 = containerId1.substring(containerId1.lastIndexOf(":"));
        String version2 = containerId2.substring(containerId2.lastIndexOf(":"));

        return -version1.compareTo(version2);
    }

}
