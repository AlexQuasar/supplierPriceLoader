package com.alexquasar.supplierPriceLoader.service;

import com.alexquasar.supplierPriceLoader.dto.StoreConfig;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.Session;
import javax.mail.Store;
import java.util.Properties;

@Service
public class StoreIMAPConnectorService implements StoreConfig {

    @Value("${alex.mailProperty.host}")
    private String host;

    @Value("${alex.mailProperty.user}")
    private String user;

    @Value("${alex.mailProperty.password}")
    private String password;

    @Value("${alex.mailProperty.port:-1}")
    private int port;

    @Value("${alex.mailProperty.isSSL}")
    private boolean isSSL;

    @Override
    @SneakyThrows
    public Store storeConnect() {
        Properties properties = new Properties();
        if (isSSL) {
            properties.setProperty("mail.imap.ssl.enable", "true");
        }

        Session session = Session.getDefaultInstance(properties);
        Store store = session.getStore("imap");
        if (port < 0) {
            store.connect(host, user, password);
        } else {
            store.connect(host, port, user, password);
        }

        return store;
    }
}
