# Chaos Lemur
[![Build Status](https://travis-ci.org/strepsirrhini-army/chaos-lemur.svg)](https://travis-ci.org/strepsirrhini-army/chaos-lemur)

This project is a self-hostable application to randomly destroy virtual machines in a BOSH-managed environment, as an aid to resilience testing of high-availability systems. Its main features are:

* Triggers on a user-defined schedule, selecting 0 or more VMs to destroy at random during each run.
* Manual triggering of unscheduled destroys.
* Per-deployment and per-job probabilities for destruction of member VMs.
* Optional blacklisting of deployments and jobs to protect their members from destruction.
* Runs against different types of IaaS (e.g. AWS, vSphere) using a small infrastructure API.
* Optionally records activities to [DataDog][d].

Although Chaos Lemur recognizes deployments and jobs, it is not possible to select an entire deployment or job for destruction. Entire deployments and jobs will be destroyed over time by chance, given sufficient runs.


## Requirements
### Java, Maven
The application is written in Java 8 and packaged as a self executable JAR file. This enables it to run anywhere that Java is available. Building the application (required for deployment) requires [Maven][m].

## Configuration
Since the application is designed to work in a PaaS environment, all configuration is done with environment variables.

| Key | Description
| --- | -----------
| `<DEPLOYMENT` | `JOB_PROBABILITY` | The probability for a given deployment or job, overriding the default. For example, `REDIS_PROBABILITY` set to `0.3` means that VMs in the `redis` job will be destroyed more often than a default VM.
| `BLACKLIST` | A comma delimited list of deployments and jobs. Any member of the deployment or job will be excluded from destruction. Default is blank, i.e. all members of all deployments and jobs are eligible for destruction. Can be combined with `WHITELIST` (see below).
| `DEFAULT_PROBABILITY` | The default probability for a VM to be destroyed, ranging from `0.0` (will never be destroyed) to `1.0` (will always be destroyed). The probability is per run, with each run independent of any other. Default is `0.2`.
| `DRYRUN` | Causes Chaos Lemur to omit the _actual_ destruction of VMs, but work properly in all other respects.  The default is `false`.
| `SCHEDULE` | The schedule to trigger a run of Chaos Lemur. Defined using Spring cron syntax, so `0 0/10 * * * *` would run every 10 minutes. Default is  `0 0 * * * *` (once per hour, on the hour).
| `WHITELIST` | A comma delimited list of deployments and jobs. If specified, only members of the deployment or job will be considered for destruction. If `WHITELIST` is not specified or blank, all deployments and jobs are eligible for destruction. Default is blank. Can be combined with `BLACKLIST` (see below).

`BLACKLIST` and `WHITELIST` can be used individually as noted above. They can also be combined for more complex filtering. The list of deployments and jobs is filtered first by excluding anything _not_ in the whitelist, and then by excluding everything in the blacklist.

For example, say you had a BOSH environment with three deployments 'cf', 'redis', and 'mysql' and plan on adding additional deployments over time. You only want want the 'dea' and 'router' jobs in the 'cf' deployment to be eligible for destruction. One option is to `BLACKLIST: "redis, mysql"` as well as all the jobs in 'cf' except for 'dea' and 'router' (e.g. `BLACKLIST: "nfs_server, ccdb, uaadb, ha_proxy, ..."`) If you added any additional deployments or any new jobs were added to the 'redis' and 'mysql' deployments, you must remember to add those to the blacklist as well. Alternatively, you could `WHITELIST: "cf"` and then `BLACKLIST: "nfs_server, ccdb, uaadb, ha_proxy, ..."`.  With this approach you need only worry about managing the blacklist of 'cf' as its jobs change over time.

### Infrastructure

Chaos Lemur requires an infrastructure to be configured, so you must set either the AWS, VSPHERE, or SIMPLE_INFRASTRUCTURE values.

| Key | Description
| --- | -----------
| `AWS_ACCESSKEYID` | Gives Chaos Lemur access to your AWS infrastructure to destroy VMs.
| `AWS_REGION` | The AWS region in which to kill VM's. Default is us-east-1
| `AWS_SECRETACCESSKEY` | Used with the `AWS_ACCESSKEYID` to give AWS access.
| `DIRECTOR_HOST` | The BOSH Director host to query for destruction candidates
| `DIRECTOR_PASSWORD` | Used with `DIRECTOR_HOST` to give BOSH Director access.
| `DIRECTOR_USERNAME` | Used with `DIRECTOR_HOST` to give BOSH Director access.
| `OPENSTACK_ENDPOINT` | The openstack api endpoint to use to destroy VMs.
| `OPENSTACK_PASSWORD` | Used with `OPENSTACK_ENDPOINT` to give vSphere access.
| `OPENSTACK_TENANT`   | Used with `OPENSTACK_ENDPOINT` to give the openstack tenant VMs if the  to destroy .
| `OPENSTACK_USERNAME` | Used with `OPENSTACK_ENDPOINT` to give vSphere access.
| `SIMPLE_INFRASTRUCTURE` | Chaos Lemur will use its built-in infrastructure rather than AWS or vSphere. Useful for testing. The value for the variable is not read, but something is required for Cloud Foundry (e.g. 'true').
| `VSPHERE_HOST` | The vSphere host used to destroy VMs.
| `VSPHERE_PASSWORD` | Used with `VSPHERE_HOST` to give vSphere access.
| `VSPHERE_USERNAME` | Used with `VSPHERE_HOST` to give vSphere access.

### Reporting

| Key | Description
| --- | -----------
| `DATADOG_APIKEY` | Allows Chaos Lemur to log destruction events to [DataDog][d]. If this value is not set Chaos Lemur will redirect the output to the logger at `INFO` level.
| `DATADOG_APPKEY` | Used with the `DATADOG_APIKEY` to give DataDog access.
| `DATADOG_TAGS` | A set of tags to attach to each DataDog event.

### Security

| Key | Description
| --- | -----------
| `SECURITY_BASIC_ENABLED` | Enables authentication. Default is true.
| `SECURITY_USER_NAME` | The username for authenticating to the API. Default is `user`.
| `SECURITY_USER_PASSWORD` | The password for authenticating to the API. Default is randomly generated by Spring Boot Security and printed at INFO level when the application starts up. See the [Spring Boot security reference documentation][b] for additional details. We recommend setting your own username and password, or at least noting the default when it is logged rather than retrieving it later.

### Services
Chaos Lemur can use Redis to persist its status across restarts (e,g. if the PaaS environment forces a reboot). If a [Redis Cloud instance][r] called `chaos-lemur-persistence` exists, Chaos Lemur will use it. Chaos Lemur only requires a few bytes of storage, so the smallest Redis plan should be sufficient.

## Deployment
_The following instructions assume that you have [created an account][c] and [installed the `cf` command line tool][i]._

This version of chaos-lemur assumes you're running version of bosh that has UAA integrated. In order to use this with UAA you have to first set up a [Bosh client][k]. 

In order to automate the deployment process as much as possible, the project contains a Cloud Foundry [manifest][y]. 
Customise the `manifest.yml` file to add environment variables as follows:

    ```
    env:
        DRYRUN: <true or false>  
        AWS_ACCESSKEYID: <your AWS KEYID>
        AWS_SECRETACCESSKEY: <your AWS Secret Access Key>
        DIRECTOR_HOST: <IP of Microbosh>
        DIRECTOR_USERNAME: <user name you've set up during bosh client step>
        DIRECTOR_PASSWORD: <password for the user you've set up during bosh client step>
     ```


To deploy run the following commands:

```bash
mvn clean package
cf push
```

To confirm that Chaos Lemur has started correctly run:

```bash
cf logs chaos-lemur --recent
```


## API
Chaos Lemur is designed to run continuously, destroying VMs on a definable schedule. To help with testing and development it is possible to pause and resume destroys using its RESTful API. All data is sent and received as `application/json`.

The API requires credentials provided via Basic Authentication. See the `SECURITY_USER_NAME` and `SECURITY_USER_PASSWORD` environment variables.

| Call | Payload | Status | Description
| ---- | ------- | :----: | -----------
| `GET  /state` | `{ "status": "[STOPPED | STARTED]" }`| `200` | Return the current status
| `POST /state` | `{ "status": "STOPPED" }` | `202` | Pause Chaos Lemur indefinitely
| `POST /state` | `{ "status": "STARTED" }` | `202` | Resume Chaos Lemur
| `POST /chaos` | `{ "event": "DESTROY" }` | `202` | Initiate a round of destroys. The destroys will happen even if Chaos Lemur is stopped. Returns the `Location` of a task for the destroy.
| `GET /task` | - | `200` | Reports the above information for all tasks.
| `GET /task/{id}` | - | `200` | Reports the status (`{ "COMPLETE | IN_PROGRESS" }`), trigger (`{ MANUAL | SCHEDULED )`), start date/time, and links for task `{id}`.


## Developing
The project is set up as a Maven project and doesn't have any special requirements beyond that. It has been created using [IntelliJ][j] and contains configuration information for that environment, but should work with other IDEs.


## License
The project is released under version 2.0 of the [Apache License][a].

[a]: http://www.apache.org/licenses/LICENSE-2.0
[b]: http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-security
[c]: https://console.run.pivotal.io/register
[d]: https://www.datadoghq.com
[i]: http://docs.run.pivotal.io/devguide/installcf/install-go-cli.html
[j]: http://www.jetbrains.com/idea/
[k]: https://docs.pivotal.io/pivotalcf/1-8/customizing/opsmanager-create-bosh-client.html
[m]: http://maven.apache.org
[r]: http://docs.run.pivotal.io/marketplace/services/rediscloud.html
[y]: manifest.yml
