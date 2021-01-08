package com.mclebtec.vault.config;

import com.mclebtec.vault.bettercloud.config.VaultConfiguration;
import com.mclebtec.vault.springcloud.config.VaultCloudConfiguration;
import lombok.Data;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "vault")
@AutoConfigureBefore({VaultConfiguration.class, VaultCloudConfiguration.class})
public class VaultProperties extends org.springframework.cloud.vault.config.VaultProperties {
    private String server;
    private String token;
    private String path;
    private String appName;
    private Map<String, String> transit;
}
