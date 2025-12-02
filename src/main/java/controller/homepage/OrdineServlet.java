package controller.homepage;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(value = "/orderServlet")

public class OrdineServlet extends HttpServlet {
    @Override
    protected void doGet(final HttpServletRequest req,final HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(final HttpServletRequest req,final HttpServletResponse resp) throws ServletException, IOException {
        final HttpSession session = req.getSession(false);
        //prendiamo il carrello dalla sessione
        final List<Carrello> cart = (List<Carrello>) session.getAttribute("cart");

        if (!cart.isEmpty() && session.getAttribute("Utente") != null){
            final Utente x = (Utente) session.getAttribute("Utente");

            //Creiamo un nuovo ordine e lo inseriamo nel DB
            final Ordine ordine = new Ordine();
            ordine.setEmailUtente(x.getEmail());
            final OrdineDao ordineDao = new OrdineDao();
            ordineDao.doSave(ordine);


            //prendiamo l'id dell'ultimo ordine del DB
            final int id_order = ordineDao.getLastInsertedId();

            //creaiamo una lista dei prodotti presenti nell'ordine
            final List<DettaglioOrdine> dettaglioOrdine = new ArrayList<>();
            for (final Carrello cartItem: cart){
                final DettaglioOrdine dettaglioOrdineItem = new DettaglioOrdine();
                dettaglioOrdineItem.setIdOrdine(id_order);
                dettaglioOrdineItem.setIdVariante(cartItem.getIdVariante());
                dettaglioOrdineItem.setIdProdotto(cartItem.getIdProdotto());
                dettaglioOrdineItem.setQuantita(cartItem.getQuantita());
                dettaglioOrdine.add(dettaglioOrdineItem);
            }

            //eliminiamo il carrello visto che abbiamo effettuato l'ordine
            session.removeAttribute("cart");
            final CarrelloDAO carrelloDAO = new CarrelloDAO();
            carrelloDAO.doRemoveCartByUser(x.getEmail());

            //salviamo i dettagli dell'ordine all'interno del DB
            final DettaglioOrdineDAO dettaglioOrdineDAO = new DettaglioOrdineDAO();
            for (final DettaglioOrdine dettaglioOrdineItem : dettaglioOrdine)
                dettaglioOrdineDAO.doSave(dettaglioOrdineItem);


            //prendiamo l'ordine che abbiamo effettuato e mostriamo il resoconto
            final Ordine ordine1 = ordineDao.doRetrieveById(ordineDao.getLastInsertedId());
            List<DettaglioOrdine> dettaglioOrdini1 = new ArrayList<>();
            dettaglioOrdini1 = dettaglioOrdineDAO.doRetrieveById(ordine1.getIdOrdine());
            req.setAttribute("order", ordine1);
            req.setAttribute("orderDetails", dettaglioOrdini1);

            req.getRequestDispatcher("WEB-INF/Ordine.jsp").forward(req, resp);
        }
    }
}
