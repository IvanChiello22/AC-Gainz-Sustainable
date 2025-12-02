package controller.Filters;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Prodotto;
import model.ProdottoDAO;
import model.Variante;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/genericFilter")
public class GenericFilterServlet extends HttpServlet {
    @Override
    protected void doGet(final HttpServletRequest req,final HttpServletResponse resp) throws ServletException, IOException {

        final HttpSession session = req.getSession();
        synchronized (session) { // race condition sulle sessioni possibile dato che si usa ajax

        //Viene chiamato il metodo handleNameForm in base a nameForm passato da request
            final String nameForm = req.getParameter("nameForm");
        if (nameForm != null){
            try {
                handleNameForm(nameForm, req, resp, session);
            } catch (final SQLException e) {
                throw new RuntimeException(e);
            }
            return;
        }


            //prende dalla sessione e dalla request i parametri per i campi dei vari filtri
            final String category = (String) session.getAttribute("categoria");
            String nameFilter = "";
            if (session.getAttribute("searchBarName") != null)
                nameFilter = (String) session.getAttribute("searchBarName");
            final String weightFilter = req.getParameter("weight");
            final String tasteFilter = req.getParameter("taste");
            final String sortingFilter = req.getParameter("sorting");



            List<Prodotto> filteredProducts = new ArrayList<>();
            final ProdottoDAO prodottoDAO = new ProdottoDAO();


            //metodo che ritorna i prodotti filtrati in base a tutti i filtri
            try {
                filteredProducts = prodottoDAO.filterProducts(category, sortingFilter, weightFilter, tasteFilter, nameFilter);
            } catch (final SQLException e) {
                throw new RuntimeException(e);
            }

            // Aggiornare la lista di prodotti filtrati nella sessione per visualizzare i gusti tramite showTasteServlet
            session.setAttribute("filteredProducts", filteredProducts);

            // Inviare la risposta JSON al client
            sendJsonResponse(resp, filteredProducts);
        }
    }


    //metodo che in base al NameForm crea una lista di prodotti che rispettano tale NameForm
    private void handleNameForm(final String nameForm,final HttpServletRequest request,final HttpServletResponse response,final HttpSession session) throws ServletException, IOException, SQLException {
        List<Prodotto> products = new ArrayList<>();

        final ProdottoDAO prodottoDAO = new ProdottoDAO();

        if (nameForm.isBlank()){
            products = prodottoDAO.doRetrieveAll();
            session.removeAttribute("categoria");
        }else {
            products = prodottoDAO.filterProducts("", "", "", "", nameForm);
        }
        request.setAttribute("originalProducts", products);
        session.setAttribute("searchBarName", nameForm);
        session.setAttribute("filteredProducts", products);

        request.getRequestDispatcher("FilterProducts.jsp").forward(request, response);
    }


    // Metodo per inviare la risposta JSON al client
    private void sendJsonResponse(final HttpServletResponse resp,final  List<Prodotto> resultProducts) throws IOException {
        resp.setContentType("application/json");
        final PrintWriter out = resp.getWriter();
        final JSONArray jsonArray = new JSONArray();

        for (final Prodotto p : resultProducts) {
            final JSONObject jsonObject = getJsonObject(p);
            jsonArray.add(jsonObject);
        }

        out.println(jsonArray);
        out.flush();
    }


    //metodo che serve per creare un oggetto JSON del prodotto scelto
    public static JSONObject getJsonObject(final Prodotto p) {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", p.getIdProdotto());
        jsonObject.put("nome", p.getNome());
        jsonObject.put("categoria", p.getCategoria());
        jsonObject.put("calorie", p.getCalorie());
        jsonObject.put("immagine", p.getImmagine());

        final Variante variante = p.getVarianti().get(0);
        jsonObject.put("idVariante", variante.getIdVariante());
        if (variante.getSconto() > 0){
            jsonObject.put("sconto", variante.getSconto());
        }
        jsonObject.put("prezzo", variante.getPrezzo());
        jsonObject.put("gusto", variante.getGusto());
        jsonObject.put("peso", variante.getPesoConfezione());

        return jsonObject;
    }




    @Override
    protected void doPost (final HttpServletRequest req,final HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
