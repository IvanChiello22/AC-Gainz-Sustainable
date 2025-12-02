package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class DettaglioOrdineDAO {
    public List<DettaglioOrdine> doRetrieveById(final int id) {
        final List<DettaglioOrdine> dettaglioOrdini = new ArrayList<>();
        try(final Connection con= ConPool.getConnection())
        {
            final PreparedStatement preparedStatement=con.prepareStatement("SELECT dettaglio_ordine.*, p.nome, p.immagine, g.nomeGusto, c.peso FROM dettaglio_ordine join prodotto p on dettaglio_ordine.id_prodotto = p.id_prodotto join variante v on dettaglio_ordine.id_variante = v.id_variante join confezione c on v.id_confezione = c.id_confezione join gusto g on v.id_gusto = g.id_gusto " +
                    "WHERE id_ordine = ?");
            preparedStatement.setInt(1,id);
            final ResultSet resultSet=preparedStatement.executeQuery();
           while (resultSet.next()){
               final DettaglioOrdine dettaglioOrdine = new DettaglioOrdine();
               dettaglioOrdine.setIdOrdine(resultSet.getInt("id_ordine"));
               dettaglioOrdine.setIdProdotto(resultSet.getString("id_prodotto"));
               dettaglioOrdine.setIdVariante(resultSet.getInt("id_variante"));
               dettaglioOrdine.setQuantita(resultSet.getInt("quantità"));
               dettaglioOrdine.setPrezzo(resultSet.getFloat("prezzo"));
               dettaglioOrdine.setGusto(resultSet.getString("nomeGusto"));
               dettaglioOrdine.setPesoConfezione(resultSet.getInt("peso"));
               dettaglioOrdine.setNomeProdotto(resultSet.getString("nome"));
               dettaglioOrdine.setImmagineProdotto(resultSet.getString("immagine"));

               dettaglioOrdini.add(dettaglioOrdine);
           }
        }
        catch (final SQLException sqlException)
        {
            throw new RuntimeException(sqlException);
        }

        return dettaglioOrdini;
    }

    public DettaglioOrdine doRetrieveByIdOrderAndIdVariant(final int idOrder,final int idVariante){
        System.out.println(idOrder + "DAO");
        System.out.println(idVariante + "DAO");
        final DettaglioOrdine dettaglioOrdine = new DettaglioOrdine();

        try (final Connection connection = ConPool.getConnection()){
            final PreparedStatement preparedStatement = connection.prepareStatement("select d.id_ordine, d.id_prodotto, d.id_variante, d.quantità, d.prezzo from dettaglio_ordine d where id_ordine = ? and id_variante = ?");
            preparedStatement.setInt(1, idOrder);
            preparedStatement.setInt(2, idVariante);

            final ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                dettaglioOrdine.setIdOrdine(resultSet.getInt("id_ordine"));
                dettaglioOrdine.setIdProdotto(resultSet.getString("id_prodotto"));
                dettaglioOrdine.setIdVariante(resultSet.getInt("id_variante"));
                dettaglioOrdine.setQuantita(resultSet.getInt("quantità"));
                dettaglioOrdine.setPrezzo(resultSet.getFloat("prezzo"));
            }else {
                return null;
            }
        }catch (final SQLException e){
            throw new RuntimeException(e);
        }


        return dettaglioOrdine;
    }


    public List<DettaglioOrdine> doRetrieveAll(){
        final List<DettaglioOrdine> dettaglioOrdini = new ArrayList<>();
        try (final Connection connection = ConPool.getConnection()){
            final PreparedStatement preparedStatement = connection.prepareStatement("select d.id_ordine, d.id_prodotto, d.id_variante, d.quantità, d.prezzo from dettaglio_ordine d");
            final ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                final DettaglioOrdine dettaglioOrdine = new DettaglioOrdine();
                dettaglioOrdine.setIdOrdine(resultSet.getInt("id_ordine"));
                dettaglioOrdine.setIdProdotto(resultSet.getString("id_prodotto"));
                dettaglioOrdine.setIdVariante(resultSet.getInt("id_variante"));
                dettaglioOrdine.setQuantita(resultSet.getInt("quantità"));
                dettaglioOrdine.setPrezzo(resultSet.getFloat("prezzo"));
                dettaglioOrdini.add(dettaglioOrdine);
            }

        }catch (final SQLException e){
            throw new RuntimeException(e);
        }


        return dettaglioOrdini;
    }

    public void doUpdateDettaglioOrdine(final DettaglioOrdine d,final int idOrdine,final String idProdotto,final int idVariante){
        try (final Connection connection = ConPool.getConnection()){
            final PreparedStatement preparedStatement = connection.prepareStatement("update dettaglio_ordine set id_ordine = ?, id_variante = ?, id_prodotto = ?, quantità = ?, prezzo = ? where id_ordine = ? and id_prodotto = ? and id_variante = ?");
            preparedStatement.setInt(1, d.getIdOrdine());
            preparedStatement.setInt(2, d.getIdVariante());
            preparedStatement.setString(3, d.getIdProdotto());
            preparedStatement.setInt(4, d.getQuantita());
            preparedStatement.setFloat(5, d.getPrezzo());

            preparedStatement.setInt(6, idOrdine);
            preparedStatement.setString(7, idProdotto);
            preparedStatement.setInt(8, idVariante);


            final int rows = preparedStatement.executeUpdate();
            System.out.println(rows + "updatedRows");


        }catch (final SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void doRemoveDettaglioOrdine(final int idOrdine,final int idVariante){
        try (final Connection connection = ConPool.getConnection()){
            final PreparedStatement preparedStatement = connection.prepareStatement("delete from dettaglio_ordine where id_ordine = ? and id_variante = ?");
            preparedStatement.setInt(1, idOrdine);
            preparedStatement.setInt(2, idVariante);



            final int rows = preparedStatement.executeUpdate();
            System.out.println(rows + "deletedRows");


        }catch (final SQLException e){
            throw new RuntimeException(e);
        }
    }


    public void doSave(final DettaglioOrdine dettaglioOrdine) {
        final StringBuilder stringBuilder = new StringBuilder("INSERT INTO dettaglio_ordine (id_ordine, id_prodotto, id_variante");
        final List<Object> parameters = new ArrayList<>();

        parameters.add(dettaglioOrdine.getIdOrdine());
        parameters.add(dettaglioOrdine.getIdProdotto());
        parameters.add(dettaglioOrdine.getIdVariante());

        if (dettaglioOrdine.getQuantita() > 0){
            stringBuilder.append(", quantità");
            parameters.add(dettaglioOrdine.getQuantita());
        }

        if (dettaglioOrdine.getPrezzo() != 0.0 && dettaglioOrdine.getPrezzo() > 0){
            stringBuilder.append(", prezzo");
            parameters.add(dettaglioOrdine.getPrezzo());
        }
        stringBuilder.append(") VALUES (?");
        int paramSize = parameters.size();
        for(int i = 1; i < paramSize; ++i) {
            stringBuilder.append(", ?");
        }
        stringBuilder.append(")");


        try (final Connection con = ConPool.getConnection()) {
            final PreparedStatement ps = con.prepareStatement(stringBuilder.toString(), Statement.RETURN_GENERATED_KEYS);
            paramSize = parameters.size();
            for (int i = 0; i < paramSize; ++i) {
                ps.setObject(i + 1, parameters.get(i));
            }

            if (ps.executeUpdate() != 1) {
                throw new RuntimeException("INSERT error.");
            }

        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
