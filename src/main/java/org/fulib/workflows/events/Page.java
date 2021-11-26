package org.fulib.workflows.events;
import java.util.Objects;
import java.util.Map;
import java.beans.PropertyChangeSupport;

public class Page
extends BaseNote {
   public static final String PROPERTY_CONTENT = "content";
   private Map<String, String> content;

   public Map<String, String> getContent()
   {
      return this.content;
   }

   public Page setContent(Map<String, String> value)
   {
      if (Objects.equals(value, this.content))
      {
         return this;
      }

      final Map<String, String> oldValue = this.content;
      this.content = value;
      this.firePropertyChange(PROPERTY_CONTENT, oldValue, value);
      return this;
   }
}
