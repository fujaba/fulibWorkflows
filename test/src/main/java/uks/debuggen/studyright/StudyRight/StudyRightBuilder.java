package uks.debuggen.studyright.StudyRight;
import uks.debuggen.studyright.events.Event;
import uks.debuggen.studyright.events.RoomBuilt;

import java.util.Objects;
import java.beans.PropertyChangeSupport;
import java.util.LinkedHashMap;
import java.util.function.Consumer;
import uks.debuggen.studyright.events.*;

public class StudyRightBuilder
{
   public static final String PROPERTY_MODEL = "model";
   public static final String PROPERTY_BUSINESS_LOGIC = "businessLogic";
   private StudyRightModel model;
   protected PropertyChangeSupport listeners;
   private StudyRightBusinessLogic businessLogic;

   public StudyRightModel getModel()
   {
      return this.model;
   }

   public StudyRightBuilder setModel(StudyRightModel value)
   {
      if (Objects.equals(value, this.model))
      {
         return this;
      }

      final StudyRightModel oldValue = this.model;
      this.model = value;
      this.firePropertyChange(PROPERTY_MODEL, oldValue, value);
      return this;
   }

   public StudyRightBusinessLogic getBusinessLogic()
   {
      return this.businessLogic;
   }

   public StudyRightBuilder setBusinessLogic(StudyRightBusinessLogic value)
   {
      if (this.businessLogic == value)
      {
         return this;
      }

      final StudyRightBusinessLogic oldValue = this.businessLogic;
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

   public void handleRoomBuilt(Event e)
   {
      RoomBuilt event = (RoomBuilt) e;
      Room object = model.getOrCreateRoom(event.getBlockId());
      object.setCredits(event.getCredits());
      object.setUni(model.getOrCreateUniversity(event.getUni()));
      for (String name : stripBrackets(event.getDoors()).split("\\s+")) {
         if (name.equals("")) continue;
         object.withDoors(model.getOrCreateRoom(name));
      }
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

   public void handleUniversityBuilt(Event e)
   {
      UniversityBuilt event = (UniversityBuilt) e;
      University object = model.getOrCreateUniversity(event.getBlockId());
      for (String name : stripBrackets(event.getRooms()).split("\\s+")) {
         if (name.equals("")) continue;
         object.withRooms(model.getOrCreateRoom(name));
      }
   }

   public void handleStudentBuilt(Event e)
   {
      StudentBuilt event = (StudentBuilt) e;
      Student object = model.getOrCreateStudent(event.getBlockId());
      object.setName(event.getName());
      object.setBirthYear(Integer.parseInt(event.getBirthYear()));
      object.setStudentId(event.getStudentId());
      object.setUni(model.getOrCreateUniversity(event.getUni()));
   }

   public void handleTourListBuilt(Event e)
   {
      TourListBuilt event = (TourListBuilt) e;
      TourList object = model.getOrCreateTourList(event.getBlockId());
   }

   public void handleStopBuilt(Event e)
   {
      StopBuilt event = (StopBuilt) e;
      Stop object = model.getOrCreateStop(event.getBlockId());
      object.setMotivation(event.getMotivation());
      object.setRoom(event.getRoom());
      object.setPreviousStop(model.getOrCreateStop(event.getPreviousStop()));
   }

   public void handleTourBuilt(Event e)
   {
      TourBuilt event = (TourBuilt) e;
      Tour object = model.getOrCreateTour(event.getBlockId());
      object.setTourList(model.getOrCreateTourList(event.getTourList()));
      object.setStops(event.getStops());
   }
}
