# Defining Workflows
Make sure that you already added the fulibWorkflows dependency to your project.
In case you did not do that check out the [Installation](../../README.md#Installation)

- Create a new directory `src/gen/resources/workflows`
- Add your first workflow file with the `.es.yaml` file extension
- A workflow file needs at least on `- workflow:` event

## Example Workflow
```yaml
- workflow: Register user

- user: Gonpachiro

- page:
    - pageName: Registration
    - text: Please register yourself
    - input: Name
    - input: Mail
    - password: Password
    - button: Register

- command: register user

- data: User gonpachiro
  username: gonpachiro24
  mail: gonpachiro.atds.jp

```

For more events check out [Definitions](../definitions/README.md).
