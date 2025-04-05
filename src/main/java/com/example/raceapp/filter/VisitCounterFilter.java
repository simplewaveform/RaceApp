package com.example.raceapp.filter;

import com.example.raceapp.service.VisitCounterService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

public class VisitCounterFilter extends OncePerRequestFilter {
    private final VisitCounterService visitCounterService;
    private final List<String> excludedUrls;

    public VisitCounterFilter(VisitCounterService visitCounterService, List<String> excludedUrls) {
        this.visitCounterService = visitCounterService;
        this.excludedUrls = excludedUrls;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        if (!isExcluded(requestURI)) {
            String clientId = request.getHeader("X-Client-Id");
            if (clientId == null || clientId.isBlank()) {
                clientId = "anonymous";
            }
            visitCounterService.recordVisit(clientId);
        }

        filterChain.doFilter(request, response);
    }

    private boolean isExcluded(String uri) {
        return excludedUrls.stream().anyMatch(uri::startsWith);
    }
}