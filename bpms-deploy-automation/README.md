# bpms-play

## bpms-deploy-automation

### deploy process 
"it.redhat.demo:bpms-deploy-automation:1.0.0-SNAPSHOT" on BC 

### create new container 
it.redhat.demo:bpms-selection-process:1.0.0

curl -v -u fabio:fabio\$739 -X POST -H 'Accept: application/json' 'http://localhost:8230/business-central/rest/runtime/it.redhat.demo:bpms-deploy-automation:1.0.0-SNAPSHOT/process/it.redhat.demo.unified-managed-deploy/start?map_bcPort=8230s&map_groupId=it.redhat.demo&map_bcHost=localhost&map_artifactId=bpms-selection-process&map_serverId=process-server&map_version=1.0.0s'

#### run some process instances

### container update 
it.redhat.demo:bpms-selection-process:1.0.0

curl -v -u fabio:fabio\$739 -X POST -H 'Accept: application/json' 'http://localhost:8230/business-central/rest/runtime/it.redhat.demo:bpms-deploy-automation:1.0.0-SNAPSHOT/process/it.redhat.demo.unified-managed-deploy/start?map_bcPort=8230s&map_groupId=it.redhat.demo&map_bcHost=localhost&map_artifactId=bpms-selection-process&map_serverId=process-server&map_version=1.0.0s'

#### run some other process instances

### mini release with migration 
it.redhat.demo:bpms-selection-process:1.0.0

curl -v -u fabio:fabio\$739 -X POST -H 'Accept: application/json' 'http://localhost:8230/business-central/rest/runtime/it.redhat.demo:bpms-deploy-automation:1.0.0-SNAPSHOT/process/it.redhat.demo.unified-managed-deploy/start?map_bcPort=8230s&map_groupId=it.redhat.demo&map_bcHost=localhost&map_artifactId=bpms-selection-process&map_serverId=process-server&map_version=1.0.1s'

## Single commands

### migration
curl -v -u fabio:fabio\$739 -X POST -H 'Accept: application/json' 'http://localhost:8230/business-central/rest/runtime/it.redhat.demo:bpms-deploy-automation:1.0.0-SNAPSHOT/process/it.redhat.demo.migration/start?map_bcPort=8230&map_bcHost=localhost&map_oldDeployment=it.redhat.demo:bpms-selection-process:1.0.0&map_newDeployment=it.redhat.demo:bpms-selection-process:1.0.1'

### undeploy
curl -v -u fabio:fabio\$739 -X POST -H 'Accept: application/json' 'http://localhost:8230/business-central/rest/runtime/it.redhat.demo:bpms-deploy-automation:1.0.0-SNAPSHOT/process/it.redhat.demo.remove-container/start?map_bcPort=8230&map_bcHost=localhost&map_serverId=process-server&map_deploymentId=it.redhat.demo:bpms-selection-process:1.0.1'

### abort all process instances
curl -v -u fabio:fabio\$739 -X POST -H 'Accept: application/json' 'http://localhost:8230/business-central/rest/runtime/it.redhat.demo:bpms-deploy-automation:1.0.0-SNAPSHOT/process/it.redhat.demo.abort-active-instances/start?map_bcPort=8230&map_bcHost=localhost&map_deploymentId=it.redhat.demo:bpms-selection-process:1.0.1'


