# General Definitions
There are two general 'events' that are only used to sort workflows for the generated html view. 

### board
```yaml
- board: payment system
```

The Board-Item has to be given a name.
You should only use one board event per workflow file.


### workflow
```yaml
- workflow: successfull payment
  
- workflow: declined payment
  
- workflow: canceled payment
```

You can define more than one workflow for one board.
Those workflows also have to be given a name.

### subprocess
```yaml
- subprocess: register new user
```

If you want to structure your workflow files, it is possible to create a subprocess event.
This event has to be given a name. The subprocess can now be written down in the separate 
file with the previous given name. For example, the file for the subprocess example above would
be a workflow file named `RegisterNewUser.es.yaml`.
It is important that the subprocess workflow file is in the same directory as it`s parent file.
