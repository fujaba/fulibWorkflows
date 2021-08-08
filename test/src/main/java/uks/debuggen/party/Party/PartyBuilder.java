package uks.debuggen.party.Party;
import java.util.LinkedHashMap;
import java.util.function.Consumer;
import uks.debuggen.party.events.*;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

public class PartyBuilder
{
   public static final String PROPERTY_MODEL = "model";
   public static final String PROPERTY_BUSINESS_LOGIC = "businessLogic";
   private PartyModel model;
   private PartyBusinessLogic businessLogic;
   protected PropertyChangeSupport listeners;

   public PartyModel getModel()
   {
      return this.model;
   }

   public PartyBuilder setModel(PartyModel value)
   {
      if (Objects.equals(value, this.model))
      {
         return this;
      }

      final PartyModel oldValue = this.model;
      this.model = value;
      this.firePropertyChange(PROPERTY_MODEL, oldValue, value);
      return this;
   }

   public PartyBusinessLogic getBusinessLogic()
   {
      return this.businessLogic;
   }

   public PartyBuilder setBusinessLogic(PartyBusinessLogic value)
   {
      if (this.businessLogic == value)
      {
         return this;
      }

      final PartyBusinessLogic oldValue = this.businessLogic;
      if (this.businessLogic != null)
      {
         this.businessLogic = null;
         oldValue.setBuilder(null);
      }
      this.businessLogic = value;
      if (value != null)
      {
         value.setBuilder(this);
      }
      this.firePropertyChange(PROPERTY_BUSINESS_LOGIC, oldValue, value);
      return this;
   }

   public String stripBrackets(String back)
   {
      if (back == null) {
         return "";
      }
      int open = back.indexOf('[');
      int close = back.indexOf(']');
      if (open >= 0 && close >= 0) {
         back = back.substring(open + 1, close);
      }
      return back;
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

   public void removeYou()
   {
      this.setBusinessLogic(null);
   }
}
