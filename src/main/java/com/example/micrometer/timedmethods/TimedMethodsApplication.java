package com.example.micrometer.timedmethods;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class TimedMethodsApplication {

  @Autowired
  private Worker worker;

  public static void main(String[] args) {
    SpringApplication.run(TimedMethodsApplication.class, args);
  }

  @Scheduled(fixedRate = 1000)
  public void scheduleJob() {
    worker.doJob();
  }

  @Configuration
  @EnableScheduling
  @EnableAspectJAutoProxy
  public class AutoTimingConfiguration {

    @Bean
    MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
      return registry -> registry.config().commonTags("application", "timed-methods-proof");
    }

    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
      return new TimedAspect(registry);
    }
  }

  @Component
  public class Worker {

    @Timed("my_metric")
    public void doJob() {
      System.out.println("work");
    }
  }
}
