package com.mclebtec.vault.bettercloud.config;

import com.bettercloud.vault.SslConfig;
import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;
import com.mclebtec.vault.config.VaultProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class VaultConfiguration {

    @Autowired
    private VaultProperties properties;

    @Bean(name = "vault")
    public Vault vault() throws VaultException {
        return new Vault(vaultConfig()).withRetries(5, 1000);
    }

    @Bean(name = "vaultConfig")
    public VaultConfig vaultConfig() throws VaultException {
        return new VaultConfig()
                .address(properties.getServer())
                .token(properties.getToken())
                .openTimeout(5)
                .readTimeout(30)
                .sslConfig(new SslConfig().build())
                .build();
    }
}
