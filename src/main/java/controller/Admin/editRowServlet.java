/*
 * AC-Gainz-Sustainable - Gym focused e-commerce
 * Copyright (C) 2025 Ivan Chiello
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package controller.Admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@WebServlet(value = "/editRow")
@MultipartConfig
public class editRowServlet extends HttpServlet {
    @Override
    protected void doPost(final HttpServletRequest req,final HttpServletResponse resp) throws ServletException, IOException {
        //prende il nome della tabella e la primarykey dalla request
        final String tableName = req.getParameter("tableName");
        System.out.println("tableName: " + tableName);
        final String primaryKey = req.getParameter("primaryKey");

        boolean success = false;

        //in base a quale tabella viene scelta viene chiamato un metodo
        //se il nome della tabella è errato manda un errore
        switch (tableName) {
            case "utente" ->
                success = editUtente(req, primaryKey);
            case "prodotto" ->
                success = editProdotto(req, primaryKey);
            case "variante" ->
                success = editVariante(req, primaryKey);
            case "ordine" ->
                success = editOrdine(req, primaryKey);
            case "dettaglioOrdine" ->
                success = editDettaglioOrdine(req, primaryKey);
            case "gusto" ->
                success = editGusto(req, primaryKey);
            case "confezione" ->
                success = editConfezione(req, primaryKey);
            default ->
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid table name.");
        }

        //se ha funzionato tutto correttamente mostra la tabella
        if (success) {
            req.getRequestDispatcher("showTable?tableName=" + tableName).forward(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid input data.");
        }
    }

    //Controlla se i parametri sono validi
    private boolean isValid(final List<String> params) {
        for (final String param : params) {
            if (param == null || param.isBlank()) {
                return false;
            }
        }
        return true;
    }


    //prende i parametri dalla request,crea un oggetto Utente avente quei parametri e poi tramite il DAO
    //aggiorna i parametri nel database
    private boolean editUtente(final HttpServletRequest req,final String primaryKey) {
        final String email = req.getParameter("email");
        final String nome = req.getParameter("nome");
        final String cognome = req.getParameter("cognome");
        final String codiceFiscale = req.getParameter("codiceFiscale");
        final String dataDiNascita = req.getParameter("dataDiNascita");
        final String indirizzo = req.getParameter("indirizzo");
        final String telefono = req.getParameter("telefono");

        if (isValid(List.of(email, nome, cognome, codiceFiscale, dataDiNascita, indirizzo, telefono))) {
            final Utente u = new Utente();
            u.setEmail(email);
            u.setNome(nome);
            u.setCognome(cognome);
            u.setCodiceFiscale(codiceFiscale);

            final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                final Date ddn = dateFormat.parse(dataDiNascita);
                u.setDataNascita(ddn);
            } catch (final ParseException e) {
                e.printStackTrace();
                return false;
            }

            u.setIndirizzo(indirizzo);
            u.setTelefono(telefono);

            final UtenteDAO utenteDAO = new UtenteDAO();
            utenteDAO.doUpdateCustomer(u, primaryKey);
            return true;
        }
        return false;
    }


    //prende i parametri dalla request,crea un oggetto Prodotto avente quei parametri e poi tramite il DAO
    //aggiorna i parametri nel database
    private boolean editProdotto(final HttpServletRequest req,final String primaryKey) {
        final String idProdotto = req.getParameter("idProdotto");
        final String nome = req.getParameter("nome");
        final String descrizione = req.getParameter("descrizione");
        final String categoria = req.getParameter("categoria");
        final String immagine = req.getParameter("immagine");
        final String calorie = req.getParameter("calorie");
        final String carboidrati = req.getParameter("carboidrati");
        final String proteine = req.getParameter("proteine");
        final String grassi = req.getParameter("grassi");

        if (isValid(List.of(idProdotto, nome, descrizione, categoria, immagine, calorie, carboidrati, proteine, grassi))) {
            final Prodotto p = new Prodotto();
            p.setIdProdotto(idProdotto);
            p.setNome(nome);
            p.setDescrizione(descrizione);
            p.setCategoria(categoria);
            p.setImmagine(immagine);
            p.setCalorie(Integer.parseInt(calorie));
            p.setCarboidrati(Integer.parseInt(carboidrati));
            p.setProteine(Integer.parseInt(proteine));
            p.setGrassi(Integer.parseInt(grassi));

            final ProdottoDAO prodottoDAO = new ProdottoDAO();
            prodottoDAO.updateProduct(p, primaryKey);
            return true;
        }
        return false;
    }


    //prende i parametri dalla request,crea un oggetto Variante avente quei parametri e poi tramite il DAO
    //aggiorna i parametri nel database
    private boolean editVariante(final HttpServletRequest req,final String primaryKey) {
        final String idVariante = req.getParameter("idVariante");
        final String idProdottoVariante = req.getParameter("idProdottoVariante");
        final String idGusto = req.getParameter("idGusto");
        final String idConfezione = req.getParameter("idConfezione");
        final String prezzo = req.getParameter("prezzo");
        final String quantity = req.getParameter("quantity");
        final String sconto = req.getParameter("sconto");
        final String evidenza = req.getParameter("evidenza");

        if (isValid(List.of(idVariante, idProdottoVariante, idGusto, idConfezione, prezzo, quantity, sconto, evidenza))) {
            final Variante v = new Variante();
            v.setIdVariante(Integer.parseInt(idVariante));
            v.setIdProdotto(idProdottoVariante);
            v.setIdGusto(Integer.parseInt(idGusto));
            v.setIdConfezione(Integer.parseInt(idConfezione));
            v.setPrezzo(Float.parseFloat(prezzo));
            v.setQuantita(Integer.parseInt(quantity));
            v.setSconto(Integer.parseInt(sconto));
            v.setEvidenza(Integer.parseInt(evidenza) == 1);

            final VarianteDAO varianteDAO = new VarianteDAO();
            varianteDAO.updateVariante(v, Integer.parseInt(primaryKey));
            return true;
        }
        return false;
    }


    //prende i parametri dalla request,crea un oggetto Ordine avente quei parametri e poi tramite il DAO
    //aggiorna i parametri nel database
    private boolean editOrdine(final HttpServletRequest req,final String primaryKey) {
        final String idOrdine = req.getParameter("idOrdine");
        final String emailUtente = req.getParameter("emailUtente");
        final String dataStr = req.getParameter("data");
        final String stato = req.getParameter("stato");
        final String totaleStr = req.getParameter("totale");


        // Validate if all parameters are valid
        if (isValid(List.of(idOrdine, emailUtente, dataStr, stato, totaleStr))) {
            final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            final Date data;
            try {
                data = dateFormat.parse(dataStr);
            } catch (final ParseException e) {
                e.printStackTrace();
                return false;
            }

            float totale;
            try {
                totale = Float.parseFloat(totaleStr);
            } catch (final NumberFormatException e) {
                e.printStackTrace();
                return false;
            }

            if (totale < 0) {
                return false;
            }

            final Ordine ordine = new Ordine();
            try {
                ordine.setIdOrdine(Integer.parseInt(idOrdine));
            } catch (final NumberFormatException e) {
                e.printStackTrace();
                return false;
            }

            ordine.setStato(stato);
            ordine.setEmailUtente(emailUtente);
            ordine.setTotale(totale);
            ordine.setDataOrdine(data);

            final OrdineDao ordineDao = new OrdineDao();
            try {
                ordineDao.doUpdateOrder(ordine, Integer.parseInt(primaryKey));
            } catch (final NumberFormatException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        return false;
    }

    //prende i parametri dalla request,crea un oggetto DettaglioOrdine avente quei parametri e poi tramite il DAO
    //aggiorna i parametri nel database
    private boolean editDettaglioOrdine(final HttpServletRequest req,final String primaryKey) {
        final String idOrdine = req.getParameter("idOrdine");
        final String idVariante = req.getParameter("idVariante");
        final String idProdotto = req.getParameter("idProdotto");
        final String quantity = req.getParameter("quantity");

        if (isValid(List.of(idOrdine, idVariante, idProdotto, quantity))) {
            final int q = Integer.parseInt(quantity);
            if (q < 0) return false;

            final DettaglioOrdine dettaglioOrdine = new DettaglioOrdine();
            dettaglioOrdine.setIdOrdine(Integer.parseInt(idOrdine));
            dettaglioOrdine.setIdProdotto(idProdotto);
            dettaglioOrdine.setIdVariante(Integer.parseInt(idVariante));
            dettaglioOrdine.setQuantita(q);

            final String[] primaryKeys = primaryKey.split(", ");
            final int firstPK = Integer.parseInt(primaryKeys[0]);
            final String secondPK = primaryKeys[1];
            final int thirdPK = Integer.parseInt(primaryKeys[2]);

            final DettaglioOrdineDAO dettaglioOrdineDAO = new DettaglioOrdineDAO();
            dettaglioOrdineDAO.doUpdateDettaglioOrdine(dettaglioOrdine, firstPK, secondPK, thirdPK);
            return true;
        }
        return false;
    }


    //prende i parametri dalla request,crea un oggetto Gusto avente quei parametri e poi tramite il DAO
    //aggiorna i parametri nel database
    private boolean editGusto(final HttpServletRequest req,final String primaryKey) {
        final String idGusto = req.getParameter("idGusto");
        final String nomeGusto = req.getParameter("nomeGusto");

        if (isValid(List.of(idGusto, nomeGusto))) {
            final Gusto gusto = new Gusto();
            gusto.setIdGusto(Integer.parseInt(idGusto));
            gusto.setNome(nomeGusto);

            final GustoDAO gustoDAO = new GustoDAO();
            gustoDAO.updateGusto(gusto, Integer.parseInt(primaryKey));
            return true;
        }
        return false;
    }


    //prende i parametri dalla request,crea un oggetto Confezione avente quei parametri e poi tramite il DAO
    //aggiorna i parametri nel database
    private boolean editConfezione(final HttpServletRequest req,final String primaryKey) {
        final String idConfezione = req.getParameter("idConfezione");
        final String pesoConfezione = req.getParameter("pesoConfezione");

        if (isValid(List.of(idConfezione, pesoConfezione)) && Integer.parseInt(pesoConfezione) > 0) {
            final Confezione confezione = new Confezione();
            confezione.setIdConfezione(Integer.parseInt(idConfezione));
            confezione.setPeso(Integer.parseInt(pesoConfezione));

            final ConfezioneDAO confezioneDAO = new ConfezioneDAO();
            confezioneDAO.doUpdateConfezione(confezione, Integer.parseInt(primaryKey));
            return true;
        }
        return false;
    }

    @Override
    protected void doGet(final HttpServletRequest req,final HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }
}
