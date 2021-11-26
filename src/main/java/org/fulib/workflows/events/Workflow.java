package org.fulib.workflows.events;
import java.util.Objects;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Collection;

public class Workflow
extends BaseNote {
   public static final String PROPERTY_NOTES = "notes";
   private List<BaseNote> notes;

   public List<BaseNote> getNotes()
   {
      return this.notes != null ? Collections.unmodifiableList(this.notes) : Collections.emptyList();
   }

   public Workflow withNotes(BaseNote value)
   {
      if (this.notes == null)
      {
         this.notes = new ArrayList<>();
      }
      if (this.notes.add(value))
      {
         this.firePropertyChange(PROPERTY_NOTES, null, value);
      }
      return this;
   }

   public Workflow withNotes(BaseNote... value)
   {
      for (final BaseNote item : value)
      {
         this.withNotes(item);
      }
      return this;
   }

   public Workflow withNotes(Collection<? extends BaseNote> value)
   {
      for (final BaseNote item : value)
      {
         this.withNotes(item);
      }
      return this;
   }

   public Workflow withoutNotes(BaseNote value)
   {
      if (this.notes != null && this.notes.removeAll(Collections.singleton(value)))
      {
         this.firePropertyChange(PROPERTY_NOTES, value, null);
      }
      return this;
   }

   public Workflow withoutNotes(BaseNote... value)
   {
      for (final BaseNote item : value)
      {
         this.withoutNotes(item);
      }
      return this;
   }

   public Workflow withoutNotes(Collection<? extends BaseNote> value)
   {
      for (final BaseNote item : value)
      {
         this.withoutNotes(item);
      }
      return this;
   }
}
