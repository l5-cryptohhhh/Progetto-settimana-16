package org.example.progettosettimana16.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Pool di thread dedicato all'OCR: l'elaborazione e' pesante per la CPU,
 * quindi al massimo 2 documenti vengono elaborati in parallelo e gli altri attendono in coda.
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "ocrExecutor")
    ThreadPoolTaskExecutor ocrExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("ocr-");
        return executor;
    }
}
