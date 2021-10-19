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

You can define more than one workflow in one workflow file.
Those workflows also have to be given a name.