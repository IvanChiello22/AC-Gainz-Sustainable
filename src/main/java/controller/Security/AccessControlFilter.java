package controller.Security;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Utente;

import java.io.IOException;

@WebFilter(filterName = "/AccessControlFilter", urlPatterns = "/*")
public class AccessControlFilter extends HttpFilter implements Filter {

    @Override
    public void doFilter(final ServletRequest req,final ServletResponse res,final  FilterChain chain) throws IOException, ServletException {

        final HttpServletRequest httpServletRequest = (HttpServletRequest) req;
        final HttpServletResponse httpServletResponse = (HttpServletResponse) res;


        Utente utente = null;
        utente = (Utente) httpServletRequest.getSession().getAttribute("Utente");

        boolean isAdmin = false;
        final boolean isLogged = (utente != null);
        if (isLogged) {
            isAdmin = utente.getPoteri();
        }
        System.out.println(isAdmin);

        final String path = httpServletRequest.getServletPath();
        System.out.println(path);

        //se non ha fatto l'accesso non puo entrare in areautente
        if (path.contains("AreaUtente.jsp") && !isLogged){
            httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/Login.jsp");
            return; // Interrompe l'esecuzione del filtro
        }

        //se ha fatto l'accesso non puo entrare il login e registrazione
        if ((path.contains("Login.jsp") || path.contains("Registrazione.jsp")) && isLogged){
            httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/index.jsp");
            return; // Interrompe l'esecuzione del filtro
        }

        //se non ha fatto l'accesso non puo entrare nella sezione dedicata agli ordini
        if (path.contains("Ordine.jsp") && !isLogged) {
            httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/index.jsp");
            return; // Interrompe l'esecuzione del filtro
        }

        //non puo sloggarsi se non è loggato
        if (path.contains("logOut") && !isLogged){
            httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/index.jsp");
            return; // Interrompe l'esecuzione del filtro
        }

        //non puo usufruire delle funzioni di admin se non è un admin
        if ((path.contains("admin") || path.contains("showTable") || path.contains("deleteRow") || path.contains("editRow") || path.contains("insertRow") || path.contains("showRowForm")) && !isAdmin){
            httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/index.jsp");
            return; // Interrompe l'esecuzione del filtro
        }
        chain.doFilter(req, res);
    }
}
