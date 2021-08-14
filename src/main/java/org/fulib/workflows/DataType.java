package org.fulib.workflows;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Collections;
import java.util.Collection;
import java.beans.PropertyChangeSupport;

public class DataType
{
   public static final String PROPERTY_DATA_TYPE_NAME = "dataTypeName";
   public static final String PROPERTY_DATA_NOTES = "dataNotes";
   public static final String PROPERTY_HANDLERS = "handlers";
   public static final String PROPERTY_EVENT_STORMING_BOARD = "eventStormingBoard";
   public static final String PROPERTY_MIGRATED_TO = "migratedTo";
   private String dataTypeName;
   private List<DataNote> dataNotes;
   private List<ServiceNote> handlers;
   private EventStormingBoard eventStormingBoard;
   protected PropertyChangeSupport listeners;
   private String migratedTo;

   public String getDataTypeName()
   {
      return this.dataTypeName;
   }

   public DataType setDataTypeName(String value)
   {
      if (Objects.equals(value, this.dataTypeName))
      {
         return this;
      }

      final String oldValue = this.dataTypeName;
      this.dataTypeName = value;
      this.firePropertyChange(PROPERTY_DATA_TYPE_NAME, oldValue, value);
      return this;
   }

   public List<DataNote> getDataNotes()
   {
      return this.dataNotes != null ? Collections.unmodifiableList(this.dataNotes) : Collections.emptyList();
   }

   public DataType withDataNotes(DataNote value)
   {
      if (this.dataNotes == null)
      {
         this.dataNotes = new ArrayList<>();
      }
      if (!this.dataNotes.contains(value))
      {
         this.dataNotes.add(value);
         value.setType(this);
         this.firePropertyChange(PROPERTY_DATA_NOTES, null, value);
      }
      return this;
   }

   public DataType withDataNotes(DataNote... value)
   {
      for (final DataNote item : value)
      {
         this.withDataNotes(item);
      }
      return this;
   }

   public DataType withDataNotes(Collection<? extends DataNote> value)
   {
      for (final DataNote item : value)
      {
         this.withDataNotes(item);
      }
      return this;
   }

   public DataType withoutDataNotes(DataNote value)
   {
      if (this.dataNotes != null && this.dataNotes.remove(value))
      {
         value.setType(null);
         this.firePropertyChange(PROPERTY_DATA_NOTES, value, null);
      }
      return this;
   }

   public DataType withoutDataNotes(DataNote... value)
   {
      for (final DataNote item : value)
      {
         this.withoutDataNotes(item);
      }
      return this;
   }

   public DataType withoutDataNotes(Collection<? extends DataNote> value)
   {
      for (final DataNote item : value)
      {
         this.withoutDataNotes(item);
      }
      return this;
   }

   public List<ServiceNote> getHandlers()
   {
      return this.handlers != null ? Collections.unmodifiableList(this.handlers) : Collections.emptyList();
   }

   public DataType withHandlers(ServiceNote value)
   {
      if (this.handlers == null)
      {
         this.handlers = new ArrayList<>();
      }
      if (!this.handlers.contains(value))
      {
         this.handlers.add(value);
         value.withHandledDataTypes(this);
         this.firePropertyChange(PROPERTY_HANDLERS, null, value);
      }
      return this;
   }

   public DataType withHandlers(ServiceNote... value)
   {
      for (final ServiceNote item : value)
      {
         this.withHandlers(item);
      }
      return this;
   }

   public DataType withHandlers(Collection<? extends ServiceNote> value)
   {
      for (final ServiceNote item : value)
      {
         this.withHandlers(item);
      }
      return this;
   }

   public DataType withoutHandlers(ServiceNote value)
   {
      if (this.handlers != null && this.handlers.remove(value))
      {
         value.withoutHandledDataTypes(this);
         this.firePropertyChange(PROPERTY_HANDLERS, value, null);
      }
      return this;
   }

   public DataType withoutHandlers(ServiceNote... value)
   {
      for (final ServiceNote item : value)
      {
         this.withoutHandlers(item);
      }
      return this;
   }

   public DataType withoutHandlers(Collection<? extends ServiceNote> value)
   {
      for (final ServiceNote item : value)
      {
         this.withoutHandlers(item);
      }
      return this;
   }

   public EventStormingBoard getEventStormingBoard()
   {
      return this.eventStormingBoard;
   }

   public DataType setEventStormingBoard(EventStormingBoard value)
   {
      if (this.eventStormingBoard == value)
      {
         return this;
      }

      final EventStormingBoard oldValue = this.eventStormingBoard;
      if (this.eventStormingBoard != null)
      {
         this.eventStormingBoard = null;
         oldValue.withoutDataTypes(this);
      }
      this.eventStormingBoard = value;
      if (value != null)
      {
         value.withDataTypes(this);
      }
      this.firePropertyChange(PROPERTY_EVENT_STORMING_BOARD, oldValue, value);
      return this;
   }

   public String getMigratedTo()
   {
      return this.migratedTo;
   }

   public DataType setMigratedTo(String value)
   {
      if (Objects.equals(value, this.migratedTo))
      {
         return this;
      }

      final String oldValue = this.migratedTo;
      this.migratedTo = value;
      this.firePropertyChange(PROPERTY_MIGRATED_TO, oldValue, value);
      return this;
   }

   public boolean firePropertyChange(String propertyName, Object oldValue, Object newValue)
   {
      if (this.listeners != null)
      {
         this.listeners.firePropertyChange(propertyName, oldValue, newValue);
         return true;
      }
      return false;
   }

   public PropertyChangeSupport listeners()
   {
      if (this.listeners == null)
      {
         this.listeners = new PropertyChangeSupport(this);
      }
      return this.listeners;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder();
      result.append(' ').append(this.getDataTypeName());
      result.append(' ').append(this.getMigratedTo());
      return result.substring(1);
   }

   public void removeYou()
   {
      this.withoutDataNotes(new ArrayList<>(this.getDataNotes()));
      this.withoutHandlers(new ArrayList<>(this.getHandlers()));
      this.setEventStormingBoard(null);
   }
}
