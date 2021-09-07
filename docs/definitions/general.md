# General Definitions

## Required Items
### board
The name of the overall project
```yaml
- board: health experts
```

### boundedContext
The title of a sub-workflow that becomes its own service
```yaml
- boundedContext: consultations
```

### brokerTopic
Define a broker address and topic as target for subsequent events
```yaml
- brokerTopic: marburg health knowledge
```

### externalSystem
A stream of events provided by a broker under some topic
```yaml
- externalSystem: marburg health knowledge
  events: 12:00 - 13:00
```

### workflow
The title of your current workflow
```yaml
- workflow: stiko
```

### service
Action performed by user
```yaml
- service: Shop
  port: 42100
```

### subprocess
A subprocess which is defined in a seperate workflow
```yaml
- subprocess: register new user
```

### command
A command send by a user
```yaml
- command: load diseases 12:00
  names: [common cold, influenza, pneumonia]
```

### event
An event signalling that some system state is reached
```yaml
- event: user registered 12:05:03
  name: Alice
```

```yaml
- event: order registered 13:04
  order: order1300
  product: shoes
  customer: Alice
  address: Wonderland 1
```

```yaml
- event: treatment initiated
  treatment: ibuprofen 400 1-1-1
  consultation: Alice#2021-06-02T14:00
```

### policy
The following steps define the reaction of a service to some triggering command or event
```yaml
- policy: MarburgHealthSystem
  trigger: 12:00
```

## Optional Items
### class
class for data within a service
```yaml
- class: Person
  name: String
  birthYear: int
  uni: University
  uni.back: [persons]

- class: Student
  extends: Person
  studentId: String
```

### data
data object created within a service
```yaml
- data: Disease
  name: common cold
  symptoms: [runny nose, cough, hoarseness, medium fever]
  symptoms.back: [indicates]
  counterSymptoms: [chills, joint pain]
  counterSymptoms.back: [excludes]
```

### action
Defines the user who is going to perform an action
```yaml
- action: Dr Who
```

### query
Defines a query on the current system state ????
```yaml
- query: sE_BBQ 14:01:01
  result: []
```
