package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrdineDao {
    public Ordine doRetrieveById(final int id) {
        final Ordine ordine = new Ordine();
        try(final Connection con= ConPool.getConnection())
        {
            final PreparedStatement preparedStatement=con.prepareStatement("SELECT id_ordine, email_utente, data, stato, totale, descrizione FROM ordine WHERE id_ordine=?");
            preparedStatement.setInt(1,id);
            final ResultSet resultSet=preparedStatement.executeQuery();
            if(resultSet.next()){
                ordine.setIdOrdine(resultSet.getInt("id_ordine"));
                ordine.setEmailUtente(resultSet.getString("email_utente"));
                ordine.setDataOrdine(resultSet.getDate("data"));
                ordine.setStato(resultSet.getString("stato"));
                ordine.setTotale(resultSet.getFloat("totale"));
                ordine.setDescrizione(resultSet.getString("descrizione"));
            }else {
                return null;
            }

        }
        catch (final SQLException sqlException)
        {
            throw new RuntimeException(sqlException);
        }

        return ordine;
    }


    public List<Ordine> doRetrieveByEmail(final String email){
        final List<Ordine> ordini = new ArrayList<>();
        try (final Connection connection = ConPool.getConnection()) {
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT id_ordine, email_utente, data, stato, totale, descrizione from ordine where email_utente = ?");
            preparedStatement.setString(1, email);

            final ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                final Ordine ordine = new Ordine();
                ordine.setIdOrdine(resultSet.getInt("id_ordine"));
                ordine.setEmailUtente(resultSet.getString("email_utente"));
                ordine.setDataOrdine(resultSet.getDate("data"));
                ordine.setStato(resultSet.getString("stato"));
                ordine.setTotale(resultSet.getFloat("totale"));
                ordine.setDescrizione(resultSet.getString("descrizione"));
                ordini.add(ordine);
            }



        }catch (final SQLException e){
            throw new RuntimeException(e);
        }

        return ordini;
    }
    public int getLastInsertedId(){
        int id = 0;
        try (final Connection connection = ConPool.getConnection()){
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT LAST_INSERT_ID()");
            final ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                id = resultSet.getInt(1);
            }
        }catch (final SQLException e) {
            throw new RuntimeException(e);
        }

        return id;
    }

    public void doSave(final Ordine ordine) {
        final StringBuilder query = new StringBuilder("INSERT INTO ordine (id_ordine");
        final List<Object> parameters = new ArrayList<>();

        parameters.add(ordine.getIdOrdine());

        if (ordine.getEmailUtente() != null) {
            query.append(", email_utente");
            parameters.add(ordine.getEmailUtente());
        }

        if (ordine.getDataOrdine() != null) {
            query.append(", data");
            final Date utilDate = ordine.getDataOrdine();
            final java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
            parameters.add(sqlDate);
        }

        if (ordine.getStato() != null && !ordine.getStato().isBlank()) {
            query.append(", stato");
            parameters.add(ordine.getStato());
        }

        if (ordine.getTotale() > 0) {
            query.append(", totale");
            parameters.add(ordine.getTotale());
        }

        if (ordine.getDescrizione() != null) {
            query.append(", descrizione");
            parameters.add(ordine.getDescrizione());
        }

        query.append(") VALUES (?");
        int paramSize = parameters.size();
        for (int i = 1; i < paramSize; ++i) {
            query.append(", ?");
        }
        query.append(")");

        try (final Connection con = ConPool.getConnection();
             final PreparedStatement ps = con.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS)) {
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


    public List<Ordine> doRetrieveAll(){

        final ArrayList<Ordine> ordini = new ArrayList<>();
        final Statement st;
        final ResultSet rs;

        Ordine o;

        try (final Connection con = ConPool.getConnection()) {

            st = con.createStatement();

            rs = st.executeQuery("SELECT o.id_ordine, o.email_utente, o.data, o.stato, o.totale, o.descrizione  FROM ordine o");

            while(rs.next()) {

                o = new Ordine();
                o.setIdOrdine(rs.getInt("id_ordine"));
                o.setEmailUtente(rs.getString("email_utente"));
                o.setDataOrdine(rs.getDate("data"));
                o.setStato(rs.getString("stato"));
                o.setTotale(rs.getFloat("totale"));
                o.setDescrizione(rs.getString("descrizione"));
                ordini.add(o);
            }

            con.close();

            return ordini;
        }

        catch (final SQLException e) {

            throw new RuntimeException(e);
        }
    }

    public void doUpdateOrder(final Ordine o,final int idOrdine){

        try (final Connection con = ConPool.getConnection()) {
            final PreparedStatement preparedStatement = con.prepareStatement("update ordine set id_ordine = ?, email_utente = ?, stato = ?, data = ?, totale = ?, descrizione = ? where id_ordine = ?");
            preparedStatement.setInt(1, o.getIdOrdine());
            preparedStatement.setString(2, o.getEmailUtente());
            preparedStatement.setString(3, o.getStato());
            preparedStatement.setDate(4, new java.sql.Date(o.getDataOrdine().getTime()));
            preparedStatement.setFloat(5, o.getTotale());
            preparedStatement.setString(6, o.getDescrizione());
            preparedStatement.setInt(7, idOrdine);

            final int rows = preparedStatement.executeUpdate();
        }
        catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void doDeleteOrder(final int idOrdine){
        try (final Connection connection = ConPool.getConnection()){
            final PreparedStatement preparedStatement = connection.prepareStatement("delete from ordine where id_ordine = ?");
            preparedStatement.setInt(1, idOrdine);

            final int rows = preparedStatement.executeUpdate();

        }catch (final SQLException e){
            throw new RuntimeException(e);
        }
    }
}
