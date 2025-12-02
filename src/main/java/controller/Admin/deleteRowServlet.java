package controller.Admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;

import static controller.Admin.showRowForm.*;

@WebServlet(value = "/deleteRow")

public class deleteRowServlet extends HttpServlet {
    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        //prendiamo i dati dalla request
        final String tableName = req.getParameter("tableName");
        final String primaryKey = req.getParameter("primaryKey");
        final Utente utente = (Utente) req.getSession().getAttribute("Utente");

        // usato per controllare se admin cancella il suo stesso profilo
        boolean isTheSame = false;

        JSONArray jsonArray = null;
        final boolean success = switch (tableName) {
            case "utente" -> handleRemoveRowFromUtente(primaryKey);
            case "prodotto" -> handleRemoveRowFromProdotto(primaryKey);
            case "variante" -> handleRemoveRowFromVariante(primaryKey);
            case "ordine" -> handleRemoveRowFromOrdine(primaryKey);
            case "dettaglioOrdine" -> handleRemoveRowFromDettaglioOrdine(primaryKey);
            case "gusto" -> handleRemoveRowFromGusto(primaryKey);
            case "confezione" -> handleRemoveRowFromConfezione(primaryKey);
            default -> false;
        };


        if (success && tableName.equals("utente")) {
            isTheSame = checkIfAdminDeletingSelf(primaryKey, utente);
        }


        if (isTheSame) {
            req.getSession(false).invalidate();
            resp.sendRedirect("index.jsp");
            return; // Interrompe l'esecuzione
        } else {
            if (success) {
                jsonArray = getJsonArrayForTable(tableName);
            }

            if (jsonArray != null) {
                sendJsonResponse(jsonArray, resp);
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid table name or primary key.");
            }
        }
    }


    private boolean checkIfAdminDeletingSelf(final String primaryKey, final Utente utente) {
        System.out.println("Checking if admin is deleting self");  // Log per debug
        return primaryKey != null && !primaryKey.isBlank() && utente.getEmail().equals(primaryKey);
    }




    private boolean handleRemoveRowFromConfezione(final String primaryKey) {
        final ConfezioneDAO confezioneDAO = new ConfezioneDAO();
        if (isValidPrimaryKey(primaryKey)) {
           final int idConfezione = Integer.parseInt(primaryKey);
            confezioneDAO.doRemoveConfezione(idConfezione);
            return true;
        }
        return false;
    }

    private boolean handleRemoveRowFromGusto(final String primaryKey) {
        if (isValidPrimaryKey(primaryKey)) {
            final int idGusto = Integer.parseInt(primaryKey);
            final GustoDAO gustoDAO = new GustoDAO();
            gustoDAO.doRemoveGusto(idGusto);
            return true;
        }
        return false;
    }

    //metodo che rimuove la tupla dalla tabella dettaglio ordine(presenta 2 chiavi primarie)
    private boolean handleRemoveRowFromDettaglioOrdine(final String primaryKey) {
        if (primaryKey != null && !primaryKey.isBlank()) {
            final String[] primaryKeys = primaryKey.split(", ");
            final DettaglioOrdineDAO dettaglioOrdineDAO = new DettaglioOrdineDAO();
            dettaglioOrdineDAO.doRemoveDettaglioOrdine(Integer.parseInt(primaryKeys[0]), Integer.parseInt(primaryKeys[2]));
            return true;
        }
        return false;
    }

    private boolean handleRemoveRowFromOrdine(final String primaryKey) {
        if (isValidPrimaryKey(primaryKey)) {
            final OrdineDao ordineDao = new OrdineDao();
            ordineDao.doDeleteOrder(Integer.parseInt(primaryKey));
            return true;
        }
        return false;
    }

    private boolean handleRemoveRowFromVariante(final String primaryKey) {
        if (isValidPrimaryKey(primaryKey)) {
            final VarianteDAO varianteDAO = new VarianteDAO();
            final Variante v = varianteDAO.doRetrieveVarianteByIdVariante(Integer.parseInt(primaryKey));
            if (v != null) {
                varianteDAO.doRemoveVariante(Integer.parseInt(primaryKey));
            }
            return true;
        }
        return false;
    }

    private boolean handleRemoveRowFromProdotto(final String primaryKey) {
        if (primaryKey != null && !primaryKey.isBlank()) {
            final ProdottoDAO prodottoDAO = new ProdottoDAO();
            final Prodotto p = prodottoDAO.doRetrieveById(primaryKey);
            if (p != null) {
                prodottoDAO.removeProductFromIdProdotto(primaryKey);
            }
            return true;
        }
        return false;
    }

    private boolean handleRemoveRowFromUtente(final String primaryKey){
        if (primaryKey != null && !primaryKey.isBlank()) {
            final UtenteDAO utenteDAO = new UtenteDAO();
            final Utente u = utenteDAO.doRetrieveByEmail(primaryKey);
            if (u != null) {
                utenteDAO.doRemoveUserByEmail(u.getEmail());
                return true;
            }
        }
        return false;
    }

    //metodo che in base a tableName prende tutte le tuple e le inserisce in un JSONArray
    private JSONArray getJsonArrayForTable(final String tableName) {
        return switch (tableName) {
            case "utente" -> getAllUtentiJsonArray(new UtenteDAO());
            case "prodotto" -> getAllProdottiJsonArray(new ProdottoDAO());
            case "variante" -> getAllVariantiJsonArray(new VarianteDAO());
            case "ordine" -> getAllOrdiniJsonArray(new OrdineDao());
            case "dettaglioOrdine" -> getAllDettagliOrdiniJsonArray(new DettaglioOrdineDAO());
            case "gusto" -> getAllGustiJsonArray(new GustoDAO());
            case "confezione" -> getAllConfezioniJsonArray(new ConfezioneDAO());
            default -> null;
        };
    }

    private static JSONArray getAllUtentiJsonArray(final UtenteDAO utenteDAO) {
        final List<Utente> utenti = utenteDAO.doRetrieveAll();
        final JSONArray jsonArray = new JSONArray();
        for (final Utente utente : utenti) {
            jsonArray.add(jsonHelperHere(utente));
        }
        return jsonArray;
    }

    private static JSONArray getAllProdottiJsonArray(final ProdottoDAO prodottoDAO) {
        final List<Prodotto> prodotti = prodottoDAO.doRetrieveAll();
        final JSONArray jsonArray = new JSONArray();
        for (final Prodotto prodotto : prodotti) {
            jsonArray.add(jsonProductHelper(prodotto));
        }
        return jsonArray;
    }

    private static JSONArray getAllVariantiJsonArray(final VarianteDAO varianteDAO) {
        final List<Variante> varianti = varianteDAO.doRetrieveAll();
        final JSONArray jsonArray = new JSONArray();
        for (final Variante variante : varianti) {
            jsonArray.add(jsonVarianteHelper(variante));
        }
        return jsonArray;
    }

    private static JSONArray getAllOrdiniJsonArray(final OrdineDao ordineDao) {
        final List<Ordine> ordini = ordineDao.doRetrieveAll();
        final JSONArray jsonArray = new JSONArray();
        for (final Ordine ordine : ordini) {
            jsonArray.add(jsonOrdineHelper(ordine));
        }
        return jsonArray;
    }

    private static JSONArray getAllDettagliOrdiniJsonArray(final DettaglioOrdineDAO dettaglioOrdineDAO) {
        final List<DettaglioOrdine> dettagliOrdini = dettaglioOrdineDAO.doRetrieveAll();
        final JSONArray jsonArray = new JSONArray();
        for (final DettaglioOrdine dettaglioOrdine : dettagliOrdini) {
            jsonArray.add(dettaglioOrdineHelper(dettaglioOrdine));
        }
        return jsonArray;
    }

    private static JSONArray getAllGustiJsonArray(final GustoDAO gustoDAO) {
        final List<Gusto> gusti = gustoDAO.doRetrieveAll();
        final JSONArray jsonArray = new JSONArray();
        for (final Gusto gusto : gusti) {
            jsonArray.add(gustoHelper(gusto));
        }
        return jsonArray;
    }

    private static JSONArray getAllConfezioniJsonArray(final ConfezioneDAO confezioneDAO) {
        final List<Confezione> confezioni = confezioneDAO.doRetrieveAll();
        final JSONArray jsonArray = new JSONArray();
        for (final Confezione confezione : confezioni) {
            jsonArray.add(confezioneHelper(confezione));
        }
        return jsonArray;
    }

    private static void sendJsonResponse(final JSONArray jsonArray, final HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        final PrintWriter o = response.getWriter();
        o.println(jsonArray);
        o.flush();
    }

    private boolean isValidPrimaryKey(final String primaryKey) {
        return primaryKey != null && !primaryKey.isBlank() && Integer.parseInt(primaryKey) > 0;
    }


    //metodo che crea un oggetto JSON dell'utente
    protected static JSONObject jsonHelperHere(final Utente x) {
        final JSONObject userObject = new JSONObject();
        userObject.put("email", x.getEmail());
        userObject.put("nome", x.getNome());
        userObject.put("cognome", x.getCognome());
        userObject.put("codiceFiscale", x.getCodiceFiscale());
        if (x.getDataNascita() != null) {
            userObject.put("dataDiNascita", new SimpleDateFormat("yyyy-MM-dd").format(x.getDataNascita()));
        } else {
            userObject.put("dataDiNascita", "");
        }
        userObject.put("indirizzo", x.getIndirizzo());
        userObject.put("telefono", x.getTelefono());
        return userObject;
    }

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
