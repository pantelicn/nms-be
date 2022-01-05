package com.opdev;

import java.util.Optional;

import com.opdev.test.InitDataService;
import com.opdev.test.TestDataService;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.RequiredArgsConstructor;

@SpringBootApplication
@RequiredArgsConstructor
public class NMSApp implements CommandLineRunner {

        private final InitDataService initDataService;
        private final Optional<TestDataService> testDataService;

        public static void main(String[] args) {
                SpringApplication.run(NMSApp.class, args);
        }

        @Override
        public void run(final String... args) throws Exception {
                initDataService.insert();
                testDataService.ifPresent(TestDataService::insert);
        }

}
