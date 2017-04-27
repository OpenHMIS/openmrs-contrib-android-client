package org.openmrs.mobile.utilities;

import java.lang.reflect.Field;


public class ConsoleLogger {

    public ConsoleLogger() {

    }

    public static void dump(Object obj, Object obj1) {
        System.out.println("=========================== STARTING DUMPING OF 2 OBJECTS ===========================");
        System.out.println("=========================== OBJECT 1 ===========================");
        dump(obj);
        System.out.println("=========================== OBJECT 2 ===========================");
        dump(obj1);
        System.out.println("=========================== FINISHED DUMPING OF 2 OBJECTS ===========================");
    }

    public static void dump(Object obj, Object obj1, Object obj2) {
        System.out.println("=========================== STARTING DUMPING OF 2 OBJECTS ===========================");
        System.out.println("=========================== OBJECT 1 ===========================");
        dump(obj);
        System.out.println("=========================== OBJECT 2 ===========================");
        dump(obj1);
        System.out.println("=========================== OBJECT 3 ===========================");
        dump(obj2);
        System.out.println("=========================== FINISHED DUMPING OF 2 OBJECTS ===========================");
    }

    public static void dump(Object object) {
        System.out.println("=========================== STARTING LISTING OF OBJECT PROPERTIES ===========================");
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
        System.out.println("=========================== STARTING LISTING OF OBJECT PROPERTIES ===========================");
    }
}