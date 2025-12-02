package controller.homepage;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet(value = "/cartServlet")
public class CarrelloServlet extends HttpServlet {
    @Override
    protected void doGet(final HttpServletRequest req,final HttpServletResponse resp) throws ServletException, IOException {
        final String action = req.getParameter("action");


        final ProdottoDAO prodottoDAO = new ProdottoDAO();
        final HttpSession session = req.getSession();
        synchronized (session) { //uso di synchronized per race conditions su session tramite ajax
            resp.setContentType("application/json");
            final PrintWriter out = resp.getWriter();


            switch (action) {
                case "show" -> handleShowAction(session, prodottoDAO, out);
                case "addVariant" -> handleAddVariantAction(req, session, prodottoDAO, out);
                case "removeVariant" -> handleRemoveVariantAction(req, session, prodottoDAO, out);
                case "quantityVariant" -> handleQuantityVariantAction(req, session, prodottoDAO, out);
            }

        }
    }


    //metodo che permette di visualizzare i prodotti all'interno del carrello
    private void handleShowAction(final HttpSession session,final ProdottoDAO prodottoDAO,final PrintWriter out) throws IOException {
        final List<Carrello> cartItems = (List<Carrello>) session.getAttribute("cart");

        if (cartItems != null && !cartItems.isEmpty()) {
            writeCartItemsToResponse(cartItems, prodottoDAO, out);
        } else {
            final JSONArray jsonArray = new JSONArray();
            out.println(jsonArray);
            out.flush();
            out.close();
        }
    }


    //metodo che viene usato quando viene modificata la quantita di un prodotto nel carrello
    public void handleQuantityVariantAction(final HttpServletRequest request,final HttpSession session,final ProdottoDAO prodottoDAO,final PrintWriter out) throws IOException {
        //prende i dati del prodotto nel carrello dalla request
        final String idProdotto = request.getParameter("id");
        final String gusto = request.getParameter("gusto");
        final int pesoConfezione = Integer.parseInt(request.getParameter("pesoConfezione"));

        final Prodotto p = prodottoDAO.doRetrieveById(idProdotto);
        if (p != null){
            final VarianteDAO varianteDAO = new VarianteDAO();
            final Variante v = varianteDAO.doRetrieveVariantByFlavourAndWeight(p.getIdProdotto(), gusto, pesoConfezione).get(0);
            final List<Carrello> cartItems = (List<Carrello>) session.getAttribute("cart");
            if (v != null && cartItems != null){
                final String quantity = request.getParameter("quantity");
                //controlla che la quantita passata sia corretta e che sia inferiore alla quantita presente nel DB
                if (quantity != null && !quantity.isBlank() && Integer.parseInt(quantity) < v.getQuantita()){
                    final int q = Integer.parseInt(quantity);
                    //nel caso in cui non si rimuova il prodotto
                    if (q < 0) {
                        handleRemoveVariantAction(request, session, prodottoDAO, out);
                    }
                    else {
                        //aggiorna il prezzo in base alla nuova quantità contando ovviamente lo sconto
                        float price = v.getPrezzo();
                        if (v.getSconto() > 0){
                            price = price * (1 - (float) v.getSconto() / 100);
                            price = Math.round(price * 100.0f) / 100.0f;
                        }

                        price *= q;

                        //aggiorna il prodotto nel carrello con la nuova quantita e il nuovo prezzo
                        for (final Carrello c: cartItems){
                            if (c.getIdVariante() == v.getIdVariante()){
                                c.setQuantita(q);
                                c.setPrezzo(price);
                                break;
                            }
                        }

                        session.setAttribute("cart", cartItems);
                        writeCartItemsToResponse(cartItems, prodottoDAO, out);
                    }
                }
            }
        }
    }




    //metodo che viene usato per rimuovere un prodotto dal carrello
    public void handleRemoveVariantAction(final HttpServletRequest request,final HttpSession session,final ProdottoDAO prodottoDAO,final PrintWriter out) throws IOException {
        //prendo i dati del prodotto nel carrello passati dalla request
        final String idToRemove = request.getParameter("id");
        final String gusto = request.getParameter("gusto");
        final int pesoConfezione = Integer.parseInt(request.getParameter("pesoConfezione"));

        final Prodotto p = prodottoDAO.doRetrieveById(idToRemove);

        if (p != null){
            final VarianteDAO varianteDAO = new VarianteDAO();
            final Variante v = varianteDAO.doRetrieveVariantByFlavourAndWeight(p.getIdProdotto(), gusto, pesoConfezione).get(0);
            if (v != null){
                final List<Carrello> cartItems = (List<Carrello>) session.getAttribute("cart");

                //elimina il prodotto dal carrello
                if (cartItems != null) {
                    cartItems.removeIf((final var item) -> item.getIdVariante()  == v.getIdVariante());
                    session.setAttribute("cart", cartItems);

                    writeCartItemsToResponse(cartItems, prodottoDAO, out);
                }
            }
        }
    }




    //metodo che aggiunge al carrello un nuovo prodotto
    private void handleAddVariantAction(final HttpServletRequest request,final HttpSession session,final ProdottoDAO prodottoDAO,final PrintWriter out) throws IOException {
        //prendo il prodotto
        final String id = request.getParameter("id");
        final Prodotto p = prodottoDAO.doRetrieveById(id);
        if (p != null) {
            int quantity = 1;

            //setto la quantita desiderata dall'utente
            if (request.getParameter("quantity") != null) {
                final int x = Integer.parseInt(request.getParameter("quantity"));
                if (x > 0) quantity = x;
            }


            //prendo altri dati relativi al prodotto
            final String gusto = request.getParameter("gusto");
            final String pesoConfezione = request.getParameter("pesoConfezione");



            final VarianteDAO varianteDAO = new VarianteDAO();

            final Variante v = varianteDAO.doRetrieveVariantByFlavourAndWeight(id, gusto, Integer.parseInt(pesoConfezione)).get(0);

            List<Carrello> cartItems = (List<Carrello>) session.getAttribute("cart");
            if (cartItems == null) cartItems = new ArrayList<>();

            if (v != null) {
                float price = v.getPrezzo();

                if (v.getSconto() > 0) {
                    price = price * (1 - (float) v.getSconto() / 100);
                    price = Math.round(price * 100.0f) / 100.0f;
                }

                //controllo che il prodotto da aggiungere non fosse gia presente nel carrello
                //nel caso lo fosse sommo le due quantita e aggiorno ovviamente il prezzo
                boolean itemExists = false;
                if (!cartItems.isEmpty()) {
                    for (final Carrello item : cartItems) {
                        if (item.getIdVariante() == v.getIdVariante()) {
                            final int newQuantity = item.getQuantita() + quantity;
                            if (newQuantity <= v.getQuantita()) {
                                item.setQuantita(newQuantity);
                                item.setPrezzo(item.getPrezzo() + (price * quantity));
                            }
                            itemExists = true;
                            break;
                        }
                    }
                }

                //se il prodotto non è presente nel carrello lo aggiungo
                if (!itemExists && quantity <= v.getQuantita()) {
                    final Carrello c = new Carrello();
                    c.setIdProdotto(id);
                    c.setIdVariante(v.getIdVariante());
                    c.setNomeProdotto(p.getNome());
                    c.setQuantita(quantity);
                    c.setPrezzo(price * quantity);
                    c.setGusto(gusto);
                    c.setPesoConfezione(Integer.parseInt(pesoConfezione));
                    c.setImmagineProdotto(p.getImmagine());
                    cartItems.add(c);
                }

                session.setAttribute("cart", cartItems);
                writeCartItemsToResponse(cartItems, prodottoDAO, out);
            }
        }
    }





    //metodo che per ogni prodotto nel carrello crea un oggetto JSON e lo inserisce in un JSONArray
    //metodo che crea quindi il carrello effettivo
    private void writeCartItemsToResponse(final List<Carrello> cartItems,final  ProdottoDAO prodottoDAO,final PrintWriter out) throws IOException{
        final JSONArray jsonArray = new JSONArray();
        float totalPrice = 0;

        for (final Carrello item: cartItems){
            final Prodotto p = prodottoDAO.doRetrieveById(item.getIdProdotto());
            final JSONObject jsonObject = new JSONObject();
            //crea l'oggetto JSON con tutti i valori del prodotto
            jsonObject.put("idProdotto", item.getIdProdotto());
            jsonObject.put("idVariante", item.getIdVariante());
            jsonObject.put("nomeProdotto", p.getNome());
            jsonObject.put("imgSrc", p.getImmagine());
            jsonObject.put("flavour", item.getGusto());
            jsonObject.put("weight", item.getPesoConfezione());
            jsonObject.put("quantity", item.getQuantita());

            //prezzo in base allo sconto che presenta il prodotto
            float itemPrice = item.getPrezzo();
            itemPrice = Math.round(itemPrice * 100.0f) / 100.0f;
            jsonObject.put("prezzo", itemPrice);

            //prezzo totale del carrello
            totalPrice += item.getPrezzo();

            jsonArray.add(jsonObject);
        }

        totalPrice = Math.round(totalPrice * 100.0f) / 100.0f;

        final JSONObject totalPriceObject = new JSONObject();
        totalPriceObject.put("totalPrice", totalPrice);
        jsonArray.add(totalPriceObject);

        out.println(jsonArray);
        out.flush();
        out.close();
    }


    @Override
    protected void doPost(final HttpServletRequest req,final HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
