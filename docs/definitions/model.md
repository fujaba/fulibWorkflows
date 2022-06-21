# Model
It is possible to define data created in a workflow. Out of those data events it is possible to create
object diagrams. The more data events are in the workflow the bigger, the later object diagrams get.

Objects that are related to a specific service in the workflows' description will be combined and for each service
a class diagram will be generated automatically.

```yaml
- data: Room r1
  topic: math
  credits: 17
  neighbors: [r2, r5]

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

- data: Room r2
  stop: s1

- data: Stop s1
```
To define a bidirectional association you will have to define both directions when using it for a data event the first time.

Using the example above to define Room <-> Room you will have to add `<fieldNameInFirstClass>: <dataNameSecondClass>`.
The cardinalities one and many are both present in the example.
While evaluating the classes from the objects fulibWorkflows checks if additonal data to an object is either an attribute or an
association.
This is realized through looking for other data notes which have the name `r2` for example.
If an object with that name exists an association will be present in the corresponding class diagram.

For a `to many` - association you will have to add square brackets to the text after `:` as displayed in `neighbors: [r2]`.
In the case that it is a `to one` - association simply miss out the square brackets.
