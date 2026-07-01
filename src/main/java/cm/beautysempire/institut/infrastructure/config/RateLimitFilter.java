package cm.beautysempire.institut.infrastructure.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RateLimitFilter extends OncePerRequestFilter {

    // Stocke les "seaux" (buckets) par adresse IP
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    // Les routes qu'on veut protéger contre le spam
    private static final List<String> ENDPOINTS_PROTEGES = List.of(
            "/api/messages/contact",
            "/api/messages/pre-inscription",
            "/api/newsletter/subscribe"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        boolean estProtege = ENDPOINTS_PROTEGES.stream().anyMatch(requestURI::startsWith);

        if (estProtege && request.getMethod().equals("POST")) {
            String ip = request.getRemoteAddr();
            Bucket bucket = buckets.computeIfAbsent(ip, this::creerNouveauBucket);

            if (!bucket.tryConsume(1)) {
                // Si la limite est dépassée, on renvoie une erreur 429 (Too Many Requests)
                response.setStatus(429);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"status\":\"ERROR\",\"statusCode\":429,\"message\":\"Trop de requêtes. Veuillez patienter 10 minutes avant de réessayer.\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private Bucket creerNouveauBucket(String key) {
        // Limite : 5 requêtes toutes les 10 minutes par adresse IP
        Bandwidth limit = Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(10)));
        return Bucket.builder().addLimit(limit).build();
    }
}