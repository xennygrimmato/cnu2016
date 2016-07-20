package com.cnu16.a9;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by vaibhavtulsyan on 20/07/16.
 * Rule: findbugs:STCAL_STATIC_CALENDAR_INSTANCE
 * Calendars are inherently unsafe for multithreaded use.
 */
public class RuleD {

    public static void f() {

        try {
            final DateFormat format = new SimpleDateFormat("yyyyMMdd");

            Callable<Date> task = new Callable<Date>() {
                public Date call() throws Exception {
                    return format.parse("20101022");
                }
            };

            //pool with 5 threads
            ExecutorService exec = Executors.newFixedThreadPool(5);
            List<Future<Date>> results = new ArrayList<Future<Date>>();

            //perform 10 date conversions
            for (int i = 0; i < 10; i++) {
                results.add(exec.submit(task));
            }
            exec.shutdown();

            for (Future<Date> result : results) {
                System.out.println(result.get());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}