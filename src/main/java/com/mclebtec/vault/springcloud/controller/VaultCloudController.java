package com.mclebtec.vault.springcloud.controller;

import com.bettercloud.vault.VaultException;
import com.mclebtec.vault.config.VaultProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.vault.core.VaultKeyValueOperations;
import org.springframework.vault.core.VaultTransitOperations;
import org.springframework.vault.support.VaultResponse;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping(value = "/v2/vault")
public class VaultCloudController {
    private final VaultKeyValueOperations kv;
    private final VaultTransitOperations transitOperations;
    private final VaultProperties vProperties;
    private final Properties properties;


    public VaultCloudController(VaultKeyValueOperations kv,
                                VaultTransitOperations transitOperations,
                                VaultProperties vProperties,
                                Properties properties) {
        this.kv = kv;
        this.transitOperations = transitOperations;
        this.vProperties = vProperties;
        this.properties = properties;
    }

    @GetMapping(value = "load/all")
    public Map<String, Object> showSecretData(@RequestParam boolean decryptEnabled) {
        log.info("Properties ={}", properties.get("application.key"));
        VaultResponse response = kv.get(vProperties.getAppName());
        Map<String, Object> details = Objects.requireNonNull(response).getData();
        log.info("Configuration details = {}", details);
        if(decryptEnabled) decryptCipherTextValues(details);
        return details;
    }

    @PostMapping(value = "create", produces = APPLICATION_JSON_VALUE)
    public void createSecrets(@RequestBody Map<String, Object> secrets)
            throws VaultException {
        if (secrets.containsKey("common.password")) {
            Map<String, String> transit = vProperties.getTransit();
            String ciphertext = transitOperations.encrypt(transit.get("key"),
                    (String) secrets.get("common.password"));
            secrets.put("common.password", ciphertext);
            kv.put(vProperties.getAppName(), secrets);
        }
    }

    private void decryptCipherTextValues(Map<String, Object> details) {
        if (details != null) {
            Set<String> keys = details.keySet();
            keys.forEach(k -> {
                try {
                    String value = (String) details.get(k);
                    String decryptedValue = transitOperations.decrypt("vault-app-key", value);
                    details.put(k, decryptedValue);
                } catch (Exception e) {
                    log.warn("Not a cypher text with error = {}", e.getMessage());
                }
            });
        }
    }
}