package org.fulib.workflows.events;

import java.util.List;

/**
 * Processes Workflow note from event storming and containing all events following the workflow note
 */
public class Workflow extends BaseNote {
    private List<BaseNote> notes;

    /**
     * Getter for notes field
     * @return list of notes for this workflow
     */
    public List<BaseNote> getNotes() {
        return notes;
    }

    /**
     * Setter for notes field
     * @param notes list of notes for this workflow
     */
    public void setNotes(List<BaseNote> notes) {
        this.notes = notes;
    }
}
