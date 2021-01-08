package com.lymbyc.leni.bettercloud.controller;

import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultException;
import com.bettercloud.vault.response.LogicalResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping(value = "/v1/vault")
public class VaultController {
    private final Vault vault;

    public VaultController(Vault vault) {
        this.vault = vault;
    }

    @PostMapping(value = "create", produces = APPLICATION_JSON_VALUE)
    public LogicalResponse createSecrets(
            @RequestHeader String path,
            @RequestBody Map<String, Object> secrets) throws VaultException {
        log.info("Inside the method with request body = {}, path= {}", secrets, path);
        return vault.logical()
                .write(path, secrets);
    }

    @GetMapping(value = "load/kv", produces = APPLICATION_JSON_VALUE)
    public LogicalResponse showSecrets(
            @RequestParam(value = "secret_engine") String secretEngine) throws VaultException {
        log.info("Inside the show secret method with request param = {}", secretEngine);
        return vault.logical()
                .list(secretEngine);
    }

    @GetMapping(value = "load/all", produces = APPLICATION_JSON_VALUE)
    public Map<String, String> showSecretData(
            @RequestParam(value = "secret_engine_path") String secretEnginePath) throws VaultException {
        log.info("Inside the method to load all data with request param = {}", secretEnginePath);
        return vault.logical()
                .read(secretEnginePath)
                .getData();
    }

    @GetMapping(value = "load/value", produces = APPLICATION_JSON_VALUE)
    public Map<String, String> showSecretDataValue(
            @RequestParam(value = "secret_engine_path") String secretEnginePath,
            @RequestParam(value = "key") String key) throws VaultException {
        log.info("Inside the method to load all data with request param = {}", secretEnginePath);
        Map<String, String> secret = new HashMap<>();
        String value = vault.logical()
                .read(secretEnginePath)
                .getData()
                .get(key);
        secret.put("value", value);
        return secret;
    }
}
