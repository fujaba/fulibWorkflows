# Defining Workflows
Make sure that you already added the fulibWorkflows dependency to your project.
In case you did not do that check out the [Installation](../../README.md#Installation)

- Create a new directory ``src/gen/resources/workflows``
- Add your first workflow file with the ``.es.yaml`` file extension

## Generate classes
It is possible to use fulibWorkflows as another way to generate fulib.

```yaml
- class: Person
  firstName: String
  lastName: String
```
In this example there will be generated a class ``Person`` with the attributes ``firstName`` and ``lastName``. Both of the 
attributes are of type ``String``.

For further information on how to add associations and inheritance check out [Definitions](../definitions/README.md).

## Generate Services
This section will give a short explanation on how to create a working workflow file using the basics.

Start your workflow with a workflow event. Give it a self explaining name
```yaml
- workflow: Successfull payment
```

TODO
events and so on

For further event types and information about those check out 
[Service definitions](../definitions/eventModelling_service.md).

It is also possible to generate basic html pages with fulibWorkflows. Check out [GUI Definitions](../definitions/gui.md)