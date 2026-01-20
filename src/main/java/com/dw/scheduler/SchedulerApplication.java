package com.dw.scheduler;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * DW Scheduler Management System - Application Entry Point
 *
 * @author DW Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableScheduling
@MapperScan("com.dw.scheduler.mapper")
public class SchedulerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SchedulerApplication.class, args);
        System.out.println("\n" +
                "=======================================================\n" +
                " DW Scheduler Management System Started Successfully! \n" +
                " API Documentation: http://localhost:8080/api/doc.html \n" +
                " Druid Monitor: http://localhost:8080/api/druid/        \n" +
                "=======================================================\n");
    }
}
