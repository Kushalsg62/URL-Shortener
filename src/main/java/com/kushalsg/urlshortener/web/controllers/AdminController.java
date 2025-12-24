package com.kushalsg.urlshortener.web.controllers;

import com.kushalsg.urlshortener.ApplicationProperties;
import com.kushalsg.urlshortener.domain.models.PagedResult;
import com.kushalsg.urlshortener.domain.models.ShortUrlDto;
import com.kushalsg.urlshortener.domain.services.ShortUrlService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import com.kushalsg.urlshortener.domain.repositories.ShortUrlRepository;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final ShortUrlService shortUrlService;
    private final ApplicationProperties properties;
    private final ShortUrlRepository shortUrlRepository;

    public AdminController(ShortUrlService shortUrlService,
                           ApplicationProperties properties,
                           ShortUrlRepository shortUrlRepository) {
        this.shortUrlService = shortUrlService;
        this.properties = properties;
        this.shortUrlRepository = shortUrlRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(
            @RequestParam(defaultValue = "1") int page,
            Model model) {
        PagedResult<ShortUrlDto> allUrls = shortUrlService.findAllShortUrls(page, properties.pageSize());
        model.addAttribute("shortUrls", allUrls);
        model.addAttribute("baseUrl", properties.baseUrl());
        model.addAttribute("paginationUrl", "/admin/dashboard");
        return "admin-dashboard";
    }
    @PostMapping("/toggle-status/{id}")
    public String toggleStatus(@PathVariable Long id) {
        shortUrlRepository.findById(id).ifPresent(url -> {
            url.setIsActive(!url.getIsActive());
            shortUrlRepository.save(url);
        });
        return "redirect:/admin/dashboard";
    }
}
