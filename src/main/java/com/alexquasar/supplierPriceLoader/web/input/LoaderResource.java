package com.alexquasar.supplierPriceLoader.web.input;

import com.alexquasar.supplierPriceLoader.service.LoaderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/loadEmails")
public class LoaderResource {

    private LoaderService loaderService;

    public LoaderResource(LoaderService loaderService) {
        this.loaderService = loaderService;
    }

    @GetMapping
    public void loadEmails() {
        loaderService.loadPriceItems();
    }
}
