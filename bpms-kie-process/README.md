Kjar version 1.0.0-SNAPSHOT
it.redhat.demo.proc --> version 1
script task log --> System.out.println("Ciao");

Kjar version 1.0.1-SNAPSHOT
it.redhat.demo.proc --> version 2
script task log --> System.out.println("Ciao 2");

#1. Depoly container from version 1.0.0-SNAPSHOT
http://localhost:8180/kie-server/services/rest/server/containers/main
PUT
<kie-container container-id="main">
  <release-id>
    <group-id>it.redhat.demo</group-id>
    <artifact-id>bpms-kie-process</artifact-id> 
    <version>1.0.0-SNAPSHOT</version> 
  </release-id> 
</kie-container>

#2 Upgrade container to version 1.0.1-SNAPSHOT
http://localhost:8180/kie-server/services/rest/server/containers/main/release-id
POST
<release-id>
  <group-id>it.redhat.demo</group-id>
  <artifact-id>bpms-kie-process</artifact-id> 
  <version>1.0.1-SNAPSHOT</version> 
</release-id> 

response
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<response type="SUCCESS" msg="Release id successfully updated.">
    <release-id>
        <artifact-id>bpms-kie-process</artifact-id>
        <group-id>it.redhat.demo</group-id>
        <version>1.0.1-SNAPSHOT</version>
    </release-id>
</response>

#3 View container
http://localhost:8180/kie-server/services/rest/server/containers/main

response
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<response type="SUCCESS" msg="Info for container main">
    <kie-container container-id="main" status="STARTED">
        <messages>
            <content>Release id successfully updated for container main</content>
            <severity>INFO</severity>
            <timestamp>2017-01-18T18:52:34.354+01:00</timestamp>
        </messages>
        <release-id>
            <artifact-id>bpms-kie-process</artifact-id>
            <group-id>it.redhat.demo</group-id>
            <version>1.0.1-SNAPSHOT</version>
        </release-id>
        <resolved-release-id>
            <artifact-id>bpms-kie-process</artifact-id>
            <group-id>it.redhat.demo</group-id>
            <version>1.0.1-SNAPSHOT</version>
        </resolved-release-id>
        <scanner status="DISPOSED"/>
    </kie-container>
</response>

