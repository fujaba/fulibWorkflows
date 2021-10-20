# Model Definitions
Besides, the classes that will be generated through different events from a defined workflow. 
It is possible to extend those classes to contain extra fields and associations. 

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

## Classes
```yaml
- class: Person
```

Multiple classes can be defined in the yaml notation. A class always needs a name. That name has to be upper case for the first letter to keep java naming conventions.

## Attributes
```yaml
- class: Person
  name: String
  birthYear: int
```

It is possible to add as many attributes as you like in addition to those already modelled in previous/following events.
Attributes have to be in the form `<variableName>: <variableType>` under a class event. 

## Associations
```yaml
- class: Person
  uni: University
  uni.back: [persons]
```

<variableName>: <variableType>

If [<variableType>] -> To n association so it will be generated 

## Inheritance
```yaml
- class: Student
  extends: Person
```

If it is necessary to use inheritance in your model it is possible to do this in yaml notation, too.
Modelling a new class you can add the `extends: <ParentClass>`