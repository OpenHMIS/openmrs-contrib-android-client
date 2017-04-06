package org.openmrs.mobile;

import android.util.Log;

/**
 * Created by bandahealth on 4/6/17.
 */

public class ConsoleLogger {
    public static void var_dump(Object obj) {
        System.out.println("==============================");
        System.out.println(obj);
        System.out.println("==============================");
    }

    public static void var_dump(Object obj, Object obj1) {
        System.out.println("==============================");
        System.out.println(obj);
        System.out.println(obj1);
        System.out.println("==============================");
    }

    public static void var_dump(Object obj, Object obj1, Object obj2) {
        System.out.println("==============================");
        System.out.println(obj);
        System.out.println(obj1);
        System.out.println(obj2);
        System.out.println("==============================");
    }

}
