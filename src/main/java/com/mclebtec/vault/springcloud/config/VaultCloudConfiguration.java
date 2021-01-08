package com.mclebtec.vault.springcloud.config;

import com.mclebtec.vault.config.VaultProperties;
import com.mclebtec.vault.springcloud.config.dto.Application;
import com.mclebtec.vault.springcloud.config.dto.Common;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.vault.core.VaultKeyValueOperations;
import org.springframework.vault.core.VaultSysOperations;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.core.VaultTransitOperations;
import org.springframework.vault.support.VaultMount;

import java.util.Map;

import static org.springframework.vault.core.VaultKeyValueOperationsSupport.KeyValueBackend.KV_2;

@Slf4j
@Configuration
public class VaultCloudConfiguration {

    @Autowired
    private Common commonSecret;
    @Autowired
    private Application applicationSecret;
    @Autowired
    private VaultProperties properties;
    @Autowired
    private VaultTemplate vaultTemplate;


    @Bean(name = "vaultKeyValueOperations")
    public VaultKeyValueOperations vaultKeyValueOperations() {
        printKeys();
        return vaultTemplate.opsForKeyValue(properties.getPath(), KV_2);
    }

    @Bean(name = "transitOperations")
    public VaultTransitOperations transitOperations() {
        return vaultTemplate.opsForTransit();
    }

    @Bean(name = "sysOperations")
    public VaultSysOperations sysOperations() {
        VaultSysOperations sysOperations = vaultTemplate.opsForSys();
        Map<String, String> transit = properties.getTransit();
        if (!sysOperations.getMounts().containsKey(transit.get("path") + "/")) {
            sysOperations.mount(transit.get("path"), VaultMount.create(transit.get("path")));
            transitOperations().createKey(transit.get("key"));
        }
        return sysOperations;
    }

    private void printKeys() {
        log.info("Test key 1 = {}", commonSecret.getMyKey());
        log.info("Test key 2 = {}", commonSecret.getUsername());
        log.info("Test key 3 = {}", commonSecret.getPassword());
        log.info("Test key 4 = {}", applicationSecret.getKey());
        log.info("Test key 5 = {}", applicationSecret.getSecret());
    }
}
