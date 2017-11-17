/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.redhat.demo.bpm.process.command;

import java.util.Arrays;

import org.jbpm.services.task.commands.TaskContext;
import org.jbpm.services.task.commands.UserGroupCallbackTaskCommand;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Task;
import org.kie.internal.command.Context;
import org.kie.internal.task.api.model.InternalPeopleAssignments;


public class AddPeopleAssignmentsCommand extends UserGroupCallbackTaskCommand<Void> {

    private static final long serialVersionUID = -1856489382099976731L;

    public static final int POT_OWNER = 0;
    public static final int EXCL_OWNER = 1;
    public static final int ADMIN = 2;
    
    private int type;
    private OrganizationalEntity[] entities;
    private boolean removeExisting;

    public AddPeopleAssignmentsCommand(String userId, long taskId, int type, OrganizationalEntity[] entities, boolean removeExisting) {
        super();
        setUserId(userId);
        setTaskId(taskId);
        this.type = type;
        this.entities = entities;
        this.removeExisting = removeExisting;
    }

    @Override
    public Void execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        
        Task task = context.getTaskQueryService().getTaskInstanceById( taskId);
        // security check
        /*if (!isBusinessAdmin(userId, task.getPeopleAssignments().getBusinessAdministrators(), context)) {
            throw new PermissionDeniedException( "User " + userId + " is not business admin of task " + taskId);
        }*/
        switch (type) {
            case POT_OWNER:
                if (removeExisting) {
                    task.getPeopleAssignments().getPotentialOwners().clear();
                }
                task.getPeopleAssignments().getPotentialOwners().addAll(Arrays.asList(entities));
                break;
            case EXCL_OWNER:
                if (removeExisting) {
                    ((InternalPeopleAssignments)task.getPeopleAssignments()).getExcludedOwners().clear();
                }
                ((InternalPeopleAssignments)task.getPeopleAssignments()).getExcludedOwners().addAll( Arrays.asList( entities));
                break;
            case ADMIN:
                if (removeExisting) {
                    task.getPeopleAssignments().getBusinessAdministrators().clear();
                }
                task.getPeopleAssignments().getBusinessAdministrators().addAll(Arrays.asList(entities));
                break;

            default:
                break;
        }
        doCallbackOperationForPeopleAssignments( ((InternalPeopleAssignments)task.getPeopleAssignments()), context);
        return null;
    }

}
