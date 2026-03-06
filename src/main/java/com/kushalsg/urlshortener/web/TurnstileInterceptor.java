package com.kushalsg.urlshortener.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TurnstileInterceptor implements HandlerInterceptor {

    private static final String SESSION_KEY = "turnstile_verified";

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String path = request.getRequestURI();


        if (path.startsWith("/verify")
                || path.startsWith("/css")
                || path.startsWith("/js")
                || path.startsWith("/images")
                || path.startsWith("/webjars")
                || path.startsWith("/error")
                || path.startsWith("/favicon")) {
            return true;
        }

        HttpSession session = request.getSession(false);
        boolean verified = session != null
                && Boolean.TRUE.equals(session.getAttribute(SESSION_KEY));

        if (!verified) {
            response.sendRedirect("/verify");
            return false;
        }

        return true;
    }
}