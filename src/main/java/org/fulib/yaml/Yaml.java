package org.fulib.yaml;

import java.util.Arrays;
import java.util.Map;

/**
 * Simple yaml encoding
 *
 * @since 1.5
 */
public class Yaml
{
   private Yaml()
   {
      // no instances
   }

   public static String encode(Object... objects)
   {
      final String[] packageNames = Arrays
            .stream(objects)
            .map(Object::getClass)
            .map(Class::getPackage)
            .map(Package::getName)
            .toArray(String[]::new);
      return new YamlIdMap(packageNames).encode(objects);
   }

   public static String encodeSimple(Object... objects)
   {
      final String[] packageNames = Arrays
            .stream(objects)
            .map(Object::getClass)
            .map(Class::getPackage)
            .map(Package::getName)
            .toArray(String[]::new);
      return new YamlIdMap(packageNames).encodeWithSimpleClassNames().encode(objects);
   }

   public static Map<String, Object> decode(String yaml)
   {
      final YamlIdMap idMap = new YamlIdMap();
      idMap.decode(yaml);
      return idMap.getObjIdMap();
   }
}
