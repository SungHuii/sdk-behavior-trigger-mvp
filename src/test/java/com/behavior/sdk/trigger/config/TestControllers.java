package com.behavior.sdk.trigger.config;

import com.behavior.sdk.trigger.integration.epic4.ErrorSliceTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestControllers {

    @Bean
    ErrorSliceTest.SampleController sampleController() {
        return new ErrorSliceTest.SampleController();
    }
}
