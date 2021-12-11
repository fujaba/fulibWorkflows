package org.fulib.workflows.events;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private List<Workflow> workflows;

    public List<Workflow> getWorkflows() {
        return workflows;
    }

    public void setWorkflows(List<Workflow> workflows) {
        this.workflows = workflows;
    }

    public void addWorkflow(Workflow workflow) {
        if (workflows == null) {
            workflows = new ArrayList<>();
        }
        workflows.add(workflow);
    }
}
