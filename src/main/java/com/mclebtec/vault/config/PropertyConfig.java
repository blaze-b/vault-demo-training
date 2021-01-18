package com.mclebtec.vault.config;

import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultException;
import com.mclebtec.vault.bettercloud.config.VaultConfiguration;
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
import java.util.Set;

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
    public Properties loadViaBetterCloud() throws VaultException {
        if (properties != null) {
            Map<String, String> details = vault.logical()
                    .read("secret/" + vProperties.getAppName())
                    .getData();
            log.info("Configuration better cloud details = {}", details);
            properties.putAll(details);
        }
        return properties;
    }

    @PostConstruct
    public Properties loadViaSpringCloud() {
        if (properties != null) {
            VaultResponse response = kv.get(vProperties.getAppName());
            Map<String, Object> details = Objects.requireNonNull(response).getData();
            if (details != null) {
                Set<String> keys = details.keySet();
                keys.forEach(k -> {
                    try {
                        String value = (String) details.get(k);
                        Map<String, String> transit = vProperties.getTransit();
                        String decryptedValue = transitOperations.decrypt(transit.get("key"), value);
                        details.put(k, decryptedValue);
                    } catch (Exception e) {
                        log.warn("Not a cypher text with error = {}", e.getMessage());
                    }
                });
            }
            log.info("Configuration spring cloud details = {}", details);
            properties.putAll(details);
        }
        return properties;
    }
}
;