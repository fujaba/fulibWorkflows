package org.fulib.yaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YamlIdMap extends IdMap
{
   // =============== Constants ===============

   private static final String REMOVE = "remove";

   // =============== Fields ===============

   private String yaml;
   private boolean decodingPropertyChange;

   private Yamler yamler = new Yamler();

   private HashMap<String, String> attrTimeStamps = new HashMap<>();

   private String yamlChangeText;

   private boolean encodeWithSimpleClassNames = false;

   public YamlIdMap encodeWithSimpleClassNames()
   {
      this.encodeWithSimpleClassNames = true;
      return this;
   }

   // =============== Constructors ===============

   /**
    * @param packageName
    *    the names of the package in which model classes reside
    *
    * @since 1.2
    */
   public YamlIdMap(String packageName)
   {
      this(new ReflectorMap(packageName));
   }

   /**
    * @param packageNames
    *    the names of the packages in which model classes reside
    */
   public YamlIdMap(String... packageNames)
   {
      this(new ReflectorMap(packageNames));
   }

   /**
    * @param packageNames
    *    the names of the packages in which model classes reside
    *
    * @since 1.2
    */
   public YamlIdMap(Collection<String> packageNames)
   {
      this(new ReflectorMap(packageNames));
   }

   /**
    * @since 1.2
    */
   public YamlIdMap(ReflectorMap reflectorMap)
   {
      super(reflectorMap);
   }

   // =============== Properties ===============


   public LinkedHashMap<String, Object> getObjIdMap()
   {
      return this.objIdMap;
   }


   public LinkedHashMap<Object, String> getIdObjMap()
   {
      return this.idObjMap;
   }

   /**
    * @deprecated since 1.2; use {@link #getAttributeTimeStamp(String)} instead
    */
   @Deprecated
   public HashMap<String, String> getAttrTimeStamps()
   {
      return this.attrTimeStamps;
   }

   /**
    * @deprecated since 1.2; use {@link #setUserId(String)} instead
    */
   @Deprecated
   public YamlIdMap withUserId(String userId)
   {
      this.userId = userId;
      return this;
   }

   public boolean isDecodingPropertyChange()
   {
      return this.decodingPropertyChange;
   }

   public void setDecodingPropertyChange(boolean decodingPropertyChange)
   {
      this.decodingPropertyChange = decodingPropertyChange;
   }

   // =============== Methods ===============

   // --------------- CSV ---------------

   public Object decodeCSV(String fileName)
   {
      try
      {
         byte[] bytes = Files.readAllBytes(Paths.get(fileName));

         String csvText = new String(bytes);

         String yamlText = this.convertCsv2Yaml(csvText);

         // System.err.println(yamlText);

         return this.decode(yamlText);
      }
      catch (IOException e)
      {
         Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
      }

      return null;
   }

   private String convertCsv2Yaml(String csvText)
   {
      String[] split = csvText.split(";");

      for (int i = 0; i < split.length; i++)
      {
         String token = split[i];

         if (token.startsWith("\"") && token.endsWith("\""))
         {
            // already done
            continue;
         }

         if (token.startsWith("\"") && !token.endsWith("\""))
         {
            // there is a semicolon within "   ;   " , recombine it
            int j = i;
            String nextToken;
            while (true)
            {
               j++;
               nextToken = split[j];
               split[j] = "";
               token = token + ";" + nextToken;
               if (nextToken.endsWith("\""))
               {
                  split[i] = token;
                  i = j;
                  break;
               }
            }
            continue;
         }

         if (token.trim().length() == 0)
         {
            continue;
         }

         Pattern pattern = Pattern.compile("\\s");
         Matcher matcher = pattern.matcher(token.trim());
         boolean found = matcher.find();

         if (found)
         {
            token = YamlGenerator.encapsulate(token);
            split[i] = token;
         }
      }

      StringBuilder buf = new StringBuilder();

      for (String str : split)
      {
         buf.append(str).append(" ");
      }

      return buf.toString();
   }

   // --------------- Decoding ---------------

   public Object decode(String yaml, Object root)
   {
      this.putObject(root);
      Object decodedRoot = this.decode(yaml);

      if (decodedRoot != root)
      {
         throw new RuntimeException("Object passed as root does not match the first object in the yaml string.\n"
               + "Ensure that the type of the passed root and the first object in the yaml string match. \n"
               + "Ensure that the key of the passed root and the key of the first object in tha yaml string match. \n"
               + "You get the key of the passed root object via 'String key = getOrCreateKey(root);'\n");
      }

      return root;
   }

   public Object decode(String yaml)
   {
      this.decodingPropertyChange = false;
      this.yamlChangeText = null;

      this.yaml = yaml;
      Object root;

      this.yamler = new Yamler(yaml);

      if (!"-".equals(this.yamler.getCurrentToken()))
      {
         return this.yamler.decode(yaml);
      }

      root = this.parseObjectIds();

      this.yamler = new Yamler(yaml);

      this.parseObjectAttrs();

      // reset property change decoding
      this.setDecodingPropertyChange(false);

      this.yamlChangeText = null;

      return root;
   }

   // --------------- Parsing ---------------

   private void parseObjectAttrs()
   {
      while (this.yamler.getCurrentToken() != null)
      {
         if (!"-".equals(this.yamler.getCurrentToken()))
         {
            this.yamler.printError("'-' expected");
            this.yamler.nextToken();
            continue;
         }

         String key = this.yamler.nextToken();

         if (key.endsWith(":"))
         {
            // usual
            this.parseUsualObjectAttrs();
         }
         else
         {
            this.parseObjectTableAttrs();
         }
      }
   }

   private void parseObjectTableAttrs()
   {
      // skip column names
      String className = this.yamler.getCurrentToken();

      Reflector creator = this.reflectorMap.getReflector(className);
      this.yamler.nextToken();

      ArrayList<String> colNameList = new ArrayList<>();

      while (this.yamler.getCurrentToken() != null && this.yamler.getLookAheadToken() != null
            && this.yamler.getLookAheadToken().endsWith(":"))
      {
         String colName = this.yamler.stripColon(this.yamler.getCurrentToken());
         colNameList.add(colName);
         this.yamler.nextToken();
      }

      while (this.yamler.getCurrentToken() != null && !"-".equals(this.yamler.getCurrentToken()))
      {
         String objectId = this.yamler.stripColon(this.yamler.getCurrentToken());
         this.yamler.nextToken();

         Object obj = this.objIdMap.get(objectId);

         // column values
         int colNum = 0;
         while (this.yamler.getCurrentToken() != null && !this.yamler.getCurrentToken().endsWith(":") && !"-".equals(
               this.yamler.getCurrentToken()))
         {
            String attrName = colNameList.get(colNum);

            if (this.yamler.getCurrentToken().startsWith("["))
            {
               String value = this.yamler.getCurrentToken().substring(1);
               if ("".equals(value.trim()))
               {
                  value = this.yamler.nextToken();
               }
               this.setValue(creator, obj, attrName, value);

               while (this.yamler.getCurrentToken() != null && !this.yamler.getCurrentToken().endsWith("]"))
               {
                  this.yamler.nextToken();
                  value = this.yamler.getCurrentToken();
                  if (this.yamler.getCurrentToken().endsWith("]"))
                  {
                     value = this.yamler.getCurrentToken().substring(0, this.yamler.getCurrentToken().length() - 1);
                  }
                  if (!"".equals(value.trim()))
                  {
                     this.setValue(creator, obj, attrName, value);
                  }
               }
            }
            else
            {
               this.setValue(creator, obj, attrName, this.yamler.getCurrentToken());
            }
            colNum++;
            this.yamler.nextToken();
         }
      }
   }

   private void parseUsualObjectAttrs()
   {
      String objectId = this.yamler.stripColon(this.yamler.getCurrentToken());
      String className = this.yamler.nextToken();
      this.yamler.nextToken();

      if (className.endsWith(".remove"))
      {
         this.objIdMap.remove(objectId);

         // skip time stamp, if necessary
         while (this.yamler.getCurrentToken() != null && !"-".equals(this.yamler.getCurrentToken()))
         {
            this.yamler.nextToken();
         }
         return;
      }

      if (".Map".equals(className))
      {
         YamlObject yamlObj = (YamlObject) this.objIdMap.get(objectId);
         Map<String, Object> map = yamlObj.getProperties();

         while (this.yamler.getCurrentToken() != null && !"-".equals(this.yamler.getCurrentToken()))
         {
            String attrName = this.yamler.stripColon(this.yamler.getCurrentToken());
            this.yamler.nextToken();

            if (map == null)
            {
               // no object created by parseObjectIds. Object has been removed.
               // ignore attr changes
               while (this.yamler.getCurrentToken() != null && !this.yamler.getCurrentToken().endsWith(":")
                     && !"-".equals(this.yamler.getCurrentToken()))
               {
                  this.yamler.nextToken();
               }
               continue;
            }

            // many values
            ArrayList<Object> previousValue = null;
            while (this.yamler.getCurrentToken() != null && !this.yamler.getCurrentToken().endsWith(":") && !"-".equals(
                  this.yamler.getCurrentToken()))
            {
               String attrValue = this.yamler.getCurrentToken();

               Object target = this.objIdMap.get(attrValue);

               if (target != null)
               {
                  if (previousValue != null)
                  {
                     previousValue.add(target);
                     map.put(attrName, previousValue);
                  }
                  else
                  {
                     map.put(attrName, target);
                     previousValue = new ArrayList<>();
                     previousValue.add(target);
                  }
               }
               else
               {
                  if (previousValue != null)
                  {
                     previousValue.add(attrValue);
                     map.put(attrName, previousValue);
                  }
                  else
                  {
                     map.put(attrName, attrValue);
                     previousValue = new ArrayList<>();
                     previousValue.add(attrValue);
                  }
               }

               this.yamler.nextToken();
            }
         }
      }
      else
      {
         Reflector reflector = this.reflectorMap.getReflector(className);

         Object obj = this.objIdMap.get(objectId);

         // read attributes
         while (this.yamler.getCurrentToken() != null && !"-".equals(this.yamler.getCurrentToken()))
         {
            String attrName = this.yamler.stripColon(this.yamler.getCurrentToken());
            this.yamler.nextToken();

            if (obj == null)
            {
               // no object created by parseObjectIds. Object has been removed.
               // ignore attr changes
               while (this.yamler.getCurrentToken() != null && !this.yamler.getCurrentToken().endsWith(":")
                     && !"-".equals(this.yamler.getCurrentToken()))
               {
                  this.yamler.nextToken();
               }
               continue;
            }

            // many values
            while (this.yamler.getCurrentToken() != null && !this.yamler.getCurrentToken().endsWith(":") && !"-".equals(
                  this.yamler.getCurrentToken()))
            {
               String attrValue = this.yamler.getCurrentToken();

               if (this.yamler.getLookAheadToken() != null && this.yamler.getLookAheadToken().endsWith(".time:"))
               {
                  String propWithTime = this.yamler.nextToken();
                  String newTimeStamp = this.yamler.nextToken();
                  String oldTimeStamp = this.attrTimeStamps.get(objectId + "." + attrName);

                  if (oldTimeStamp == null || oldTimeStamp.compareTo(newTimeStamp) <= 0)
                  {
                     this.setDecodingPropertyChange(true);

                     if (this.yamlChangeText == null)
                     {
                        this.yamlChangeText = this.yaml;
                     }

                     this.setValue(reflector, obj, attrName, attrValue);
                     this.attrTimeStamps.put(objectId + "." + attrName, newTimeStamp);
                  }
               }
               else
               {
                  this.setValue(reflector, obj, attrName, attrValue);
               }

               this.yamler.nextToken();
            }
         }
      }
   }

   private void setValue(Reflector reflector, Object obj, String attrName, String attrValue)
   {
      String type = "new";

      if (attrName.endsWith(".remove"))
      {
         attrName = attrName.substring(0, attrName.length() - ".remove".length());

         if (reflector.getValue(obj, attrName) instanceof Collection)
         {
            type = REMOVE;
         }
         else
         {
            attrValue = null;
         }
      }

      try
      {
         Object setResult = reflector.setValue(obj, attrName, attrValue, type);

         if (setResult == null)
         {
            Object targetObj = this.objIdMap.get(attrValue);
            if (targetObj != null)
            {
               reflector.setValue(obj, attrName, targetObj, type);
            }
         }
      }
      catch (Exception e)
      {
         // maybe a node
         Object targetObj = this.objIdMap.get(attrValue);
         if (targetObj != null)
         {
            reflector.setValue(obj, attrName, targetObj, type);
         }
      }
   }

   private Object parseObjectIds()
   {
      Object root = null;
      while (this.yamler.getCurrentToken() != null)
      {
         if (!"-".equals(this.yamler.getCurrentToken()))
         {
            this.yamler.printError("'-' expected");
            this.yamler.nextToken();
            continue;
         }

         String key = this.yamler.nextToken();

         if (key.endsWith(":"))
         {
            // usual
            Object now = this.parseUsualObjectId();
            if (root == null)
            {
               root = now;
            }
         }
         else
         {
            Object now = this.parseObjectTableIds();
            if (root == null)
            {
               root = now;
            }
         }
      }

      return root;
   }

   private Object parseUsualObjectId()
   {
      String objectId = this.yamler.stripColon(this.yamler.getCurrentToken());
      int pos = objectId.lastIndexOf('.');
      String numPart = objectId.substring(pos + 2);
      int objectNum;

      try
      {
         objectNum = Integer.parseInt(numPart);
      }
      catch (NumberFormatException e)
      {
         objectNum = this.objIdMap.size() + 1;
      }

      if (objectNum > this.maxUsedIdNum)
      {
         this.maxUsedIdNum = objectNum;
      }

      String className = this.yamler.nextToken();

      Object obj = this.objIdMap.get(objectId);

      String userId = null;

      // skip attributes
      while (this.yamler.getCurrentToken() != null && !"-".equals(this.yamler.getCurrentToken()))
      {
         String token = this.yamler.nextToken();
         if (token != null && token.endsWith(".time:"))
         {
            token = this.yamler.nextToken();

            userId = token.substring(token.lastIndexOf('.') + 1);
         }
      }

      boolean foreignChange = false;

      if (userId != null)
      {
         int dotIndex = objectId.indexOf('.');

         if (dotIndex > 0)
         {
            String ownerId = objectId.substring(0, dotIndex);
            foreignChange = !userId.equals(ownerId);
         }
      }

      if (obj == null && !className.endsWith(".remove") && !foreignChange)
      {
         if (".Map".equals(className))
         {
            obj = new YamlObject(objectId);
         }
         else
         {
            Reflector reflector = this.reflectorMap.getReflector(className);
            obj = reflector.newInstance();
         }

         this.objIdMap.put(objectId, obj);
         this.idObjMap.put(obj, objectId);
      }

      return obj;
   }

   private Object parseObjectTableIds()
   {
      Object root = null;

      // skip column names
      String className = this.yamler.getCurrentToken();

      Reflector reflector = this.reflectorMap.getReflector(className);

      while (!"".equals(this.yamler.getCurrentToken()) && this.yamler.getLookAheadToken().endsWith(":"))
      {
         this.yamler.nextToken();
      }

      while (!"".equals(this.yamler.getCurrentToken()) && !"-".equals(this.yamler.getCurrentToken()))
      {
         String objectId = this.yamler.stripColon(this.yamler.getCurrentToken());
         this.yamler.nextToken();

         Object obj = reflector.newInstance();

         this.objIdMap.put(objectId, obj);
         this.idObjMap.put(obj, objectId);

         if (root == null)
         {
            root = obj;
         }

         // skip column values
         while (!"".equals(this.yamler.getCurrentToken()) && !this.yamler.getCurrentToken().endsWith(":")
               && !"-".equals(this.yamler.getCurrentToken()))
         {
            this.yamler.nextToken();
         }
      }

      return root;
   }

   // --------------- Object Access ---------------

   /**
    * Puts the {@code object} in this IdMap with the specified {@code id}.
    * The call
    *
    * <pre><code>
    *     idMap.putNameObject("foo", bar);
    * </code></pre>
    * <p>
    * is equivalent to
    *
    * <pre><code>
    *     idMap.putObject("foo", bar);
    *     idMap.discoverObjects(bar);
    * </code></pre>
    * <p>
    * and the latter should be used for clarity.
    *
    * @param id
    *    the id
    * @param object
    *    the object
    *
    * @return this instance, to allow method chaining
    *
    * @deprecated since 1.2; use {@link #putObject(String, Object)} and {@link #discoverObjects(Object)} instead
    */
   @Deprecated
   public YamlIdMap putNameObject(String id, Object object)
   {
      this.putObject(id, object);
      this.discoverObjects(object);
      return this;
   }

   // --------------- Keys ---------------

   /**
    * @deprecated since 1.2; use {@link #putObject(Object)} instead
    */
   @Deprecated
   public String getOrCreateKey(Object obj)
   {
      return this.putObject(obj);
   }

   // --------------- Object Collection ---------------

   /**
    * Discovers all objects reachable from the {@code roots} and within the packages specified in the constructor.
    *
    * @param roots
    *    the root objects
    *
    * @return a set of all discovered objects
    *
    * @see #discoverObjects(Object...)
    *
    * @deprecated since 1.2; use {@link #discoverObjects(Object...)} instead (unless the resulting set is needed)
    */
   @Deprecated
   public LinkedHashSet<Object> collectObjects(Object... roots)
   {
      final LinkedHashSet<Object> collectedObjects = new LinkedHashSet<>();
      this.reflectorMap.discoverObjects(roots, collectedObjects);
      for (final Object collectedObject : collectedObjects)
      {
         this.putObject(collectedObject);
      }
      return collectedObjects;
   }

   // --------------- Encoding ---------------

   /**
    * Encodes this IdMap to a Yaml string.
    * This method is merely a shorthand for calling {@link #discoverObjects(Object...)} and {@link #encode()}.
    * I.e.,
    *
    * <pre><code>
    *    String yaml = idMap.encode(foo, bar, baz);
    * </code></pre>
    * <p>
    * is equivalent to
    *
    * <pre><code>
    *    idMap.discoverObjects(foo, bar, baz);
    *    String yaml = idMap.encode();
    * </code></pre>
    *
    * @param roots
    *    the root objects
    *
    * @return this IdMap encoded as a Yaml string
    */
   public String encode(Object... roots)
   {
      Objects.requireNonNull(roots);
      this.collectObjects(roots);
      return this.encode();
   }

   /**
    * Encodes this IdMap to a Yaml string.
    *
    * @return this IdMap encoded as a Yaml string
    *
    * @since 1.2
    */
   public String encode()
   {
      StringBuilder buf = new StringBuilder();
      for (Entry<String, Object> entry : this.objIdMap.entrySet())
      {
         Object obj = entry.getValue();
         if (obj instanceof Enum)
         {
            continue;
         }

         String key = entry.getKey();
         String className = obj.getClass().getName();
         if (encodeWithSimpleClassNames) {
            className = obj.getClass().getSimpleName();
         }

         buf.append("- ").append(key).append(": \t").append(className).append("\n");

         // attrs
         Reflector creator = this.getReflector(obj);

         for (String prop : creator.getAllProperties())
         {
            Object value = creator.getValue(obj, prop);

            if (value == null)
            {
               continue;
            }

            if (value instanceof Collection)
            {
               if (((Collection<?>) value).isEmpty())
               {
                  continue;
               }

               buf.append("  ").append(prop).append(':');
               for (Object item : (Collection<?>) value)
               {
                  buf.append(" \t");
                  this.encodeValue(buf, item);
               }
               buf.append('\n');
            }
            else if (value instanceof Map)
            {
            }
            else
            {
               buf.append("  ").append(prop).append(": \t");
               this.encodeValue(buf, value);
               buf.append('\n');

               // add time stamp?
               if (this.userId != null)
               {
                  String timeKey = key + "." + prop;
                  String timeStamp = this.attrTimeStamps.get(timeKey);

                  if (timeStamp != null)
                  {
                     buf.append("  ").append(prop).append(".time: \t").append(timeStamp).append("\n");
                  }
               }
            }
         }
         buf.append("\n");
      }

      return buf.toString();
   }

   private void encodeValue(StringBuilder buf, Object value)
   {
      if (value instanceof Enum)
      {
         final Enum<?> enumValue = (Enum<?>) value;
         // <enumClass>.<constantName>
         buf.append(enumValue.getDeclaringClass().getName()).append('.').append(enumValue.name());
         return;
      }

      final String valueKey = this.idObjMap.get(value);
      if (valueKey != null)
      {
         buf.append(valueKey);
      }
      else if (value instanceof String)
      {
         try
         {
            YamlGenerator.encapsulate((String) value, buf);
         }
         catch (IOException ignored)
         {
         }
      }
      else
      {
         buf.append(value);
      }
   }

   /**
    * @deprecated since 1.2; unused
    */
   @Deprecated
   public void encodeAttrValue(StringBuilder buf, Object obj, String propertyName, Object value)
   {
      // already known?
      String key = this.putObject(obj);
      String className = obj.getClass().getSimpleName();
      buf.append("- ").append(key).append(": \t").append(className).append("\n");
      Class<?> valueClass = value.getClass();

      if (valueClass.getName().startsWith("java.lang.") || valueClass == String.class)
      {
         buf.append("  ")
               .append(propertyName)
               .append(": \t")
               .append(YamlGenerator.encapsulate(value.toString()))
               .append("\n");
         if (this.userId != null)
         {
            String now = "" + LocalDateTime.now() + "." + this.userId;
            buf.append("  ").append(propertyName).append(".time: \t").append(now).append("\n");
            this.attrTimeStamps.put(key + "." + propertyName, now);
         }
      }
      else
      {
         // value is an object
         String valueKey = this.putObject(value);

         buf.append("  ").append(propertyName).append(": \t").append(valueKey).append("\n");
         if (this.userId != null)
         {
            // add timestamp only for to-one assocs
            Reflector reflector = this.reflectorMap.getReflector(obj);
            String fieldName = propertyName;

            if (propertyName.endsWith(".remove"))
            {
               fieldName = propertyName.substring(0, propertyName.lastIndexOf('.'));
            }

            Object fieldValue = reflector.getValue(obj, fieldName);

            String now = LocalDateTime.now() + "." + this.userId;
            if (fieldValue instanceof Collection)
            {
               buf.append("  ")
                     .append(propertyName)
                     .append('.')
                     .append(valueKey)
                     .append(".time: \t")
                     .append(now)
                     .append("\n");
               this.attrTimeStamps.put(key + "." + propertyName + "." + valueKey, now);
            }
            else
            {
               buf.append("  ").append(propertyName).append(".time: \t").append(now).append("\n");
               this.attrTimeStamps.put(key + "." + propertyName, now);
            }
         }

         if (!propertyName.endsWith(".remove"))
         {
            buf.append("- ").append(valueKey).append(": \t").append(valueClass.getSimpleName()).append("\n");
         }
      }
   }

   // --------------- Yaml Change ---------------

   public String getYamlChange()
   {
      String result = this.yamlChangeText;
      this.yamlChangeText = "";
      return result;
   }

   // --------------- Time Stamps ---------------

   /**
    * @deprecated since 1.2; unused
    */
   @Deprecated
   public String getLastTimeStamps()
   {
      LinkedHashMap<String, String> user2TimeStampMap = this.getLastTimeStampMap();

      StringBuilder buf = new StringBuilder();
      for (Entry<String, String> e : user2TimeStampMap.entrySet())
      {
         buf.append(e.getValue()).append(" ");
      }

      return buf.toString();
   }

   /**
    * @since 1.2
    */
   public String getAttributeTimeStamp(String attribute)
   {
      return this.attrTimeStamps.get(attribute);
   }

   /**
    * @deprecated since 1.2; unused
    */
   @Deprecated
   public LinkedHashMap<String, String> getLastTimeStampMap()
   {
      LinkedHashMap<String, String> user2TimeStampMap = new LinkedHashMap<>();

      for (Entry<String, String> e : this.attrTimeStamps.entrySet())
      {
         String timeStamp = e.getValue();
         int pos = timeStamp.lastIndexOf('.');
         String userName = timeStamp.substring(pos + 1);
         String oldTimeStamp = user2TimeStampMap.get(userName);

         if (oldTimeStamp == null || oldTimeStamp.compareTo(timeStamp) < 0)
         {
            user2TimeStampMap.put(userName, timeStamp);
         }
      }
      return user2TimeStampMap;
   }

   /**
    * @deprecated since 1.2; unused
    */
   @Deprecated
   public LinkedHashMap<String, String> getLastTimeStampMap(String lastTimeStamps)
   {
      LinkedHashMap<String, String> user2TimeStampMap = new LinkedHashMap<>();

      String[] split = lastTimeStamps.split("\\s+");

      for (String s : split)
      {
         int pos = s.lastIndexOf('.');
         String user = s.substring(pos + 1);
         user2TimeStampMap.put(user, s);
      }

      return user2TimeStampMap;
   }
}
