package com.kushalsg.urlshortener.web.controllers;

import com.kushalsg.urlshortener.ApplicationProperties;
import com.kushalsg.urlshortener.domain.exceptions.ShortUrlNotFoundException;
import com.kushalsg.urlshortener.domain.models.CreateShortUrlCmd;
import com.kushalsg.urlshortener.domain.models.PagedResult;
import com.kushalsg.urlshortener.domain.models.ShortUrlDto;
import com.kushalsg.urlshortener.domain.services.ShortUrlService;
import com.kushalsg.urlshortener.web.dtos.CreateShortUrlForm;
import com.kushalsg.urlshortener.domain.services.QRCodeService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.HashMap;
import java.util.Map;

import java.util.List;
import java.util.Optional;

@Controller
public class ShortUrlController {
    private final ShortUrlService shortUrlService;
    private final ApplicationProperties properties;
    private final QRCodeService qrCodeService;
    private final SecurityUtils securityUtils;

    public ShortUrlController(ShortUrlService shortUrlService,
                              ApplicationProperties properties,
                              SecurityUtils securityUtils,
                              QRCodeService qrCodeService) {
        this.shortUrlService = shortUrlService;
        this.properties = properties;
        this.securityUtils = securityUtils;
        this.qrCodeService = qrCodeService;
    }

    @GetMapping("/")
    public String home(
            @RequestParam(defaultValue = "1") Integer page,
            Model model) {
        this.addShortUrlsDataToModel(model, page);
        model.addAttribute("paginationUrl", "/");
        model.addAttribute("createShortUrlForm",
                new CreateShortUrlForm("", false, null));
        return "index";
    }

    private void addShortUrlsDataToModel(Model model, int pageNo) {
        PagedResult<ShortUrlDto> shortUrls = shortUrlService.findAllPublicShortUrls(pageNo, properties.pageSize());

        Map<Long, String> qrCodeMap = new HashMap<>();

        shortUrls.data().forEach(url -> {
            String fullUrl = properties.baseUrl() + "/s/" + url.shortKey();
            String qrCode = qrCodeService.generateQRCodeBase64(fullUrl, 200, 200);
            qrCodeMap.put(url.id(), qrCode);
        });

        model.addAttribute("qrCodeMap", qrCodeMap);


        model.addAttribute("shortUrls", shortUrls);
        model.addAttribute("baseUrl", properties.baseUrl());
        model.addAttribute("paginationUrl", "/");
    }

    @PostMapping("/short-urls")
    String createShortUrl(@ModelAttribute("createShortUrlForm") @Valid CreateShortUrlForm form,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes,
                          Model model) {
        if(bindingResult.hasErrors()) {
            this.addShortUrlsDataToModel(model, 1);
            return "index";
        }

        try {
            Long userId = securityUtils.getCurrentUserId();
            CreateShortUrlCmd cmd = new CreateShortUrlCmd(
                    form.originalUrl(),
                    form.isPrivate(),
                    form.expirationInDays(),
                    userId
            );
            var shortUrlDto = shortUrlService.createShortUrl(cmd);
            redirectAttributes.addFlashAttribute("successMessage", "Short URL created successfully "+
                    properties.baseUrl()+"/s/"+shortUrlDto.shortKey());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create short URL");

        }
        return "redirect:/";
    }

    @GetMapping("/s/{shortKey}")
    String redirectToOriginalUrl(@PathVariable String shortKey) {
        Long userId = securityUtils.getCurrentUserId();
        Optional<ShortUrlDto> shortUrlDtoOptional = shortUrlService.accessShortUrl(shortKey, userId);
        if(shortUrlDtoOptional.isEmpty()) {
            throw new ShortUrlNotFoundException("Invalid short key: "+shortKey);
        }
        ShortUrlDto shortUrlDto = shortUrlDtoOptional.get();
        return "redirect:"+shortUrlDto.originalUrl();
    }

    @GetMapping("/my-urls")
    public String showUserUrls(
            @RequestParam(defaultValue = "1") int page,
            Model model) {
        var currentUserId = securityUtils.getCurrentUserId();
        PagedResult<ShortUrlDto> myUrls =
                shortUrlService.getUserShortUrls(currentUserId, page, properties.pageSize());
        model.addAttribute("shortUrls", myUrls);
        model.addAttribute("baseUrl", properties.baseUrl());
        model.addAttribute("paginationUrl", "/my-urls");
        return "my-urls";
    }

    @PostMapping("/delete-urls")
    public String deleteUrls(
            @RequestParam(value = "ids", required = false) List<Long> ids,
            RedirectAttributes redirectAttributes) {
        if (ids == null || ids.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "No URLs selected for deletion");
            return "redirect:/my-urls";
        }
        try {
            var currentUserId = securityUtils.getCurrentUserId();
            shortUrlService.deleteUserShortUrls(ids, currentUserId);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Selected URLs have been deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error deleting URLs: " + e.getMessage());
        }
        return "redirect:/my-urls";
    }
}
