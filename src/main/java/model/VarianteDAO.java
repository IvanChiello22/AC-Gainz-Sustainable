package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VarianteDAO {

    public List<Variante> doRetrieveVariantiByIdProdotto(final String idProdotto){
        final List<Variante> varianti = new ArrayList<>();

        try (final Connection connection = ConPool.getConnection()){
            final PreparedStatement preparedStatement = connection.prepareStatement("select v.*, g.nomeGusto, c.peso from variante v join gusto g on v.id_gusto = g.id_gusto join confezione c on v.id_confezione = c.id_confezione " +
                    "where id_prodotto_variante = ? order by (v.prezzo * (1 - v.sconto / 100)) asc");
            preparedStatement.setString(1, idProdotto);

            final ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                final Variante variante = new Variante();
                variante.setIdVariante(resultSet.getInt("id_variante"));
                variante.setIdProdotto(resultSet.getString("id_prodotto_variante"));
                variante.setIdGusto(resultSet.getInt("id_gusto"));
                variante.setIdConfezione(resultSet.getInt("id_confezione"));
                variante.setQuantita(resultSet.getInt("quantità"));
                variante.setPrezzo(resultSet.getFloat("prezzo"));
                variante.setSconto(resultSet.getInt("sconto"));
                variante.setEvidenza(resultSet.getBoolean("evidenza"));
                variante.setGusto(resultSet.getString("nomeGusto"));
                variante.setPesoConfezione(resultSet.getInt("peso"));

                varianti.add(variante);
            }


        }catch (final SQLException e){
            throw new RuntimeException(e);
        }

        return varianti;
    }


    public List<Variante> doRetrieveAll(){
        final List<Variante> varianti = new ArrayList<>();

        try (final Connection connection = ConPool.getConnection()){
            final PreparedStatement preparedStatement = connection.prepareStatement("select v.id_variante, v.id_prodotto_variante, v.id_gusto, v.id_confezione, v.prezzo, v.quantità, v.sconto, v.evidenza from variante v");

            final ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                final Variante variante = new Variante();
                variante.setIdVariante(resultSet.getInt("id_variante"));
                variante.setIdProdotto(resultSet.getString("id_prodotto_variante"));
                variante.setIdGusto(resultSet.getInt("id_gusto"));
                variante.setIdConfezione(resultSet.getInt("id_confezione"));
                variante.setQuantita(resultSet.getInt("quantità"));
                variante.setPrezzo(resultSet.getFloat("prezzo"));
                variante.setSconto(resultSet.getInt("sconto"));
                variante.setEvidenza(resultSet.getBoolean("evidenza"));

                varianti.add(variante);
            }
        }catch (final SQLException e){
            throw new RuntimeException();
        }



        return varianti;
    }


    public List<Variante> doRetrieveVariantByCriteria(final String idProdotto,final String attribute,final String value) {
        final List<Variante> varianti = new ArrayList<>();

        try (final Connection connection = ConPool.getConnection()) {
            String sql = "select v.id_variante, v.id_prodotto_variante, v.id_gusto, v.id_confezione, v.prezzo, v.quantità, v.sconto, v.evidenza, g.nomeGusto, c.peso from variante v join gusto g on v.id_gusto = g.id_gusto join confezione c on v.id_confezione = c.id_confezione where v.id_prodotto_variante = ?";

            switch (attribute) {
                case "flavour" -> sql += " and g.nomeGusto = ?";
                case "weight" -> sql += " and c.peso = ?";
            }

            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, idProdotto);

            if (attribute.equals("flavour")) {
                preparedStatement.setString(2, value);
            } else if (attribute.equals("weight")) {
                preparedStatement.setInt(2, Integer.parseInt(value));
            }

            final ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                final Variante variante = new Variante();
                variante.setIdVariante(resultSet.getInt("id_variante"));
                variante.setIdProdotto(resultSet.getString("id_prodotto_variante"));
                variante.setIdGusto(resultSet.getInt("id_gusto"));
                variante.setIdConfezione(resultSet.getInt("id_confezione"));
                variante.setQuantita(resultSet.getInt("quantità"));
                variante.setPrezzo(resultSet.getFloat("prezzo"));
                variante.setSconto(resultSet.getInt("sconto"));
                variante.setEvidenza(resultSet.getBoolean("evidenza"));
                variante.setGusto(resultSet.getString("nomeGusto"));
                variante.setPesoConfezione(resultSet.getInt("peso"));

                varianti.add(variante);
            }
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }

        return varianti;
    }



    public List<Variante> doRetrieveVariantByFlavourAndWeight(final String idProdotto,final String flavour,final int weight) {
        final List<Variante> varianti = new ArrayList<>();

        try (final Connection connection = ConPool.getConnection()) {
            final String sql = "select v.id_variante, v.id_prodotto_variante, v.id_gusto, v.id_confezione, v.prezzo, v.quantità, v.sconto, v.evidenza, g.nomeGusto, c.peso from variante v join gusto g on v.id_gusto = g.id_gusto join confezione c on v.id_confezione = c.id_confezione" +
                    " where id_prodotto_variante = ? and g.nomeGusto = ? and c.peso = ? order by (v.prezzo * (1-v.sconto/100))";


            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, idProdotto);

            preparedStatement.setString(2, flavour);
            preparedStatement.setInt(3, weight);

            final ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                final Variante variante = new Variante();
                variante.setIdVariante(resultSet.getInt("id_variante"));
                variante.setIdProdotto(resultSet.getString("id_prodotto_variante"));
                variante.setIdGusto(resultSet.getInt("id_gusto"));
                variante.setIdConfezione(resultSet.getInt("id_confezione"));
                variante.setQuantita(resultSet.getInt("quantità"));
                variante.setPrezzo(resultSet.getFloat("prezzo"));
                variante.setSconto(resultSet.getInt("sconto"));
                variante.setEvidenza(resultSet.getBoolean("evidenza"));
                variante.setGusto(resultSet.getString("nomeGusto"));
                variante.setPesoConfezione(resultSet.getInt("peso"));

                varianti.add(variante);
            }
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }

        return varianti;
    }



    public Variante doRetrieveVarianteByIdVariante(final int idVariante){
        final Variante variante = new Variante();

        try (final Connection connection = ConPool.getConnection()){
            final String sql = "select v.id_variante, v.id_prodotto_variante, v.id_gusto, v.id_confezione, v.prezzo, v.quantità, v.sconto, v.evidenza, g.nomeGusto, c.peso from variante v join gusto g on v.id_gusto = g.id_gusto join confezione c on v.id_confezione = c.id_confezione where v.id_variante = ?";
            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, idVariante);

            final ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()){
                variante.setIdVariante(resultSet.getInt("id_variante"));
                variante.setIdProdotto(resultSet.getString("id_prodotto_variante"));
                variante.setIdGusto(resultSet.getInt("id_gusto"));
                variante.setIdConfezione(resultSet.getInt("id_confezione"));
                variante.setPrezzo(resultSet.getFloat("prezzo"));
                variante.setSconto(resultSet.getInt("sconto"));
                variante.setEvidenza(resultSet.getBoolean("evidenza"));
                variante.setQuantita(resultSet.getInt("quantità"));


                variante.setGusto(resultSet.getString("nomeGusto"));
                variante.setPesoConfezione(resultSet.getInt("peso"));
            }else {
                return null;
            }

        }catch (final SQLException e){
            throw new RuntimeException(e);
        }


        return variante;
    }


    public Variante doRetrieveCheapestVariant(final String idProdotto){
        final Variante variante = new Variante();


        try (final Connection connection = ConPool.getConnection()){
            final String sql = "select v.*, g.nomeGusto, c.peso from prodotto p join variante v on p.id_prodotto = v.id_prodotto_variante join gusto g on v.id_gusto = g.id_gusto join confezione c on v.id_confezione = c.id_confezione" +
                    " where p.id_prodotto = ? order by (v.prezzo * (1 - v.sconto / 100)) limit 1";
            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, idProdotto);

            final ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                variante.setIdVariante(resultSet.getInt("id_variante"));
                variante.setIdProdotto(resultSet.getString("id_prodotto_variante"));
                variante.setIdGusto(resultSet.getInt("id_gusto"));
                variante.setIdConfezione(resultSet.getInt("id_confezione"));
                variante.setQuantita(resultSet.getInt("quantità"));
                variante.setPrezzo(resultSet.getFloat("prezzo"));
                variante.setSconto(resultSet.getInt("sconto"));
                variante.setEvidenza(resultSet.getBoolean("evidenza"));
                variante.setGusto(resultSet.getString("nomeGusto"));
                variante.setPesoConfezione(resultSet.getInt("peso"));
            }
        }catch (final SQLException e){
            throw new RuntimeException(e);
        }

        return variante;
    }


    public List<Variante> doRetrieveVariantiByProdotti(final List<Prodotto> prodotti) {
        if (prodotti.isEmpty()) {
            return new ArrayList<>();
        }

        final List<Variante> varianti = new ArrayList<>();

        try (final Connection connection = ConPool.getConnection()) {
            final StringBuilder sql = new StringBuilder("SELECT v.*, g.nomeGusto, c.peso FROM variante v ")
                    .append("JOIN gusto g ON v.id_gusto = g.id_gusto ")
                    .append("JOIN confezione c ON v.id_confezione = c.id_confezione ")
                    .append("WHERE v.id_prodotto_variante IN (");
            int prodSize = prodotti.size();
            for (int i = 0; i < prodSize; ++i) {
                sql.append("?");
                if (i < prodSize - 1) {
                    sql.append(", ");
                }
            }
            sql.append(")");

            final PreparedStatement ps = connection.prepareStatement(sql.toString());
            prodSize = prodotti.size();
            for (int i = 0; i < prodSize; ++i) {
                ps.setString(i + 1, prodotti.get(i).getIdProdotto());
            }

            final ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                final Variante variante = new Variante();
                variante.setIdVariante(rs.getInt("id_variante"));
                variante.setIdProdotto(rs.getString("id_prodotto_variante"));
                variante.setIdGusto(rs.getInt("id_gusto"));
                variante.setIdConfezione(rs.getInt("id_confezione"));
                variante.setQuantita(rs.getInt("quantità"));
                variante.setPrezzo(rs.getFloat("prezzo"));
                variante.setSconto(rs.getInt("sconto"));
                variante.setEvidenza(rs.getBoolean("evidenza"));
                variante.setGusto(rs.getString("nomeGusto"));
                variante.setPesoConfezione(rs.getInt("peso"));

                varianti.add(variante);
            }
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }

        return varianti;
    }


    public List<Variante> doRetrieveFilteredVariantiByIdProdotto(final String idProdotto,final String weightFilter,final String tasteFilter) throws SQLException {
        final List<Variante> varianti = new ArrayList<>();

        try (final Connection connection = ConPool.getConnection()) {
            final StringBuilder sql = new StringBuilder();
            sql.append("SELECT v.*, g.nomeGusto, c.peso FROM variante v ");
            sql.append("JOIN gusto g ON v.id_gusto = g.id_gusto ");
            sql.append("JOIN confezione c ON v.id_confezione = c.id_confezione ");
            sql.append("WHERE v.id_prodotto_variante = ? ");

            // Gestione dei filtri
            if (weightFilter != null && !weightFilter.isBlank()) {
                sql.append("AND c.peso = ? ");
            }
            if (tasteFilter != null && !tasteFilter.isBlank()) {
                sql.append("AND g.nomeGusto = ? ");
            }

            final PreparedStatement preparedStatement = connection.prepareStatement(sql.toString());
            preparedStatement.setString(1, idProdotto);

            int paramIndex = 2;
            if (weightFilter != null && !weightFilter.isBlank()) {
                preparedStatement.setInt(paramIndex++, Integer.parseInt(weightFilter.split(" ")[0]));
            }
            if (tasteFilter != null && !tasteFilter.isBlank()) {
                preparedStatement.setString(paramIndex++, tasteFilter.split(" \\(")[0]);
            }

            final ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                final Variante variante = new Variante();
                variante.setIdVariante(resultSet.getInt("id_variante"));
                variante.setIdProdotto(resultSet.getString("id_prodotto_variante"));
                variante.setIdGusto(resultSet.getInt("id_gusto"));
                variante.setIdConfezione(resultSet.getInt("id_confezione"));
                variante.setQuantita(resultSet.getInt("quantità"));
                variante.setPrezzo(resultSet.getFloat("prezzo"));
                variante.setSconto(resultSet.getInt("sconto"));
                variante.setEvidenza(resultSet.getBoolean("evidenza"));
                variante.setGusto(resultSet.getString("nomeGusto"));
                variante.setPesoConfezione(resultSet.getInt("peso"));

                varianti.add(variante);
            }
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }

        return varianti;
    }


    public Variante doRetrieveCheapestFilteredVarianteByIdProdotto(final String idProdotto,final String weightFilter,final String tasteFilter,final boolean evidence) throws SQLException {
        Variante cheapestVariante = null;

        try (final Connection connection = ConPool.getConnection()) {
            final StringBuilder sql = new StringBuilder();
            sql.append("SELECT v.*, g.nomeGusto, c.peso, ");
            sql.append("(v.prezzo * (1 - v.sconto / 100.0)) AS prezzo_scontato ");
            sql.append("FROM variante v ");
            sql.append("JOIN gusto g ON v.id_gusto = g.id_gusto ");
            sql.append("JOIN confezione c ON v.id_confezione = c.id_confezione ");
            sql.append("WHERE v.id_prodotto_variante = ? ");

            if (weightFilter != null && !weightFilter.isBlank()) {
                sql.append("AND c.peso = ? ");
            }
            if (tasteFilter != null && !tasteFilter.isBlank()) {
                sql.append("AND g.nomeGusto = ? ");
            }
            if (evidence)
                sql.append("AND v.evidenza = 1 ");

            sql.append("ORDER BY prezzo_scontato ASC LIMIT 1");

            final PreparedStatement preparedStatement = connection.prepareStatement(sql.toString());
            preparedStatement.setString(1, idProdotto);

            int paramIndex = 2;
            if (weightFilter != null && !weightFilter.isBlank()) {
                preparedStatement.setInt(paramIndex++, Integer.parseInt(weightFilter.split(" ")[0]));
            }
            if (tasteFilter != null && !tasteFilter.isBlank()) {
                preparedStatement.setString(paramIndex++, tasteFilter.split(" \\(")[0]);
            }

            final ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                cheapestVariante = new Variante();
                cheapestVariante.setIdVariante(resultSet.getInt("id_variante"));
                cheapestVariante.setIdProdotto(resultSet.getString("id_prodotto_variante"));
                cheapestVariante.setIdGusto(resultSet.getInt("id_gusto"));
                cheapestVariante.setIdConfezione(resultSet.getInt("id_confezione"));
                cheapestVariante.setQuantita(resultSet.getInt("quantità"));
                cheapestVariante.setPrezzo(resultSet.getFloat("prezzo"));
                cheapestVariante.setSconto(resultSet.getInt("sconto"));
                cheapestVariante.setEvidenza(resultSet.getBoolean("evidenza"));
                cheapestVariante.setGusto(resultSet.getString("nomeGusto"));
                cheapestVariante.setPesoConfezione(resultSet.getInt("peso"));
            }
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }

        return cheapestVariante;
    }


    public void updateVariante(final Variante v,final int idVariante){
        try (final Connection connection = ConPool.getConnection()){
            final PreparedStatement preparedStatement = connection.prepareStatement("update variante set id_variante = ?, id_prodotto_variante = ?, id_gusto = ?, id_confezione = ?, prezzo = ?, quantità = ?, sconto = ?, evidenza = ? where id_variante = ?");
            preparedStatement.setInt(1, v.getIdVariante());
            preparedStatement.setString(2, v.getIdProdotto());
            preparedStatement.setInt(3, v.getIdGusto());
            preparedStatement.setInt(4, v.getIdConfezione());
            preparedStatement.setFloat(5, v.getPrezzo());
            preparedStatement.setInt(6, v.getQuantita());
            preparedStatement.setInt(7, v.getSconto());
            preparedStatement.setBoolean(8, v.isEvidenza());
            preparedStatement.setInt(9, idVariante);


            final int rows = preparedStatement.executeUpdate();
        }catch (final SQLException e){
            throw new RuntimeException(e);
        }
    }


    public void doRemoveVariante(final int idVariante){
        try (final Connection connection = ConPool.getConnection()){
            final PreparedStatement preparedStatement = connection.prepareStatement("delete from variante where id_variante = ?");
            preparedStatement.setInt(1, idVariante);

            final int rows = preparedStatement.executeUpdate();

        }catch (final SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void doSaveVariante(final Variante v){
        try (final Connection connection = ConPool.getConnection()){
            final PreparedStatement preparedStatement = connection.prepareStatement("insert into variante (id_prodotto_variante, id_gusto, id_confezione, prezzo, quantità, sconto, evidenza) values (?, ?, ?, ?, ?, ?, ?)");
            preparedStatement.setString(1, v.getIdProdotto());
            preparedStatement.setInt(2, v.getIdGusto());
            preparedStatement.setInt(3, v.getIdConfezione());
            preparedStatement.setFloat(4, v.getPrezzo());
            preparedStatement.setInt(5, v.getQuantita());
            preparedStatement.setInt(6, v.getSconto());
            preparedStatement.setBoolean(7, v.isEvidenza());

            preparedStatement.executeUpdate();

        }catch (final SQLException e){
            throw new RuntimeException(e);
        }
    }
}
