package com.mclebtec.vault;

import com.mclebtec.vault.springcloud.config.dto.Application;
import com.mclebtec.vault.springcloud.config.dto.Common;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.vault.config.VaultProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@SpringBootApplication
@EnableConfigurationProperties({VaultProperties.class,
        Application.class,
        Common.class})
@EnableScheduling
public class VaultDemoTrainingApplication {

    public static void main(String[] args) {
        SpringApplication.run(VaultDemoTrainingApplication.class, args);
    }
}