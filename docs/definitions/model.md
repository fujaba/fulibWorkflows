# Model Definitions
Besides, the classes that will be generated through different events from a defined workflow. 
It is possible to extend those classes to contain extra fields and associations.

To see the generated code from fulib check out the [fulib documentation](https://github.com/fujaba/fulib/blob/master/docs/definitions/README.md).

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
To define a bidirectional associations you will have to define both directions.

Using the example above to define Person -> University you will have to add `<fieldNameInFirstClass>: <SecondClass>`.
After that simply add `<fieldNameInFirstClass>.back: <fieldNameInSecondClass>`.
The cardinalities one and many are both present in the example.
If you want to model a direction as a `to many` - association you will have to add square brackets to the text after `:`.
In the case that it is a `to one` - association simply miss out the square brackets.


## Inheritance
```yaml
- class: Student
  extends: Person
```

If it is necessary to use inheritance in your model it is possible to do this in yaml notation, too.
Modelling a new class you can add the `extends: <ParentClass>` item.
