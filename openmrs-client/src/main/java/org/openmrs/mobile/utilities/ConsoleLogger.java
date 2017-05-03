package org.openmrs.mobile.utilities;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class ConsoleLogger {

    public ConsoleLogger() {

    }

    public static void dump(String object) {
        System.out.println("=========================== START OUTPUT OF STRING ===========================");
        System.out.println(object);
        System.out.println("=========================== END OUTPUT OF STRING ===========================");
    }

    public static void dump(boolean object) {
        System.out.println("=========================== START OUTPUT OF BOOLEAN ===========================");
        System.out.println(object);
        System.out.println("=========================== END OUTPUT OF BOOLEAN ===========================");
    }

    public static void dump(Boolean object) {
        System.out.println("=========================== START OUTPUT OF BOOLEAN ===========================");
        System.out.println(object);
        System.out.println("=========================== END OUTPUT OF BOOLEAN ===========================");
    }

    public static void dump(int object) {
        System.out.println("=========================== START OUTPUT OF INT ===========================");
        System.out.println(object);
        System.out.println("=========================== END OUTPUT OF INT ===========================");
    }

    public static void dump(Map object) {
        System.out.println("=========================== START OUTPUT OF MAP ===========================");
        Set keys = object.keySet();
        for (Iterator i = keys.iterator(); i.hasNext(); ) {
            String key = (String) i.next();
            String value = (String) object.get(key);
            System.out.printf("%s : %s%n", key, value);
        }
        System.out.println("=========================== END OUTPUT OF MAP ===========================");
    }

    public static void dump(Object object) {
        System.out.println("=========================== START LISTING OF OBJECT PROPERTIES ===========================");
        for (Field field : object.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            String name = field.getName();
            Object value = null;
            try {
                value = field.get(object);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            //System.out.printf("Field name: %s, Field value: %s%n", name, value);
            System.out.printf("%s : %s%n", name, value);
        }
        System.out.println("=========================== END LISTING OF OBJECT PROPERTIES ===========================");
    }

    public static void dump(Object obj, Object obj1) {
        System.out.println("=========================== START DUMPING OF 2 OBJECTS ===========================");
        System.out.println("=========================== OBJECT 1 ===========================");
        dump(obj);
        System.out.println("=========================== OBJECT 2 ===========================");
        dump(obj1);
        System.out.println("=========================== END DUMPING OF 2 OBJECTS ===========================");
    }

    public static void dump(Object obj, Object obj1, Object obj2) {
        System.out.println("=========================== START DUMPING OF 3 OBJECTS ===========================");
        System.out.println("=========================== OBJECT 1 ===========================");
        dump(obj);
        System.out.println("=========================== OBJECT 2 ===========================");
        dump(obj1);
        System.out.println("=========================== OBJECT 3 ===========================");
        dump(obj2);
        System.out.println("=========================== END DUMPING OF 3 OBJECTS ===========================");
    }

}