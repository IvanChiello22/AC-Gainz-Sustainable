package controller.Admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import model.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@WebServlet("/insertRow")
@MultipartConfig
public class insertRowServlet extends HttpServlet {
    private static final String CARTELLA_UPLOAD = "Immagini";

    @Override
    protected void doGet(final HttpServletRequest req,final HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(final HttpServletRequest req,final HttpServletResponse resp) throws ServletException, IOException {
        final String nameTable = req.getParameter("nameTable");
        System.out.println(nameTable);

        boolean success = false;

        //in base a quale tabella viene scelta viene chiamato un metodo
        //se il nome della tabella è errato manda un errore
        switch (nameTable) {
            case "utente" ->
                success = insertUtente(req);
            case "prodotto" ->
                success = insertProdotto(req);
            case "variante" ->
                success = insertVariante(req);
            case "ordine" ->
                success = insertOrdine(req);
            case "dettagliOrdine" ->
                success = insertDettaglioOrdine(req);
            case "gusto" ->
                success = insertGusto(req);
            case "confezione" ->
                success = insertConfezione(req);

            default ->{
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid table name.");
                return;
            }
        }

        //se ha funzionato tutto correttamente mostra la tabella
        if (success) {
            req.getRequestDispatcher("showTable?tableName=" + nameTable).forward(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid input data.");
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


    //prende i parametri dalla request,controlla che siano validi,
    //crea un oggetto Utente avente quei parametri e poi tramite il DAO
    //salva il nuovo Utente nel database tramite metodo DAO
    private boolean insertUtente(final HttpServletRequest req) {
        final String email = req.getParameter("email");
        final String password = req.getParameter("password");
        final String nome = req.getParameter("nome");
        final String cognome = req.getParameter("cognome");
        final String codiceFiscale = req.getParameter("codiceFiscale");
        final String dataDiNascita = req.getParameter("dataDiNascita");
        final String indirizzo = req.getParameter("indirizzo");
        final String telefono = req.getParameter("telefono");

        if (isValid(List.of(email, password, nome, cognome, codiceFiscale, dataDiNascita, indirizzo, telefono))) {
            final Utente u = new Utente();
            u.setEmail(email);
            u.setPassword(password);
            u.hashPassword();
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
            utenteDAO.doSave(u);
            return true;
        }
        return false;
    }


    //prende i parametri dalla request,controlla che siano validi,
    //crea un oggetto Prodotto avente quei parametri e poi tramite il DAO
    //salva il nuovo Prodotto nel database tramite metodo DAO
    private boolean insertProdotto(final HttpServletRequest req) throws IOException, ServletException {
        final String idProdotto = req.getParameter("idProdotto");
        final String nome = req.getParameter("nome");
        final String descrizione = req.getParameter("descrizione");
        final String categoria = req.getParameter("categoria");
        final Part filePart = req.getPart("immagine");

        //Per l'immagine
        final String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        String destinazione = CARTELLA_UPLOAD + "/" + fileName;
        Path pathDestinazione = Paths.get(getServletContext().getRealPath(destinazione));

        int i = 2;
        while (Files.exists(pathDestinazione)) {
            destinazione = CARTELLA_UPLOAD + "/" + i + "_" + fileName;
            pathDestinazione = Paths.get(getServletContext().getRealPath(destinazione));
            ++i;
        }

        final InputStream fileInputStream = filePart.getInputStream();
        Files.createDirectories(pathDestinazione.getParent());
        Files.copy(fileInputStream, pathDestinazione);

        final String calorie = req.getParameter("calorie");
        final String carboidrati = req.getParameter("carboidrati");
        final String proteine = req.getParameter("proteine");
        final String grassi = req.getParameter("grassi");

        if (isValid(List.of(idProdotto, nome, descrizione, categoria, calorie, carboidrati, proteine, grassi))) {
            final Prodotto p = new Prodotto();
            p.setIdProdotto(idProdotto);
            p.setNome(nome);
            p.setDescrizione(descrizione);
            p.setCategoria(categoria);
            p.setImmagine(destinazione);
            p.setCalorie(Integer.parseInt(calorie));
            p.setCarboidrati(Integer.parseInt(carboidrati));
            p.setProteine(Integer.parseInt(proteine));
            p.setGrassi(Integer.parseInt(grassi));

            final ProdottoDAO prodottoDAO = new ProdottoDAO();
            prodottoDAO.doSave(p);
            return true;
        }
        return false;
    }


    //prende i parametri dalla request,controlla che siano validi,
    //crea un oggetto Variante avente quei parametri e poi tramite il DAO
    //salva il nuovo Variante nel database tramite metodo DAO
    private boolean insertVariante(final HttpServletRequest req) {
        final String idProdottoVariante = req.getParameter("idProdottoVariante");
        final String idGusto = req.getParameter("idGusto");
        final String idConfezione = req.getParameter("idConfezione");
        final String prezzo = req.getParameter("prezzo");
        final String quantity = req.getParameter("quantity");
        final String sconto = req.getParameter("sconto");
        final String evidenza = req.getParameter("evidenza");

        if (isValid(List.of(idProdottoVariante, idGusto, idConfezione, prezzo, quantity, sconto))) {
            final float price = Float.parseFloat(prezzo);
            final int q = Integer.parseInt(quantity);
            final int discount = Integer.parseInt(sconto);
            boolean evidence = false;
            if (evidenza != null && !evidenza.isEmpty()) {
                evidence = Integer.parseInt(evidenza) == 1;
            }
            if (price > 0 && q > 0 && discount >= 0 && discount <= 100) {
                final Variante v = new Variante();
                v.setIdProdotto(idProdottoVariante);
                v.setIdGusto(Integer.parseInt(idGusto));
                v.setIdConfezione(Integer.parseInt(idConfezione));
                v.setPrezzo(price);
                v.setQuantita(q);
                v.setSconto(discount);
                v.setEvidenza(evidence);

                final VarianteDAO varianteDAO = new VarianteDAO();
                varianteDAO.doSaveVariante(v);
                return true;
            }
        }
        return false;
    }


    //prende i parametri dalla request,controlla che siano validi,
    //crea un oggetto Ordine avente quei parametri e poi tramite il DAO
    //salva il nuovo Ordine nel database tramite metodo DAO
    private boolean insertOrdine(final HttpServletRequest req) {
        final String emailUtente = req.getParameter("emailUtente");
        final String stato = req.getParameter("stato");
        final String totale = req.getParameter("totale");
        final String dataStr = req.getParameter("data");

        if (emailUtente != null && !emailUtente.isBlank()) {
            final Ordine ordine = new Ordine();
            ordine.setEmailUtente(emailUtente);
            ordine.setStato(stato);

            if (totale != null && !totale.isBlank() && Float.parseFloat(totale) >= 0) {
                ordine.setTotale(Float.parseFloat(totale));
            }

            if (dataStr != null && !dataStr.isBlank()) {
                final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    final Date data = dateFormat.parse(dataStr);
                    ordine.setDataOrdine(data);
                } catch (final ParseException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            final OrdineDao ordineDao = new OrdineDao();
            ordineDao.doSave(ordine);
            return true;
        }
        return false;
    }


    //prende i parametri dalla request,controlla che siano validi,
    //crea un oggetto DettaglioOrdine avente quei parametri e poi tramite il DAO
    //salva il nuovo DettaglioOrdine nel database tramite metodo DAO
    private boolean insertDettaglioOrdine(final HttpServletRequest req) {
        final String idOrdine = req.getParameter("idOrdine");
        final String idProdotto = req.getParameter("idProdotto");
        final String idVariante = req.getParameter("idVariante");
        final String quantity = req.getParameter("quantity");

        if (isValid(List.of(idOrdine, idProdotto, idVariante, quantity))) {
            final int q = Integer.parseInt(quantity);

            if (q > 0) {
                final DettaglioOrdine dettaglioOrdine = new DettaglioOrdine();
                dettaglioOrdine.setIdOrdine(Integer.parseInt(idOrdine));
                dettaglioOrdine.setIdProdotto(idProdotto);
                dettaglioOrdine.setIdVariante(Integer.parseInt(idVariante));
                dettaglioOrdine.setQuantita(q);

                final DettaglioOrdineDAO dettaglioOrdineDAO = new DettaglioOrdineDAO();
                dettaglioOrdineDAO.doSave(dettaglioOrdine);
                return true;
            }
        }
        return false;
    }


    //prende i parametri dalla request,controlla che siano validi,
    //crea un oggetto Gusto avente quei parametri e poi tramite il DAO
    //salva il nuovo Gusto nel database tramite metodo DAO
    private boolean insertGusto(final HttpServletRequest req) {
        final String nomeGusto = req.getParameter("nomeGusto");

        if (nomeGusto != null && !nomeGusto.isBlank()) {
            final Gusto gusto = new Gusto();
            gusto.setNome(nomeGusto);

            final GustoDAO gustoDAO = new GustoDAO();
            gustoDAO.doSaveGusto(gusto);
            return true;
        }
        return false;
    }


    //prende i parametri dalla request,controlla che siano validi,
    //crea un oggetto Confezione avente quei parametri e poi tramite il DAO
    //salva la nuova Confezione nel database tramite metodo DAO
    private boolean insertConfezione(final HttpServletRequest req) {
        final String peso = req.getParameter("pesoConfezione");

        if (peso != null && Integer.parseInt(peso) > 0) {
            final Confezione confezione = new Confezione();
            confezione.setPeso(Integer.parseInt(peso));

            final ConfezioneDAO confezioneDAO = new ConfezioneDAO();
            confezioneDAO.doSaveConfezione(confezione);
            return true;
        }
        return false;
    }
}
