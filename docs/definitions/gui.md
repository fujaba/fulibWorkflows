# GUI

The page 'event' consists of multiple items to build a page. Out of such an event there will be generated a plain html
view styled with bootstrap v5.1.3. The layout depends on the order the items are listed in the workflow file.
Elements can only be arranged vertically. A horizontal arrangement is not possible at this point.

```yaml
- page:
    - pageName: Example Shop
    - text: Welcome to the Example shop
    - input: Name
    - input: Address
      value: Kassel
    - password: Password
    - password: Repeat password
    - button: cancel
    - button: login
      targetPage: Login
```

### Page name

```yaml
- pageName: Example Shop
```

The `pageName` item defines the name of the page. The name of a page must be unique in one file.

### Texts

```yaml
- text: Welcome to the Example shop
```

There can be multiple `text` items in a page. As the name says a text is just text that you want to be displayed on a
page. It can be a headline, a label for an input field or a divider.

### Inputs

```yaml
- input: Name

- input: Address
  value: Kassel
```

Inputs are input fields, where the text after `input:` will be the id of the html element and the placeholder displayed
in the implementation. A page can contain multiple inputs. 

It is possible to add content which will be prefilled in the mockup. For this input gets an additional attribute called `value`.

### Password Inputs

```yaml
- password: password
```

Password Inputs are a special type of the former [inputs](#Inputs). They can be used the same way, the only difference is, that the
data entered in the password field will not show the text. Instead, it will show dots instead of text.

Password Fields can be prefilled in the mockup too. For this password gets an additional attribute called `value`.

### Buttons

```yaml
- button: cancel

- button: login
  targetPage: Login
```

There can be multiple `button` items in a page. The text added after `button:` will be the text displayed inside the
button. It is not possible to have multiple Buttons with the same text inside a single page, because the text is used as
an id in the view generation.

It is also possible to add an attribute called `targetPage` to a button. The value must be a pageName.
Out of this fulibWorkflows extracts which mockup should be shown after clicking on the generated button.
This only works for HTML-Mockups in the fulibWorkflows Web-Editor on [fulib.org](https://fulib.org/workflows).


## Divs
```yaml
- div:
    - divName: test
    - text: Testerino
```