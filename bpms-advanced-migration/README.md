# bpms-play

## bpms-advanced-migration

### Invoke process instance migration API
@POST
http://localhost:8080/kie-server/services/rest/server/containers/it.redhat.demo:bpms-advanced-migration:1.0.0-SNAPSHOT/processes/it.redhat.demo.migration/instances

Content-Type : application/json

{
	"bcHost" : "localhost",
	"bcPort" : 8230,
	"oldDeployment" : "it.redhat.demo:bpms-selection-process:1.0.0",
	"newDeployment" : "it.redhat.demo:bpms-selection-process:1.1.0",
	"nodeMappging" : {
		"Manager Interview" : "MGMT Interview",
		"Human Resource Interview" : "HR Interview"
	},
	"processDefinitionMapping" : {
		"it.redhat.demo.selection-process" : "it.redhat.demo.hiring-process"
	}
}

### Set default process instance variables
@POST
http://localhost:8080/kie-server/services/rest/server/containers/it.redhat.demo:bpms-advanced-migration:1.0.0-SNAPSHOT/processes/it.redhat.demo.defaultValueSetting/instances

Content-Type : application/json

{
	"psHost" : "localhost",
	"psPort" : 8080,
	"container" : "it.redhat.demo:bpms-selection-process:1.1.0",
	"processInstanceIdList" : [ 261 ],
	"defaultValues" : {
		"managerFeedback" : "KO"
	}
}

