package com.lymbyc.leni.config;

import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultException;
import com.lymbyc.leni.bettercloud.config.VaultConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;
import org.springframework.vault.core.VaultKeyValueOperations;
import org.springframework.vault.core.VaultTransitOperations;
import org.springframework.vault.support.VaultResponse;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

@Slf4j
@Configuration
@AutoConfigureAfter({VaultConfiguration.class, VaultConfiguration.class})
public class PropertyConfig {
    private final Properties properties;
    private final VaultProperties vProperties;
    private final VaultKeyValueOperations kv;
    private final VaultTransitOperations transitOperations;
    private final Vault vault;

    public PropertyConfig(Properties properties,
                          VaultProperties vProperties,
                          VaultKeyValueOperations kv,
                          VaultTransitOperations transitOperations,
                          Vault vault) {
        this.properties = properties;
        this.vProperties = vProperties;
        this.kv = kv;
        this.transitOperations = transitOperations;
        this.vault = vault;
    }

    @PostConstruct
//    @Scheduled(cron = "0/11 * * ? * *")
    public Properties loadForMyApp() {
        if (properties != null) {
            VaultResponse response = kv.get(vProperties.getAppName());
            Map<String, Object> details = Objects.requireNonNull(response).getData();
            log.info("Configuration details = {}", details);
            if (details != null && details.containsKey("common.password")) {
                String cypherText = (String) details.get("common.password");
                Map<String, String> transit = vProperties.getTransit();
                String plaintext = transitOperations.decrypt(transit.get("key"), cypherText);
                details.put("common.password", plaintext);
            }
            properties.putAll(details);
        }
        return properties;
    }

    @PostConstruct
    public Properties loadForLeni() throws VaultException {
        if (properties != null) {
            VaultResponse response = kv.get(vProperties.getAppName());
            Map<String, String> details = vault.logical()
                    .read("secret/leni")
                    .getData();
            log.info("Configuration details = {}", details);
            properties.putAll(details);
        }
        return properties;
    }
}
