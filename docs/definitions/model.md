# Model Definitions
It is possible to define data created in a workflow. Out of those data events it is possible to create
objectdiagrams. The more data events are in the workflow the bigger the later objectdiagrams get if the objects
are connected to each other.

Out of all objects there will be generated a classdiagram automatically.

```yaml
- data: Room r1
  topic: math
  credits: 17
  neighbors: [r2, r5]
  neighbors.back: [neighbors]

- data: Room r2
  topic: calculus
  credits: 20
  neighbors: [r1, r5]
```

## Objects
```yaml
- data: Room r1
```

Multiple objects can be defined in the yaml notation. An object always needs a class and a name.
The class has to be upper case for the first letter to keep java naming conventions.
The name must be unique in the file.

Template: `- data: <Class> <name>`

## Attributes
```yaml
- data: Room r1
  topic: math
  credits: 17
```

It is possible to add as many attributes as you like in addition to those already modelled in previous/following events.
Attributes have to be in the form `<variableName>: <variableValue>` under a data event.

The value is either `int` or `String`.

## Associations
```yaml
- data: Room r1
  neighbors: [r2]
  neighbors.back: [neighbors]

- data: Room r2
```
To define a bidirectional association you will have to define both directions when using it for a data event the first time.

Using the example above to define Person -> University you will have to add `<fieldNameInFirstClass>: <dataNameSecondClass>`.
After that simply add `<fieldNameInFirstClass>.back: <fieldNameInSecondClass>`.
The cardinalities one and many are both present in the example.
If you want to model a direction as a `to many` - association you will have to add square brackets to the text after `:`.
In the case that it is a `to one` - association simply miss out the square brackets.
