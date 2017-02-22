# bpms-play

## bpms-deploy-automation

(1) Deploy version 1:

curl -v -u fabio:fabio\$739 -X POST -H 'Accept: application/json' 'http://localhost:8230/business-central/rest/runtime/it.redhat.demo:bpms-deploy-automation:1.0.0-SNAPSHOT/process/it.redhat.demo.unified-managed-deploy/start?map_bcPort=8230s&map_groupId=it.redhat.demo&map_bcHost=localhost&map_artifactId=bpms-selection-process&map_serverId=process-server&map_version=1.0.0s'

(2) Run some process instances using BC or PS runtime

(3) Deploy version 2:

curl -v -u fabio:fabio\$739 -X POST -H 'Accept: application/json' 'http://localhost:8230/business-central/rest/runtime/it.redhat.demo:bpms-deploy-automation:1.0.0-SNAPSHOT/process/it.redhat.demo.unified-managed-deploy/start?map_bcPort=8230s&map_groupId=it.redhat.demo&map_bcHost=localhost&map_artifactId=bpms-selection-process&map_serverId=process-server&map_version=1.0.1s'

This apply pi migration.
