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


## Attributes
```yaml
- class: Person
  name: String
  birthYear: int
```

<variableName>: <variableType>

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