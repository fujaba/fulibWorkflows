package org.fulib.workflows.events;

/**
 * BaseNote is the base class for every other kind of note in an event storming board
 */
public class BaseNote {
    private String name;
    private int index;

    /**
     * Getter for name field
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for name field
     * @param name String containing the name of the note
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for index field
     * @return index
     */
    public int getIndex() {
        return index;
    }

    /**
     * Setter for index field
     * @param index of the note in the board object
     */
    public void setIndex(int index) {
        this.index = index;
    }
}
