package controller.Admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Utente;

import java.io.IOException;

@WebServlet(value = "/admin")
public class AdminServlet extends HttpServlet {

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {

        //prendiamo l'utente dalla sessione e controlliamo che abbia i poteri per accedere all'area admin
        Utente x = null;
        x = (Utente) req.getSession().getAttribute("Utente");
        if (x != null && x.getPoteri()) {
            req.getRequestDispatcher("WEB-INF/Admin/AreaAdmin.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
