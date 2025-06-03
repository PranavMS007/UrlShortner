package com.entain.urlshortener.component;

import com.entain.urlshortener.service.RateLimiterService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import java.io.IOException;

/**
 * A filter component that implements rate limiting functionality for HTTP requests.
 * This filter intercepts all incoming requests and delegates to RateLimiterService
 * to determine if the request should be allowed based on configured rate limits.
 */
@Component
public class RateLimitFilter implements Filter {
    private final RateLimiterService rateLimiterService;

    /**
     * Constructs a new RateLimitFilter with the specified RateLimiterService.
     * @param rateLimiterService the service responsible for determining if requests
     *                           should be allowed based on rate limiting rules
     */
    public RateLimitFilter(RateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }

    /**
     * Implements the filtering logic for rate limiting requests.
     * If the request is allowed by the rate limiter, it proceeds through the filter chain.
     * If the request is denied, it returns a 429 (Too Many Requests) status code.
     *
     * @param request  the ServletRequest object containing the client's request
     * @param response the ServletResponse object for sending the response
     * @param chain    the FilterChain for invoking the next filter in the chain
     * @throws IOException      if an I/O error occurs during this filter's processing
     * @throws ServletException if the processing fails for any other reason
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (!rateLimiterService.allowRequest()) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            httpResponse.getWriter().write("Too Many Requests - Rate limit exceeded");
            return;
        }
        chain.doFilter(request, response);
    }
}
