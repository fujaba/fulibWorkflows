package org.fulib.workflows.events;

import java.util.List;

/**
 * Root Class for building an event storming board from the parser
 */
public class Board {
    private List<Workflow> workflows;

    /**
     * Getter for workflows field
     * @return list of all workflows contained in a board
     */
    public List<Workflow> getWorkflows() {
        return workflows;
    }

    /**
     * Setter for workflows field
     * @param workflows containing all workflows from a board
     */
    public void setWorkflows(List<Workflow> workflows) {
        this.workflows = workflows;
    }
}
