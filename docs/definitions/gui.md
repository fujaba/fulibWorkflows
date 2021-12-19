# Gui Definitions

The page 'event' consists of multiple items to build a page. Out of such an event there will be generated a plain html
view styled with bootstrap 4.6. The layout depends on the order the items are listed in the workflow file.
Elements can only be arranged vertically. A horizontal arrangement is not possible at this point.

```yaml
- page:
    - name: Example Shop
    - text: Welcome to the Example shop
    - input: Name
    - input: Adress
    - password: Password
    - password: Repeat password
    - button: cancel
    - button: login
```

### Page name

```yaml
- name: Example Shop
```

The `name` item defines the name of the page. The name of a page must not be unique in one file.

### Texts

```yaml
- text: Welcome to the Example shop
```

There can be multiple `text` items in a page. As the name says a text is just text that you want to be displayed on a
page. It can be a headline, a label for an input field or a divider.

### Inputs

```yaml
- input: Name
```

Inputs are input fields, where the text after `input:` will be the id of the html element and the placeholder displayed
in the implementation. A page can contain multiple inputs.

### Password Inputs

```yaml
- password: password
```

Password Inputs are a special type of the former [inputs](#Inputs). They can be used the same way, the only difference is, that the
data entered in the password field will not show the text. Instead, it will show dots instead of text.

### Buttons

```yaml
- button: cancel
```

There can be multiple `button` items in a page. The text added after `button:` will be the text displayed inside the
button. It is not possible to have multiple Buttons with the same text inside a single page, because the text is used as
an id in the view generation.