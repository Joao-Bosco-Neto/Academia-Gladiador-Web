package br.com.gladiador.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro CORS explícito com prioridade máxima para garantir execução antes de qualquer outro filtro
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(CorsFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String origin = request.getHeader("Origin");
        String method = request.getMethod();
        String uri = request.getRequestURI();

        logger.info("CorsFilter - Método: {}, URI: {}, Origin: {}", method, uri, origin);

        // Configurar headers CORS para todas as requisições
        response.setHeader("Access-Control-Allow-Origin", origin != null ? origin : "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, PATCH, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, X-Requested-With");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Max-Age", "3600");

        // Para requisições OPTIONS (preflight), retornar 200 OK imediatamente
        if ("OPTIONS".equalsIgnoreCase(method)) {
            logger.info("Requisição OPTIONS (preflight) - retornando 200 OK com headers CORS");
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        // Continuar com a cadeia de filtros para outras requisições
        filterChain.doFilter(request, response);
    }
}
