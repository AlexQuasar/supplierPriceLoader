package com.alexquasar.supplierPriceLoader.service;

import com.alexquasar.supplierPriceLoader.dto.SupplierConfig;
import com.alexquasar.supplierPriceLoader.dto.Supplier_DeliverOnTime;
import com.alexquasar.supplierPriceLoader.entity.PriceItem;
import com.alexquasar.supplierPriceLoader.exception.ServiceException;
import com.alexquasar.supplierPriceLoader.repository.PriceItemsRepository;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service
public class LoaderService {

    private PriceItemsRepository priceItemsRepository;

    private final String multipart = "multipart";
    private final String saveDirectory = "F:\\Java\\IntelliJ IDEA Workspace Directory\\loadFile.csv";

    public LoaderService(PriceItemsRepository priceItemsRepository) {
        this.priceItemsRepository = priceItemsRepository;
    }

    @SneakyThrows
    public void loadPriceItems() {
        Properties properties = new Properties();
        properties.setProperty("mail.imap.ssl.enable", "true");

        Session session = Session.getDefaultInstance(properties);
        Store store = null;
        try {
            store = session.getStore("imap");
            store.connect("imap.yandex.ru", "email@yandex.ru", "password"); // необходимо настроить под свою почту
            Folder folder = null;
            try {
                folder = store.getFolder("INBOX");
                folder.open(Folder.READ_WRITE);

                readAllMessages(folder);
            } finally {
                if (folder != null) {
                    folder.close(false);
                }
            }
        } finally {
            if (store != null) {
                store.close();
            }
        }
    }

    @SneakyThrows
    private void readAllMessages(Folder folder) {
        int countMessages = folder.getMessageCount();
        Message[] messages = folder.getMessages(countMessages, countMessages);
        for (Message message : messages) {
            if (!message.isSet(Flags.Flag.SEEN)) {
                String contentType = message.getContentType();
                if (contentType.contains(multipart)) {
                    getAttachments(message);
                }
            }
        }
    }

    @SneakyThrows
    private void getAttachments(Message message) {
        String supplierName = message.getFrom()[0].toString();

        Multipart multiPart = (Multipart) message.getContent();
        int numberOfParts = multiPart.getCount();
        boolean notAllLoaded = false;
        for (int partCount = 0; partCount < numberOfParts; partCount++) {
            MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
            if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                part.saveFile(saveDirectory);
                if(!loadSupplierData(supplierName)) {
                    notAllLoaded = true;
                }
            }
        }

        if (notAllLoaded) {
            message.setFlag(Flags.Flag.SEEN, true);
        } else {
            message.setFlag(Flags.Flag.DELETED, true);
        }
    }

    private Boolean loadSupplierData(String supplierName) {
        List<PriceItem> priceItems = new ArrayList<>();

        if (Supplier_DeliverOnTime.NAME.equals(supplierName)) {
            SupplierConfig supplier = new Supplier_DeliverOnTime();
            priceItems = executeLoad(supplier);
        }

        if (!priceItems.isEmpty()) {
            priceItemsRepository.saveAll(priceItems);
            return true;
        }

        return false;
    }

    private List<PriceItem> executeLoad(SupplierConfig config) {
        try {
            return config.loadPriceItems(saveDirectory);
        } catch (Exception ex) {
            throw new ServiceException("file don't read", HttpStatus.FORBIDDEN);
        }
    }
}
