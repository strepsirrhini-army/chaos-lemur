# Chaos Lemur
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

### Environment Variables
Since the application is designed to work in a PaaS environment, all configuration is done with environment variables.

| Key | Description
| --- | -----------
| `<DEPLOYMENT | JOB>_PROBABILITY` | The probability for a given deployment or job, overriding the default. For example, `REDIS_PROBABILITY` set to `0.3` means that VMs in the `redis` job will be destroyed more often than a default VM.
| `AWS_ACCESSKEYID` | Gives Chaos Lemur access to your AWS infrastructure to destroy VMs.
| `AWS_SECRETACCESSKEY` | Used with the `AWS_ACCESSKEYID` to give AWS access.
| `BLACKLIST` | A comma delimited list of deployments and jobs. Any member of the deployment or job will be excluded from destruction. Default is blank, i.e. all members of all deployments and jobs are eligible for destruction.
| `DATADOG_APIKEY` | Allows Chaos Lemur to log destruction events to [DataDog][d]. If this value is not set Chaos Lemur will redirect the output to the logger at `INFO` level.
| `DATADOG_APPKEY` | Used with the `DATADOG_APIKEY` to give DataDog access.
| `DATADOG_TAGS` | A set of tags to attach to each DataDog event.
| `DEFAULT_PROBABILITY` | The default probability for a VM to be destroyed, ranging from `0.0` (will never be destroyed) to `1.0` (will always be destroyed). The probability is per run, with each run independent of any other. Default is `0.2`.
| `DIRECTOR_HOST` | The BOSH Director host to query for destruction candidates
| `DIRECTOR_PASSWORD` | Used with `DIRECTOR_HOST` to give BOSH Director access.
| `DIRECTOR_USERNAME` | Used with `DIRECTOR_HOST` to give BOSH Director access.
| `DRYRUN` | Causes Chaos Lemur to omit the _actual_ destruction of VMs, but work properly in all other respects.  The default is `false`.
| `SCHEDULE` | The schedule to trigger a run of Chaos Lemur. Defined using Spring cron syntax, so `0 0/10 * * * *` would run every 10 minutes. Default is  `0 0 * * * *` (once per hour, on the hour).
| `VSPHERE_HOST` | The vSphere host to used to destroy VMs.
| `VSPHERE_PASSWORD` | Used with `VSPHERE_HOST` to give vSphere access.
| `VSPHERE_USERNAME` | Used with `VSPHERE_HOST` to give vSphere access.


### Services
Chaos Lemur can use Redis to persist its status across restarts (e,g. if the PaaS environment forces a reboot). If a [Redis Cloud instance][r] called `chaos-lemur-persistence` exists, Chaos Lemur will use it. Chaos Lemur only requires a few bytes of storage, so the smallest Redis plan should be sufficient.

## Deployment
_The following instructions assume that you have [created an account][c] and [installed the `cf` command line tool][i]._

In order to automate the deployment process as much as possible, the project contains a Cloud Foundry [manifest][a].  To deploy run the following commands:

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
[r]: http://docs.run.pivotal.io/marketplace/services/rediscloud.html
[c]: https://console.run.pivotal.io/register
[d]: https://www.datadoghq.com
[i]: http://docs.run.pivotal.io/devguide/installcf/install-go-cli.html
[j]: http://www.jetbrains.com/idea/
[a]: manifest.yml
[m]: http://maven.apache.org
