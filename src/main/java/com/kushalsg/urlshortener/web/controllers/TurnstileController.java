package com.kushalsg.urlshortener.web.controllers;

import com.kushalsg.urlshortener.ApplicationProperties;
import com.kushalsg.urlshortener.domain.services.TurnstileService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TurnstileController {

    private static final String SESSION_KEY = "turnstile_verified";

    private final TurnstileService turnstileService;
    private final String siteKey;

    public TurnstileController(TurnstileService turnstileService,
                               ApplicationProperties properties) {
        this.turnstileService = turnstileService;
        this.siteKey = properties.turnstileSiteKey();
    }

    @GetMapping("/verify")
    public String verifyPage(HttpSession session, Model model) {
        if (Boolean.TRUE.equals(session.getAttribute(SESSION_KEY))) {
            return "redirect:/";
        }
        model.addAttribute("siteKey", siteKey);
        return "verify";
    }

    @PostMapping("/verify")
    public String verifySubmit(
            @RequestParam(value = "cf-turnstile-response", required = false) String token,
            HttpSession session) {

        if (turnstileService.verify(token)) {
            session.setAttribute(SESSION_KEY, true);
            return "redirect:/";
        }

        return "redirect:/verify?error";
    }
}