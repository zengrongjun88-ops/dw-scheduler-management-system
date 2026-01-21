package com.dw.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * DW Scheduler Management System - Application Entry Point
 *
 * @author DW Team
 * @version 1.0.0
 */
@SpringBootApplication
public class SchedulerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SchedulerApplication.class, args);
        System.out.println("\n" +
                "=======================================================\n" +
                " DW Scheduler Management System Started Successfully! \n" +
                " API Documentation: http://localhost:8080/api/doc.html \n" +
                " Druid Monitor: http://localhost:8080/api/druid/login.html \n" +
                "=======================================================\n");
    }
}
