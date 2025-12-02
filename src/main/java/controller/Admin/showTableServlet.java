package controller.Admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(value = "/showTable")
public class showTableServlet extends HttpServlet {
    @Override
    protected void doGet(final HttpServletRequest req,final HttpServletResponse resp) throws ServletException, IOException {
        final String tableName = req.getParameter("tableName");

        showTable(tableName, req, resp);
    }

    private void showTable(final String tableName,final HttpServletRequest request,final  HttpServletResponse response) throws ServletException, IOException {

        //In base alla tabella scelta dall'admin e inoltrata tramite la request vengono mostrate le tuple della tabella
        switch (tableName) {
            case "utente" -> {
                List<Utente> utenti = new ArrayList<>();
                final UtenteDAO utenteDAO = new UtenteDAO();

                utenti = utenteDAO.doRetrieveAll();
                request.setAttribute("tableUtente", utenti);
                request.getRequestDispatcher("WEB-INF/Admin/tableUtente.jsp").forward(request, response);
            }
            case "prodotto" ->{
                List<Prodotto> prodotti = new ArrayList<>();
                final ProdottoDAO prodottoDAO = new ProdottoDAO();

                prodotti = prodottoDAO.doRetrieveAll();
                request.setAttribute("tableProdotto", prodotti);
                request.getRequestDispatcher("WEB-INF/Admin/tableProdotto.jsp").forward(request, response);
            }
            case "variante" ->{
                List<Variante> varianti = new ArrayList<>();
                final VarianteDAO varianteDAO = new VarianteDAO();

                varianti = varianteDAO.doRetrieveAll();
                request.setAttribute("tableVariante", varianti);

                request.getRequestDispatcher("WEB-INF/Admin/tableVariante.jsp").forward(request, response);
            }
            case "ordine" ->{
                List<Ordine> ordini = new ArrayList<>();
                final OrdineDao ordineDao = new OrdineDao();

                ordini = ordineDao.doRetrieveAll();
                request.setAttribute("tableOrdine", ordini);

                request.getRequestDispatcher("WEB-INF/Admin/tableOrdine.jsp").forward(request, response);
            }case "dettaglioOrdine" ->{
                List<DettaglioOrdine> dettaglioOrdini = new ArrayList<>();
                final DettaglioOrdineDAO dettaglioOrdineDAO = new DettaglioOrdineDAO();
                dettaglioOrdini = dettaglioOrdineDAO.doRetrieveAll();

                request.setAttribute("tableDettaglioOrdini", dettaglioOrdini);

                request.getRequestDispatcher("WEB-INF/Admin/tableDettaglioOrdini.jsp").forward(request, response);
            }case "gusto" ->{
                final  GustoDAO gustoDAO = new GustoDAO();
                final List<Gusto> gusti = gustoDAO.doRetrieveAll();

                request.setAttribute("tableGusto", gusti);

                request.getRequestDispatcher("WEB-INF/Admin/tableGusto.jsp").forward(request, response);
            }case "confezione" ->{
                final  ConfezioneDAO confezioneDAO = new ConfezioneDAO();
                final List<Confezione> confezioni = confezioneDAO.doRetrieveAll();

                request.setAttribute("tableConfezione", confezioni);
                request.getRequestDispatcher("WEB-INF/Admin/tableConfezione.jsp").forward(request, response);
            }
        }


    }

    @Override
    protected void doPost(final HttpServletRequest req,final HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
