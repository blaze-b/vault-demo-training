package com.mclebtec.vault.springcloud.config.dto;

import com.mclebtec.vault.springcloud.config.VaultCloudConfiguration;
import lombok.Data;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "common")
@AutoConfigureBefore(VaultCloudConfiguration.class)
public class Common {
    private String myKey;
    private String username;
    private String password;
}
