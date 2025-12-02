package controller.utente;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@WebServlet(value = "/areaUtenteServlet")
public class AreaPersonaleServlet extends HttpServlet {
    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(final HttpServletRequest req,final HttpServletResponse resp) throws ServletException, IOException {
        //prendiamo dati utente
        final HttpSession session = req.getSession();
        final Utente utente = (Utente) session.getAttribute("Utente");

        if (utente != null) {
            //prendiamo tutti gli ordini e i relativi dati sul singolo ordine
            final OrdineDao ordineDao = new OrdineDao();
            final List<Ordine> ordini = ordineDao.doRetrieveByEmail(utente.getEmail());
            final HashMap<Integer, List<DettaglioOrdine>> dettaglioOrdini = new HashMap<>();
            final DettaglioOrdineDAO dettaglioOrdineDAO = new DettaglioOrdineDAO();

            for (final Ordine ordine : ordini) {
                //per ogni ordine prendiamo il resoconto dalla sua descrizione in modo da tenere salvati anche
                //eventuali prodotti eliminati dal DB
                if (ordine.getDescrizione() != null && !ordine.getDescrizione().isEmpty()) {
                    final List<DettaglioOrdine> dettagli = parseDescrizione(ordine.getDescrizione());
                    dettaglioOrdini.put(ordine.getIdOrdine(), dettagli);
                } else {
                    final List<DettaglioOrdine> dettagli = dettaglioOrdineDAO.doRetrieveById(ordine.getIdOrdine());
                    dettaglioOrdini.put(ordine.getIdOrdine(), dettagli);
                }
            }

            req.setAttribute("ordini", ordini);
            req.setAttribute("dettaglioOrdini", dettaglioOrdini);
            req.getRequestDispatcher("WEB-INF/AreaUtente.jsp").forward(req, resp);
        }
    }


    //metodo usato per creare un oggetto dettaglioOrdine dalla descrizione dell'ordine
    private static List<DettaglioOrdine> parseDescrizione(final String descrizione) {
        final List<DettaglioOrdine> dettagli = new ArrayList<>();
        //suddividiamo i prodotti nella descrizione
        final String[] prodotti = descrizione.split(";");

        for (final String prodotto : prodotti) {
            //suddividiamo gli attributi del singolo prodotto
            final String[] attributi = prodotto.trim().split("\\n");

            String nomeProdotto = "";
            String gusto = "";
            int pesoConfezione = 0;
            int quantita = 0;
            float prezzo = 0;

            //prendiamo i valori per gli attributi
            for (String attributo : attributi) {
                attributo = attributo.trim(); // Rimuove gli spazi iniziali e finali
                if (attributo.startsWith("Prodotto:")) {
                    nomeProdotto = attributo.replace("Prodotto:", "").trim();
                } else if (attributo.startsWith("Gusto:")) {
                    gusto = attributo.replace("Gusto:", "").trim();
                } else if (attributo.startsWith("Confezione:")) {
                    pesoConfezione = Integer.parseInt(attributo.replace("Confezione:", "").replace(" grammi", "").trim());
                } else if (attributo.startsWith("Quantità:")) {
                    quantita = Integer.parseInt(attributo.replace("Quantità:", "").trim());
                } else if (attributo.startsWith("Prezzo:")) {
                    prezzo = Float.parseFloat(attributo.replace("Prezzo:", "").replace(" €", "").trim());
                }
            }

            //Creiamo il dettaglioOrdine
            final DettaglioOrdine dettaglio = new DettaglioOrdine();
            dettaglio.setNomeProdotto(nomeProdotto);
            dettaglio.setGusto(gusto);
            dettaglio.setPesoConfezione(pesoConfezione);
            dettaglio.setQuantita(quantita);
            dettaglio.setPrezzo(prezzo);

            dettagli.add(dettaglio);
        }

        return dettagli;
    }
}


