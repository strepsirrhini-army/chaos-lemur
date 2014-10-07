# Chaos Lemur
This project is a self-hostable application to randomly destroy virtual machines in an environment, as an aid to resilience testing of HA environments. Its main features are:

 - Triggers on a user-defined schedule, selecting 0 or more VMs to destroy at random during each run.
 - Users can define groups of VMs, with per-group probabilities for destruction of member VMs.
 - Optionally blacklists groups to protect their members from destruction.
 - Runs against different VM environments (e.g. AWS, vSphere) using an infrastructure API.
 - Records activities to [DataDog].

Although Chaos Lemur is organized into groups, it is not possible to select an entire group for destruction. Entire groups will be destroyed over time by chance, given sufficient runs.


## Requirements
### Java, Maven
The application is written in Java 8 and packaged as a self executable JAR file. This enables it to run anywhere that Java is available. Building the application (required for deployment) requires [Maven].

### Environment Variables
Since the application is designed to work in a PaaS environment, all configuration is done with environment variables.

| Key | Description
| --- | -----------
| `AWS_ACCESSKEYID` | Gives Chaos Lemur access to your AWS infrastructure to destroy VMs. If this value is not set Chaos Lemur will use an internal destroyer for test purposes.
| `AWS_SECRETACCESSKEY` | Used with the `AWS_ACCESSKEYID` to give AWS access.
| `AWS_VPCID` | The Virtual Private Cloud ID that your VMs are running in. Available from the EC2 Management Console.
| `BLACKLIST` | A comma delimited list of groups. Any member of the group will be excluded from destruction. Default is blank, i.e. all members of all groups are eligible for destruction.
| `DATADOG_APIKEY` | Allows Chaos Lemur to log destruction events to [DataDog]. If this value is not set Chaos Lemur will redirect the output to the local logger.
| `DATADOG_APPKEY` | Used with the `DATADOG_APIKEY` to give DataDog access.
| `DEFAULT_PROBABILITY` | The default probability for a VM to be destroyed, ranging from `0.0` (will never be destroyed) to `1.0` (will always be destroyed). The probability is per run, with each run independent of any other. Default is `0.2`.
| `<GROUP>_PROBABILITY` | The probability for a given group, overriding the default. For example, `REDIS_PROBABILITY` set to `0.3` means that VMs in the redis group will be destroyed less often than a default VM.
| `SCHEDULE` | The schedule to trigger a run of Chaos Lemur. Defined using Spring cron syntax, so `0 0/10 * * * *` would run every 10 minutes. Default is  `0 0 * * * *` (once per hour, on the hour).


### Services
Chaos Lemur uses Redis to persist its status across restarts (e,g. if the PaaS environment forces a reboot). To do this it requires a [Redis Cloud instance] called `chaos-lemur-persistence`. Chaos Lemur only requires a few bytes of storage, so the smallest Redis plan should be sufficient.

## Deployment
_The following instructions assume that you have [created an account][cloud-foundry-account] and [installed the `cf` command line tool]._

In order to automate the deployment process as much as possible, the project contains a Cloud Foundry [manifest].  To deploy run the following commands:

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
| `GET  /state` | `{ "status": "[STOPPED / STARTED]" }`| `200` | Return the current status
| `POST /state` | `{ "status": "STOPPED" }` | `202` | Pause Chaos Lemur indefinitely
| `POST /state` | `{ "status": "STARTED" }` | `202` | Resume Chaos Lemur


## Developing
The project is set up as a Maven project and doesn't have any special requirements beyond that. It has been created using [IntelliJ] and contains configuration information for that environment, but should work with other IDEs.


## License

Copyright 2014 Pivotal Software, Inc. All Rights Reserved.

[Redis Cloud instance]: http://docs.run.pivotal.io/marketplace/services/rediscloud.html
[cloud-foundry-account]: https://console.run.pivotal.io/register
[DataDog]: https://www.datadoghq.com
[installed the `cf` command line tool]: http://docs.run.pivotal.io/devguide/installcf/install-go-cli.html
[IntelliJ]: http://www.jetbrains.com/idea/
[manifest]: manifest.yml
[Maven]: http://maven.apache.org
