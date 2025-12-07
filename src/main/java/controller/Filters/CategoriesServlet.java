package controller.Filters;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Prodotto;
import model.ProdottoDAO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(value = "/categories")
public class CategoriesServlet extends HttpServlet {
    private static final int PRODUCTS_PER_PAGE = 8;

    @Override
    protected void doGet(final HttpServletRequest req,final HttpServletResponse resp) throws ServletException, IOException {
        //Prendo la categoria dalla request e prendo la sessione
        final String filter = req.getParameter("category");
        final ProdottoDAO prodottoDAO = new ProdottoDAO();
        final HttpSession session = req.getSession();
        List<Prodotto> productsByCriteria = new ArrayList<>();

        //In base alla categoria prendo,tramite metodo DAO,tutte le tuple che soddisfano tale categoria
        if(filter.equals("tutto")){
            productsByCriteria = prodottoDAO.doRetrieveAll();
        }else {
            productsByCriteria = prodottoDAO.doRetrieveByCriteria("categoria", filter);
        }


        //rimuovo per mantenere coerenza con i gusti
        session.removeAttribute("products");
        session.removeAttribute("searchBarName");

        // Pagination logic
        int currentPage = 1;
        String pageParam = req.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                currentPage = Integer.parseInt(pageParam);
            } catch (NumberFormatException e) {
                currentPage = 1;
            }
        }

        int totalProducts = productsByCriteria.size();
        int totalPages = (int) Math.ceil((double) totalProducts / PRODUCTS_PER_PAGE);
        if (totalPages == 0) totalPages = 1;
        
        // Ensure currentPage is within valid range
        if (currentPage < 1) currentPage = 1;
        if (currentPage > totalPages) currentPage = totalPages;

        int startIndex = (currentPage - 1) * PRODUCTS_PER_PAGE;
        int endIndex = Math.min(startIndex + PRODUCTS_PER_PAGE, totalProducts);

        // Handle empty list case
        List<Prodotto> paginatedProducts;
        if (totalProducts > 0 && startIndex < totalProducts) {
            paginatedProducts = productsByCriteria.subList(startIndex, endIndex);
        } else {
            paginatedProducts = new ArrayList<>();
        }

        req.setAttribute("originalProducts", paginatedProducts);
        req.setAttribute("currentPage", currentPage);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("totalProducts", totalProducts);


        //metto nella sessione la categoria scelta
        session.setAttribute("categoria", filter);
        session.setAttribute("categoriaRecovery", filter);

        //setto nella session per vedere i gusti disponibili tramite ajax in showTasteServlet
        session.setAttribute("filteredProducts", productsByCriteria);


        final RequestDispatcher requestDispatcher = req.getRequestDispatcher("FilterProducts.jsp");
        requestDispatcher.forward(req, resp);
    }


    @Override
    protected void doPost(final HttpServletRequest req,final HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }}
