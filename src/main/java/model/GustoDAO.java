package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GustoDAO {

    public Gusto doRetrieveById(final int id){
        final Gusto gusto = new Gusto();
        try (final Connection connection = ConPool.getConnection()){
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT g.id_gusto, g.nomeGusto from gusto g where g.id_gusto = ?");
            preparedStatement.setInt(1, id );
            final ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                gusto.setIdGusto(resultSet.getInt("id_gusto"));
                gusto.setNome(resultSet.getString("nomeGusto"));
            }

        }catch (final SQLException e){
            throw new RuntimeException(e);
        }
        return gusto;
    }


    public Gusto doRetrieveByIdVariante(final int id) {
        Gusto gusto = null;
        try (final Connection connection = ConPool.getConnection()) {
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT g.id_gusto, g.nomeGusto FROM gusto g JOIN variante ON g.id_gusto = variante.id_gusto WHERE variante.id_variante = ?");
            preparedStatement.setInt(1, id);
            final ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                gusto = new Gusto();
                gusto.setIdGusto(resultSet.getInt("id_gusto"));
                gusto.setNome(resultSet.getString("nomeGusto"));
            }
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
        return gusto;
    }

    public List<Gusto> doRetrieveAll() {
        final List<Gusto> gusti = new ArrayList<>();
        try (final Connection connection = ConPool.getConnection()){
            final PreparedStatement preparedStatement = connection.prepareStatement("select g.id_gusto, g.nomeGusto from gusto g");
            final ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                final Gusto gusto = new Gusto();
                gusto.setIdGusto(resultSet.getInt("id_gusto"));
                gusto.setNome(resultSet.getString("nomeGusto"));
                gusti.add(gusto);
            }

        }catch (final SQLException e){
            throw new RuntimeException(e);
        }

        return gusti;
    }


    public void updateGusto(final Gusto g,final int idGusto){
        try (final Connection connection = ConPool.getConnection()){
            final PreparedStatement preparedStatement = connection.prepareStatement("update gusto set id_gusto = ?, nomeGusto = ? where id_gusto = ?");
            preparedStatement.setInt(1, g.getIdGusto());
            preparedStatement.setString(2, g.getNomeGusto());
            preparedStatement.setInt(3, idGusto);


            final int rows = preparedStatement.executeUpdate();
        }catch (final SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void doSaveGusto(final Gusto g) {
        final StringBuilder stringBuilder = new StringBuilder("INSERT INTO gusto (");
        final List<Object> parameters = new ArrayList<>();

        boolean first = true;

        if (g.getIdGusto() > 0) {
            stringBuilder.append("id_gusto");
            parameters.add(g.getIdGusto());
            first = false;
        }

        if (g.getNomeGusto() != null) {
            if (!first) {
                stringBuilder.append(", ");
            }
            stringBuilder.append("nomeGusto");
            parameters.add(g.getNomeGusto());
            first = false;
        }

        stringBuilder.append(") VALUES (");
        int paramSize = parameters.size();
        for (int i = 0; i < paramSize; ++i) {
            if (i > 0) {
                stringBuilder.append(", ");
            }
            stringBuilder.append("?");
        }
        stringBuilder.append(")");

        final String sql = stringBuilder.toString();

        try (final Connection conn = ConPool.getConnection();
             final PreparedStatement ps = conn.prepareStatement(sql)) {
             paramSize = parameters.size();
             for (int i = 0; i < paramSize; ++i) {
                 ps.setObject(i + 1, parameters.get(i));
             }
             ps.executeUpdate();
         } catch (final SQLException e) {
             e.printStackTrace();
        }

        System.out.println(sql); // Per debug
        System.out.println(parameters); // Per debug
    }

    public void doRemoveGusto(final int idGusto){
        try (final Connection connection = ConPool.getConnection()){
            final PreparedStatement preparedStatement = connection.prepareStatement("delete from gusto where id_gusto = ?");
            preparedStatement.setInt(1, idGusto);

            final int rows = preparedStatement.executeUpdate();


        }catch (final SQLException e){
            throw new RuntimeException(e);
        }
    }







}
