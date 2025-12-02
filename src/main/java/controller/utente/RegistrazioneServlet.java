package controller.utente;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Utente;
import model.UtenteDAO;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet("/register")
public class RegistrazioneServlet extends HttpServlet {

    //definiamo i pattern da dover rispettare
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,8}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\w\\s]).{8,}$");
    private static final Pattern COD_FISCALE_PATTERN = Pattern.compile("^[A-Z]{6}\\d{2}[A-Z]\\d{2}[A-Z]\\d{3}[A-Z]$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^3[0-9]{8,9}$");
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");

    public void doGet(final HttpServletRequest request,final HttpServletResponse response) throws IOException, ServletException {
        this.doPost(request, response); // Delegate GET requests to doPost
    }

    public void doPost(final HttpServletRequest request,final HttpServletResponse response) throws IOException, ServletException {
        //prendiamo i dati dal form
        final String email = request.getParameter("email");
        final String password = request.getParameter("password");
        final String nome = request.getParameter("nome");
        final String cognome = request.getParameter("cognome");
        final String codiceFiscale = request.getParameter("codiceFiscale");
        final String dateString = request.getParameter("dataDiNascita");
        final String indirizzo = request.getParameter("indirizzo");
        final String numCellulare = request.getParameter("numCellulare");

        // Validifichiamo l'email
        if (!isValidEmail(email)) {
            request.setAttribute("error", "Pattern email non rispettato");
            request.getRequestDispatcher("Registrazione.jsp").forward(request, response);
            return;
        }

        // Controlliamo se l'email non sia gia presente
        final UtenteDAO utenteDAO = new UtenteDAO();
        if (utenteDAO.doRetrieveByEmail(email) != null) {
            request.setAttribute("error", "Email già registrata.");
            request.getRequestDispatcher("Registrazione.jsp").forward(request, response);
            return;
        }

        // Validifichiamo la password
        if (!isValidPassword(password)) {
            request.setAttribute("error", "Pattern password non rispettato");
            request.getRequestDispatcher("Registrazione.jsp").forward(request, response);
            return;
        }

        // Validifichiamo il codice fiscale
        if (!isValidCodiceFiscale(codiceFiscale)) {
            request.setAttribute("error", "Pattern codice fiscale non rispettato");
            request.getRequestDispatcher("Registrazione.jsp").forward(request, response);
            return;
        }

        // Parse della data di nascita
        final Date dataDiNascita = parseDate(dateString);
        if (dataDiNascita == null) {
            request.setAttribute("error", "Pattern data non rispettato");
            request.getRequestDispatcher("Registrazione.jsp").forward(request, response);
            return;
        }

        // Validifichiamo il numero di telefono
        if (!isValidPhone(numCellulare)) {
            request.setAttribute("error", "Pattern numero di telefono non rispettato");
            request.getRequestDispatcher("Registrazione.jsp").forward(request, response);
            return;
        }

        // Dopo aver passato tutte le validificazioni creiamo l'oggetto utente da salvare nel DB
        final Utente utente = new Utente();
        utente.setEmail(email);
        utente.setPassword(password);
        utente.hashPassword();
        utente.setCodiceFiscale(codiceFiscale);
        utente.setNome(nome);
        utente.setCognome(cognome);
        utente.setIndirizzo(indirizzo);
        utente.setTelefono(numCellulare);
        utente.setDataNascita(dataDiNascita);

        utenteDAO.doSave(utente);

        // Store user in session and forward to index.jsp
        final HttpSession session = request.getSession();
        session.setAttribute("Utente", utente);
        response.sendRedirect("index.jsp");
    }



    // Metodo per validificare l'email
    private boolean isValidEmail(final String email) {
        final Matcher emailMatcher = EMAIL_PATTERN.matcher(email);
        return emailMatcher.matches();
    }

    // Metodo per validificare la password
    private boolean isValidPassword(final String password) {
        final Matcher passwordMatcher = PASSWORD_PATTERN.matcher(password);
        return passwordMatcher.matches();
    }

    // Metodo per validificare codice fiscale
    private boolean isValidCodiceFiscale(final String codiceFiscale) {
        final Matcher codFiscaleMatcher = COD_FISCALE_PATTERN.matcher(codiceFiscale);
        return codFiscaleMatcher.matches();
    }

    // Metodo per il parse della data
    private Date parseDate(final String dateString) {
        try {
            return DATE_FORMATTER.parse(dateString);
        } catch (final ParseException e) {
            return null;
        }
    }

    // Method to validificare il numero di telefono
    private boolean isValidPhone(final String numCellulare) {
        final Matcher phoneMatcher = PHONE_PATTERN.matcher(numCellulare);
        return phoneMatcher.matches();
    }
}
