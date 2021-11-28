package org.fulib.workflows.events;

import java.util.List;

public class Workflow extends BaseNote {
    private List<BaseNote> notes;

    public List<BaseNote> getNotes() {
        return notes;
    }

    public void setNotes(List<BaseNote> notes) {
        this.notes = notes;
    }
}
