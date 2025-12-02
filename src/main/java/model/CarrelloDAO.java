package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class CarrelloDAO {


    public void doSave(final Carrello c){
        try (final Connection con = ConPool.getConnection()){
            final String query = "INSERT INTO carrello (email_utente, id_prodotto, id_variante, quantità, prezzo) " +
                    "VALUES (?, ?, ?, ?, ?)";
            final PreparedStatement preparedStatement = con.prepareStatement(query,Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, c.getEmailUtente());
            preparedStatement.setString(2, c.getIdProdotto());
            preparedStatement.setInt(3, c.getIdVariante());
            preparedStatement.setInt(4, c.getQuantita());
            preparedStatement.setFloat(5, c.getPrezzo());


            preparedStatement.executeUpdate();
        }catch (final SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void doRemoveCartByUser(final String emailUtente){
        try (final Connection connection = ConPool.getConnection()){

            final PreparedStatement preparedStatement = connection.prepareStatement("Delete from carrello where email_utente = ?");
            preparedStatement.setString(1, emailUtente);

            final int rowsDeleted = preparedStatement.executeUpdate();
            if (rowsDeleted > 0)
                System.out.println("Eliminate correttamente: " + rowsDeleted + "righe dal carrello di: " + emailUtente);
            else System.out.println("Nessuna riga eliminata");

        }catch (final SQLException e){
            throw new RuntimeException(e);
        }
    }


    public List<Carrello> doRetrieveCartItemsByUser(final String emailUtente) {
        final List<Carrello> carrelli = new ArrayList<>();

        try (final Connection connection = ConPool.getConnection()) {
            final PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT ca.email_utente, ca.id_prodotto, ca.id_variante, ca.quantità, ca.prezzo, " +
                            "g.nomeGusto, c.peso, p.nome, p.immagine " +
                            "FROM carrello ca " +
                            "join variante v on ca.id_variante = v.id_variante " +
                            "join gusto g on v.id_gusto = g.id_gusto " +
                            "join confezione c on v.id_confezione = c.id_confezione " +
                            "join prodotto p on ca.id_prodotto = p.id_prodotto " +
                            "WHERE ca.email_utente = ?"
            );
            preparedStatement.setString(1, emailUtente);

            final ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                final Carrello carrello = new Carrello();
                carrello.setEmailUtente(resultSet.getString("email_utente"));
                carrello.setIdProdotto(resultSet.getString("id_prodotto"));
                carrello.setIdVariante(resultSet.getInt("id_variante"));
                carrello.setQuantita(resultSet.getInt("quantità"));
                carrello.setPrezzo(resultSet.getFloat("prezzo"));

                carrello.setGusto(resultSet.getString("nomeGusto"));
                carrello.setPesoConfezione(resultSet.getInt("peso"));
                carrello.setNomeProdotto(resultSet.getString("nome"));
                carrello.setImmagineProdotto(resultSet.getString("immagine"));

                carrelli.add(carrello);
            }
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }

        return carrelli;
    }


}
