package controller.Filters;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Prodotto;
import model.Variante;
import model.VarianteDAO;
import org.json.simple.JSONArray;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/showTastes")
public class ShowTasteServlet extends HttpServlet {

    @Override
    protected void doGet(final HttpServletRequest req,final  HttpServletResponse resp) throws ServletException, IOException {
        List<Prodotto> originalProducts = (List<Prodotto>) req.getSession().getAttribute("filteredProducts");

        if (originalProducts == null) {
            originalProducts = new ArrayList<>();
        }

        // Creare una mappa per contare le occorrenze di ciascun gusto
        final Map<String, Integer> tasteCounts = new HashMap<>();
        final VarianteDAO varianteDAO = new VarianteDAO();

        // Raccogliere tutte le varianti dei prodotti filtrati in una singola query
        final List<Variante> varianti = varianteDAO.doRetrieveVariantiByProdotti(originalProducts);

        // Contare le occorrenze di ciascun gusto
        for (final Variante v : varianti) {
            final String gusto = v.getGusto();
            tasteCounts.put(gusto, tasteCounts.getOrDefault(gusto, 0) + 1);
        }

        // Creare il JSONArray per la risposta contenente ogni varainte
        final JSONArray jsonArray = new JSONArray();
        for (final String key : tasteCounts.keySet()) {
            final String tasteWithCount = key + " (" + tasteCounts.get(key) + ")";
            jsonArray.add(tasteWithCount);
        }

        // Impostare il tipo di contenuto e inviare la risposta
        resp.setContentType("application/json");
        final PrintWriter out = resp.getWriter();
        out.println(jsonArray);
        out.flush();
    }

    @Override
    protected void doPost(final HttpServletRequest req,final HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
