# General Definitions
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
// TODO