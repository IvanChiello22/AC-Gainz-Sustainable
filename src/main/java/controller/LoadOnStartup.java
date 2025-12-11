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

package controller;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import model.Prodotto;
import model.ProdottoDAO;

import java.util.List;

@WebServlet(value = "/loadOnStartUp", loadOnStartup = 0)
public class LoadOnStartup extends HttpServlet {
    @Override
    public void init(final ServletConfig config) throws ServletException {
        super.init(config);

        final ProdottoDAO prodottoDAO = new ProdottoDAO();
        final List<Prodotto> prodottoList = prodottoDAO.doRetrieveAll();

        // Salva i prodotti nel servletContext
        getServletContext().setAttribute("Products", prodottoList);
    }
}
