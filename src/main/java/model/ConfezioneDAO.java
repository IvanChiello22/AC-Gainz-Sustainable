package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConfezioneDAO {

    public Confezione doRetrieveById(final int id) {
        try (final Connection connection = ConPool.getConnection()) {
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT c.id_confezione, c.peso from confezione c where c.id_confezione = ?");
            preparedStatement.setInt(1, id);
            final ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                final Confezione confezione = new Confezione();
                confezione.setIdConfezione(resultSet.getInt("id_confezione"));
                confezione.setPeso(resultSet.getInt("peso"));
                return confezione;
            }else{
                return null;
            }

        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Confezione> doRetrieveAll() {
        final List<Confezione> confezioni = new ArrayList<>();
        try (final Connection connection = ConPool.getConnection()) {
            final PreparedStatement preparedStatement = connection.prepareStatement("select c.id_confezione, c.peso from confezione c");
            final ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                final Confezione confezione = new Confezione();
                confezione.setIdConfezione(resultSet.getInt("id_confezione"));
                confezione.setPeso(resultSet.getInt("peso"));
                confezioni.add(confezione);
            }

        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }

        return confezioni;
    }

    public void doSaveConfezione(final Confezione confezione) {
        final StringBuilder sql = new StringBuilder("INSERT INTO confezione(");
        boolean first = true;
        final List<Object> params = new ArrayList<>();

        if (confezione.getIdConfezione() > 0) {
            sql.append("id_confezione");
            params.add(confezione.getIdConfezione());
            first = false;
        }

        if (confezione.getPeso() > 0) {
            if (!first) {
                sql.append(", ");
            }
            sql.append("peso");
            params.add(confezione.getPeso());
        }


        sql.append(") VALUES (");
        int paramSize = params.size();
        for (int i = 0; i < paramSize; ++i) {
            if (i > 0) {
                sql.append(", ");
            }
            sql.append("?");
        }
        sql.append(")");

        try (final Connection connection = ConPool.getConnection()) {
            final PreparedStatement preparedStatement = connection.prepareStatement(sql.toString());

            paramSize = params.size();
            for (int i = 0; i < paramSize; ++i) {
                preparedStatement.setObject(i + 1, params.get(i));
            }

            final int rows = preparedStatement.executeUpdate();

            if (rows > 0) {
                System.out.println("Inserimento riuscito.");
            } else {
                System.out.println("Nessuna riga inserita.");
            }

        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void doUpdateConfezione(final Confezione confezione,final int idConfezione){
        try (final Connection connection = ConPool.getConnection()){
            final PreparedStatement preparedStatement = connection.prepareStatement("update confezione set id_confezione = ?, peso = ? where id_confezione = ?");
            preparedStatement.setInt(1, confezione.getIdConfezione());
            preparedStatement.setInt(2, confezione.getPeso());
            preparedStatement.setInt(3, idConfezione);


            preparedStatement.executeUpdate();

        }catch (final SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void doRemoveConfezione(final int idConfezione){
        try (final Connection connection = ConPool.getConnection()){
            final PreparedStatement preparedStatement = connection.prepareStatement("delete from confezione where id_confezione = ?");
            preparedStatement.setInt(1, idConfezione);


            preparedStatement.executeUpdate();


        }catch (final SQLException e){
            throw new RuntimeException(e);
        }
    }

}

