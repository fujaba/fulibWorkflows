# General
This section describes events already known from Event Storming, new events and restrictions for values.

## Additions to Event Storming Events
Besides completely new events, some events from the Event Storming have been renamed.
Here you can read about the new events.

### workflow
```yaml
- workflow: Register successfull

- workflow: Register failed

- workflow: Login successfull

- workflow: Login failed
```

It is possible to define more than one workflow in one es.yaml file.
Every workflow needs to have a name displayed after the `:`.

#### Important:
There has to be at least on workflow event present at the top of the file.
If this is not the case, events before that will not be displayed.

# service
```yaml
- service: Authorization Service
```
Every service needs a name. It helps to clarify which service performs steps in a workflow.

The name of a service must not be unique.

### problem
```yaml
- problem: This part fails often

- problem: This is taking a long time

- problem: Wouldn't it be smarter to do this differently?

- problem: No one understands this
```

While you are event storming with domain experts it is a good idea to mark difficult sections in a workflow.
For this the `problem` event is introduced. It can hold information or questions about a certain point in the workflow.
This can be helpful in the development or for a future discussion with other domain experts.

## Event Storming Events
### event (original: Domain Event)
```yaml
- event: registration completed

- event: registration completed
  username: Karli
  mail: karli@business.com
```
An event is signalling an action performed in the business process.
Usually it is written in past tense.

In fulibWorkflows it is possible to enrich an event with additional data. Similar to the `data` event.

### user
```yaml
- user: Karli
```
The user is similar to the `service` event.
Every user needs a name. It helps to clarify which user performs steps in a workflow.

The name of a user must not be unique.

### policy (original: business process)
```yaml
- policy: Manage registration
```
With a policy it is possible to clarify that a certain number of steps are performed automatically due to a distinct prior event.
A policy can trigger new `events`.

### command
```yaml
- command: register clicked
```
A command was triggered by a user interacting with a `page`.

### externalSystem
```yaml
- externalSystem: Google
```
An external system describes a third-party service which is needed for a process but not developed by the company or it is
developed by a separate team.

It is similar to `service`/`user`.
