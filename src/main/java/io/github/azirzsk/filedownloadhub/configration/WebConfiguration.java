package io.github.azirzsk.filedownloadhub.configration;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.UUID;

/**
 * @author azir
 * @since 2025/04/11
 */
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    @Bean
    public FilterRegistrationBean<TraceFilter> traceFilter() {
        FilterRegistrationBean<TraceFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new TraceFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1);
        registrationBean.setName("traceFilter");
        return registrationBean;
    }

    /**
     * @author azir
     * @since 2025/04/11
     */
    @Slf4j
    public static class TraceFilter implements Filter {

        private static final String TRACE_ID = "traceId";

        private static void printResponseHeader(HttpServletResponse servletResponse) {
            Collection<String> headerNames = servletResponse.getHeaderNames();
            headerNames.forEach(headerName -> {
                if (log.isDebugEnabled()) {
                    log.debug("Response-Header: {}:{}", headerName, servletResponse.getHeader(headerName));
                }
            });
        }

        private static void printRequestHeader(HttpServletRequest servletRequest) {
            if (log.isDebugEnabled()) {
                Enumeration<String> headerNames = servletRequest.getHeaderNames();
                while (headerNames.hasMoreElements()) {
                    String headerName = headerNames.nextElement();
                    log.debug("Request-Header: {}:{}", headerName, servletRequest.getHeader(headerName));
                }
            }
        }

        @Override
        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {
            String traceId = UUID.randomUUID().toString();
            MDC.put(TRACE_ID, traceId);
            ((HttpServletResponse) servletResponse).setHeader(TRACE_ID, traceId);
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            if (log.isDebugEnabled()) {
                log.info("请求路径:{}", request.getRequestURI());
            }
            printRequestHeader(request);
            try {
                filterChain.doFilter(servletRequest, servletResponse);
            } finally {
                printResponseHeader((HttpServletResponse) servletResponse);
                MDC.remove(TRACE_ID);
            }
        }

    }
}
