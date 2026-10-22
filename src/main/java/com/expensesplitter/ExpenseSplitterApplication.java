package com.expensesplitter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point of the application.
 * @SpringBootApplication = @Configuration + @EnableAutoConfiguration + @ComponentScan
 */
@SpringBootApplication
public class ExpenseSplitterApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExpenseSplitterApplication.class, args);
    }
}
