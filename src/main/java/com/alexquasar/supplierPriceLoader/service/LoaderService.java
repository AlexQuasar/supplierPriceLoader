package com.alexquasar.supplierPriceLoader.service;

import com.alexquasar.supplierPriceLoader.dto.supplier.SupplierConfig;
import com.alexquasar.supplierPriceLoader.dto.supplier.SupplierIdentifier;
import com.alexquasar.supplierPriceLoader.entity.PriceItem;
import com.alexquasar.supplierPriceLoader.exception.ServiceException;
import com.alexquasar.supplierPriceLoader.repository.PriceItemsRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;
import java.util.ArrayList;
import java.util.List;

@Service
public class LoaderService {

    private PriceItemsRepository priceItemsRepository;

    private StoreIMAPConnectorService connector;
    private SupplierIdentifier supplierIdentifier = new SupplierIdentifier();
    private final String multipart = "multipart";

    @Value("${alex.tempDirectory}")
    private String saveDirectory;

    public LoaderService(PriceItemsRepository priceItemsRepository, StoreIMAPConnectorService connector) {
        this.priceItemsRepository = priceItemsRepository;
        this.connector = connector;
    }

    @SneakyThrows
    public void loadPriceItems() {
        Store store = connector.storeConnect();
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
        store.close();
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

        SupplierConfig supplier = supplierIdentifier.getSupplierConfig(supplierName);
        if (supplier != null) {
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
