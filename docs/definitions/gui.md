# Gui Definitions

```yaml
- page:
    - name: Shop 12:55
    - label: Shoes order
    - input: count
    - input: name
      fill: Alice
    - input: address
      fill: Wonderland 1
    - button: cancel
    - button: ok
      command: order registered 13:00
```

A page defines an ui page for the user to interact with. The page entry consists of multiple items.
Those items can only be those that are listed here. Keep in mind, that this 

There are a number of required items for a page, those consists of:

### name (exactly one)
Defines the name of the page
```yaml
- name: Shop 12:55
```

### label (at least one)
Defines a label
```yaml
- label: Shoes order
```

### button? (at least one)
Defines a button with optional event.

```yaml
- button: cancel
```

The optional event is important if you want to create a prototype from your workflow file.
If you do not add the command key with a value the button will not trigger an event.

```yaml
- button: ok
  command: order registered 13:00
```

## Optional items
### Input
Defines an input with optional fill
```yaml
- input: count
- input: name
  fill: Alice
```

### Password Input
Defines a password input with optional fill
```yaml
- password: password
- password: password
  fill: workflow42
```