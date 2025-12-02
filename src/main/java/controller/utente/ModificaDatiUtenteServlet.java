package controller.utente;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Utente;
import model.UtenteDAO;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet("/editServlet")
public class ModificaDatiUtenteServlet extends HttpServlet {

    private static final Pattern PASSWORD_REGEX = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\w\\s]).{8,}$");
    private static final Pattern PHONE_REGEX = Pattern.compile("^3[0-9]{8,9}$");
    private static final Pattern CODFISC_REGEX = Pattern.compile("^[A-Z]{6}\\d{2}[A-Z]\\d{2}[A-Z]\\d{3}[A-Z]$");

    @Override
    protected void doGet(final HttpServletRequest req,final  HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(final HttpServletRequest req,final HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    protected void processRequest(final HttpServletRequest request,final  HttpServletResponse response) throws ServletException, IOException {
        final String field = request.getParameter("field");

        final HttpSession session = request.getSession();
        final Utente utente = (Utente) session.getAttribute("Utente");
        final RequestDispatcher requestDispatcher = request.getRequestDispatcher("areaUtenteServlet");
        final UtenteDAO utenteDAO = new UtenteDAO();


        //gestiamo in base a cosa l'utente vuole modificare
        switch (field) {
            case "password" ->
                handlePasswordChange(request, response, utente, requestDispatcher, utenteDAO);
            case "address" ->
                handleAddressChange(request, response, utente, requestDispatcher, utenteDAO);
            case "phone" ->
                handlePhoneChange(request, response, utente, requestDispatcher, utenteDAO);
            case "codice-fiscale" ->
                handleCodiceFiscaleChange(request, response, utente, requestDispatcher, utenteDAO);
            case "data-di-nascita" ->
                handleDataNascitaChange(request, response, utente, requestDispatcher, utenteDAO);
            case "nome" ->
                handleNomeChange(request, response, utente, requestDispatcher, utenteDAO);
            case "cognome" ->
                handleCognomeChange(request, response, utente, requestDispatcher, utenteDAO);
            default ->{
                request.setAttribute("error", "Invalid field parameter");
                requestDispatcher.forward(request, response);
            }
        }
    }


    //metodo per il cambio password
    private void handlePasswordChange(final HttpServletRequest request,final  HttpServletResponse response,
                                      final Utente utente,final  RequestDispatcher requestDispatcher,final  UtenteDAO utenteDAO)
            throws ServletException, IOException {

        //prendiamo i dati dal form
        final String currentPassword = request.getParameter("current-password");
        final String newPassword = request.getParameter("new-password");
        final String confirmPassword = request.getParameter("confirm-password");

        //controlliamo che sia corretto
        if (currentPassword == null || newPassword == null || confirmPassword == null) {
            request.setAttribute("messageType", "error");
            request.setAttribute("message", "Missing parameters");
            request.setAttribute("field", "password");
            requestDispatcher.forward(request, response);
            return;
        }

        //controlliamo che rispettino il pattern
        final Matcher newPasswordMatcher = PASSWORD_REGEX.matcher(newPassword);

        //se la nuova password non rispetta i parametri
        if (!newPasswordMatcher.matches()) {
            request.setAttribute("messageType", "error");
            request.setAttribute("message", "Pattern non rispettato");
            request.setAttribute("field", "password");
            requestDispatcher.forward(request, response);
            return;
        }

        final Matcher confirmPasswordMatcher = PASSWORD_REGEX.matcher(confirmPassword);
        //se la conferma della nuova password non rispetta i parametri
        if (!confirmPasswordMatcher.matches()) {
            request.setAttribute("messageType", "error");
            request.setAttribute("message", "Pattern non rispettato");
            request.setAttribute("field", "password");
            requestDispatcher.forward(request, response);
            return;
        }

        final String hashedCurrentPassword = sha1(currentPassword);

        System.out.println("Hashed current password: " + hashedCurrentPassword);
        System.out.println("Stored password: " + utente.getPassword());

        //se la password corrente inserita è quella presente nel DB
        if (!hashedCurrentPassword.equals(utente.getPassword())) {
            request.setAttribute("messageType", "error");
            request.setAttribute("message", "Password attuale non corretta");
            request.setAttribute("field", "password");
            requestDispatcher.forward(request, response);
            return;
        }

        //se la nuova password e la conferma della nuova password corrispondono
        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("messageType", "error");
            request.setAttribute("message", "Le password non corrispondono");
            request.setAttribute("field", "password");
            requestDispatcher.forward(request, response);
            return;
        }

        //settiamo la nuova password
        utente.setPassword(newPassword);
        utente.hashPassword();

        System.out.println(utente.getPassword());

        request.getSession().setAttribute("Utente", utente);

        utenteDAO.doUpdateCustomerGeneric(utente, "password", utente.getPassword());
        System.out.println("Password Cambiata con: " + utente.getPassword());
        request.setAttribute("messageType", "success");
        request.setAttribute("message", "Password modificata con successo");
        request.setAttribute("field", "password");
        requestDispatcher.forward(request, response);
    }

    private void handleAddressChange(final HttpServletRequest request,final HttpServletResponse response,final Utente utente,
                                     final RequestDispatcher requestDispatcher,final UtenteDAO utenteDAO)
            throws ServletException, IOException {

        //prendo l'indirizzo dal form
        final String indirizzo = request.getParameter("street");

        //controlliamo che sia corretto
        if (indirizzo == null) {
            request.setAttribute("messageType", "error");
            request.setAttribute("message", "Missing parameters");
            request.setAttribute("field", "address");
            requestDispatcher.forward(request, response);
            return;
        }

        //settiamo il nuovo indirizzo
        utente.setIndirizzo(indirizzo);
        request.getSession().setAttribute("Utente", utente);
        utenteDAO.doUpdateCustomerGeneric(utente, "indirizzo", indirizzo);

        request.setAttribute("messageType", "success");
        request.setAttribute("message", "Indirizzo modificato con successo");
        request.setAttribute("field", "address");
        requestDispatcher.forward(request, response);
    }

    private void handlePhoneChange(final HttpServletRequest request,final HttpServletResponse response,final Utente utente,
                                   final RequestDispatcher requestDispatcher,final UtenteDAO utenteDAO)
            throws ServletException, IOException {
        //prendiamo il parametro del form
        final String telefono = request.getParameter("new-phone");

        //controlliamo che sia correttamente
        if (telefono == null) {
            request.setAttribute("messageType", "error");
            request.setAttribute("message", "Missing parameters");
            request.setAttribute("field", "address");
            requestDispatcher.forward(request, response);
            return;
        }

        //controlliamo che rispetti il pattern

        final Matcher phoneMatcher = PHONE_REGEX.matcher(telefono);

        if (!phoneMatcher.matches()) {
            request.setAttribute("messageType", "error");
            request.setAttribute("message", "Pattern non rispettato");
            request.setAttribute("field", "phone");
            requestDispatcher.forward(request, response);
            return;
        }

        //settiamo il nuovo numero di telefono
        utente.setTelefono(telefono);
        request.getSession().setAttribute("Utente", utente);
        utenteDAO.doUpdateCustomerGeneric(utente, "numero_di_cellulare", telefono);

        request.setAttribute("messageType", "success");
        request.setAttribute("message", "Numero di telefono modificato con successo");
        request.setAttribute("field", "phone");
        requestDispatcher.forward(request, response);
    }

    private void handleCodiceFiscaleChange(final HttpServletRequest request,final  HttpServletResponse response,final  Utente utente,
                                           final RequestDispatcher requestDispatcher,final  UtenteDAO utenteDAO)
            throws ServletException, IOException {
        //prendiamo il dato dal form
        final String codFiscale = request.getParameter("new-codice-fiscale");

        //controlliamo che sia arrivato correttamente
        if (codFiscale == null) {
            request.setAttribute("messageType", "error");
            request.setAttribute("message", "Missing parameters");
            request.setAttribute("field", "codice-fiscale");
            requestDispatcher.forward(request, response);
            return;
        }

        //controlliamo che rispetti il pattern
        final Matcher codFiscaleMatcher = CODFISC_REGEX.matcher(codFiscale);

        //nel caso non rispetti il pattern
        if (!codFiscaleMatcher.matches()) {
            request.setAttribute("messageType", "error");
            request.setAttribute("message", "Pattern non rispettato");
            request.setAttribute("field", "codice-fiscale");
            requestDispatcher.forward(request, response);
            return;
        }

        //aggiorniamo il campo
        utente.setCodiceFiscale(codFiscale);
        request.getSession().setAttribute("Utente", utente);
        utenteDAO.doUpdateCustomerGeneric(utente, "codice_fiscale", codFiscale);

        request.setAttribute("messageType", "success");
        request.setAttribute("message", "Codice fiscale modificato con successo");
        request.setAttribute("field", "codice-fiscale");
        requestDispatcher.forward(request, response);
    }

    private void handleDataNascitaChange(final HttpServletRequest request,final HttpServletResponse response,final Utente utente,
                                         final RequestDispatcher requestDispatcher,final UtenteDAO utenteDAO)
            throws ServletException, IOException {
        //prendiamo il dato del form
        final String ddn = request.getParameter("new-birthdate");

        //nel caso non fosse arrivato correttamente
        if (ddn == null) {
            request.setAttribute("messageType", "error");
            request.setAttribute("message", "Missing parameters");
            request.setAttribute("field", "data-di-nascita");
            requestDispatcher.forward(request, response);
            return;
        }

        final String[] fieldsDate = ddn.split("-");
        final int flag = Integer.parseInt(fieldsDate[0]);

        if (flag < 1900) {
            request.setAttribute("messageType", "error");
            request.setAttribute("message", "Anno minimo non rispettato");
            request.setAttribute("field", "data-di-nascita");
            requestDispatcher.forward(request, response);
            return;
        }

        //aggiorniamo il campo
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            final Date dateformatted = sdf.parse(ddn);
            utente.setDataNascita(dateformatted);
            request.getSession().setAttribute("Utente", utente);
            utenteDAO.doUpdateCustomerGeneric(utente, "data_di_nascita", ddn);

            request.setAttribute("messageType", "success");
            request.setAttribute("message", "Data di nascita modificata con successo");
            request.setAttribute("field", "data-di-nascita");
            requestDispatcher.forward(request, response);
        } catch (final ParseException e) {
            throw new ServletException("Errore nel parsing della data di nascita", e);
        }
    }

    private void handleNomeChange(final HttpServletRequest request,final HttpServletResponse response,final Utente utente,
                                  final RequestDispatcher requestDispatcher,final  UtenteDAO utenteDAO)
            throws ServletException, IOException {
        //prendiamo il dato del form
        final String nome = request.getParameter("new-name");

        //nel caso non fosse arrivato correttamente
        if (nome == null) {
            request.setAttribute("messageType", "error");
            request.setAttribute("message", "Missing parameters");
            request.setAttribute("field", "nome");
            requestDispatcher.forward(request, response);
            return;
        }

        //aggiorniamo il campo
        utente.setNome(nome);
        request.getSession().setAttribute("Utente", utente);
        utenteDAO.doUpdateCustomerGeneric(utente, "nome", nome);

        request.setAttribute("messageType", "success");
        request.setAttribute("message", "Nome modificato con successo");
        request.setAttribute("field", "nome");
        requestDispatcher.forward(request, response);
    }

    private void handleCognomeChange(final HttpServletRequest request,final HttpServletResponse response,final Utente utente,
                                     final RequestDispatcher requestDispatcher,final UtenteDAO utenteDAO)
            throws ServletException, IOException {
        //prendiamo il dato dal form
        final String cognome = request.getParameter("new-surname");

        //nel caso non fosse arrivato correttamente
        if (cognome == null) {
            request.setAttribute("messageType", "error");
            request.setAttribute("message", "Missing parameters");
            request.setAttribute("field", "cognome");
            requestDispatcher.forward(request, response);
            return;
        }

        //aggiorniamo il campo
        utente.setCognome(cognome);
        request.getSession().setAttribute("Utente", utente);
        utenteDAO.doUpdateCustomerGeneric(utente, "cognome", cognome);

        request.setAttribute("messageType", "success");
        request.setAttribute("message", "Cognome modificato con successo");
        request.setAttribute("field", "cognome");
        requestDispatcher.forward(request, response);
    }

    public String sha1(String pass) {
        try {
            final var digest =
                    MessageDigest.getInstance("SHA-1");
            digest.reset();
            digest.update(pass.getBytes(StandardCharsets.UTF_8));
            pass = String.format("%040x", new
                    BigInteger(1, digest.digest()));
        } catch (final NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        return pass;
    }
}
