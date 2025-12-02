package controller.homepage;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Prodotto;
import model.ProdottoDAO;
import model.Variante;
import model.VarianteDAO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(value = "/ProductInfo")
public class ProductServlet extends HttpServlet {

    @Override
    protected void doGet(final HttpServletRequest req,final HttpServletResponse resp) throws ServletException, IOException {
        //prendiamo dalla request la primarykey del prodotto cliccato
        final String primaryKey = req.getParameter("primaryKey");
        System.out.println(primaryKey);
        if(primaryKey != null) {
                //andiamo a prendere tutti i valori del prodotto selezionato
            final ProdottoDAO prodottoDAO = new ProdottoDAO();
            final Prodotto prodotto = prodottoDAO.doRetrieveById(primaryKey);
            if(prodotto != null) {
                final ProdottoDAO suggeritiDAO = new ProdottoDAO();

                //prendiamo tutte le sue varianti
                final VarianteDAO varianteDAO = new VarianteDAO();
                final List<Variante> varianti = prodotto.getVarianti();

                //creiamo una lista di tutti i suoi gusti
                final List<String> gusti = new ArrayList<>();
                for (final Variante v: varianti){
                    if (!gusti.contains(v.getGusto())){
                        gusti.add(v.getGusto());
                    }
                }

                //Lista di pesi associati al gusto della variante di costo inferiore
                final List<Integer> pesi = new ArrayList<>();
                final List<Variante> variantiCriteria = varianteDAO.doRetrieveVariantByCriteria(prodotto.getIdProdotto(), "flavour", varianti.get(0).getGusto());

                for (final Variante y: variantiCriteria){
                    if (!pesi.contains(y.getPesoConfezione()))
                        pesi.add(y.getPesoConfezione());
                }

                req.setAttribute("allTastes", gusti);
                req.setAttribute("firstWeights", pesi);

                //sezione dei suggeriti
                final String category = prodotto.getCategoria();
                final List<Prodotto> suggeriti = suggeritiDAO.doRetrieveByCriteria("categoria",category);
                req.setAttribute("suggeriti",suggeriti);
                req.setAttribute("prodotto",prodotto);
                req.getRequestDispatcher("Product.jsp").forward(req,resp);
            }
        }
    }

    @Override
    protected void doPost(final HttpServletRequest req,final HttpServletResponse resp) throws ServletException, IOException {
        doGet(req,resp);
    }
}
