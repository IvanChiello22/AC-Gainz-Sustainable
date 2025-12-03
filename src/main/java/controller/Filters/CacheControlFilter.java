package controller.Filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CacheControlFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Imposta la cache a 10 giorni (864000 secondi)
        httpResponse.setHeader("Cache-Control", "public, max-age=864000");

        // Continua con la richiesta normale
        chain.doFilter(request, response);
    }

    // Metodi di init e destroy vuoti (obbligatori per alcune versioni vecchie, utili per sicurezza)
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}
    @Override
    public void destroy() {}
}