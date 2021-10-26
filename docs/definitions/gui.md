# Gui Definitions

The page 'event' consists of multiple items to build a page. Out of such an event there will be generated a plain html
view without styling. The layout depends on the order the items are listed in the workflow file. Elements can only be
arranged vertically. A horizontal arrangement is not possible at this point.

```yaml
- page:
    - name: Example Shop
    - label: Welcome to the Example shop
    - input: Name
    - input: Adress
      fill: Main road 42
    - password: Password
      fill: password1337
    - password: Repeat password
    - button: cancel
    - button: login
      command: logging in
```

### Page name

```yaml
- name: Example Shop
```

The `name` item defines the name of the page that is needed for the implementation. The name of a page must not be
unique in one file.

### Labels

```yaml
- label: Welcome to the Example shop
```

There can be multiple `label` items in a page. At the name says a label is just text that you want to be displayed on a
page. It can be a headline, a label for an input field or a divider.

### Inputs

```yaml
- input: Name

- input: Adress
  fill: Main road 42
```

Inputs are input fields, where the text after `input:` will be the id of the html element and the placeholder displayed
in the implementation. A page can contain multiple Inputs. If you want to automatically fill the input with some data
you can add the `fill` element.
`fill` can be a string or a number.

### Password Inputs

```yaml
- password: Repeat password

- password: Password
  fill: password1337
```

Password Inputs are a special type of the former inputs. They can be used the same way, the only difference is, that the
data entered in the password field will not show the text. Instead, it will show dots instead of text.

### Buttons

```yaml
- button: cancel

- button: ok
  command: order registered 13:00
```

There can be multiple `button` item in a page. The text added after `button:` will be the text displayed inside the
button. It is not possible to have multiple Buttons with the same text inside a single page, because the text is used as
an id in the view generation.

The `button` item can have an optional command entry. With this it is possible to connect trigger which creates a
command event.