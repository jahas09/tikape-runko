package tikape.runko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import tikape.runko.domain.Annos;
import tikape.runko.domain.RaakaAine;

public class AnnosDao implements Dao<Annos, Integer> {

    private Database database;

    public AnnosDao(Database database) {
        this.database = database;
    }

    @Override
    public Annos findOne(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Annos WHERE id = ?");
        stmt.setObject(1, key);

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        Integer id = rs.getInt("id");
        String nimi = rs.getString("nimi");

        Annos o = new Annos(id, nimi);

        rs.close();
        stmt.close();
        connection.close();

        return o;
    }

    public Annos findOne(String key) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Annos WHERE nimi = ?");
        stmt.setObject(1, key);

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        Integer id = rs.getInt("id");
        String nimi = rs.getString("nimi");

        Annos o = new Annos(id, nimi);

        rs.close();
        stmt.close();
        connection.close();

        return o;
    }
    
    @Override
    public List<Annos> findAll() throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Annos");

        ResultSet rs = stmt.executeQuery();
        List<Annos> annokset = new ArrayList<>();
        while (rs.next()) {
            Integer id = rs.getInt("id");
            String nimi = rs.getString("nimi");

            annokset.add(new Annos(id, nimi));
        }

        rs.close();
        stmt.close();
        connection.close();

        return annokset;
    }

    @Override
    public void delete(Integer key) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void lisaaAnnos(String nimi) throws SQLException {
        Annos lisattava = onkoTallennettu(nimi);

        if (lisattava == null) {

            try (Connection conn = database.getConnection()) {
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO Annos (nimi) VALUES (?)");
                stmt.setString(1, nimi);
                stmt.executeUpdate();
            }
        }

    }

    private Annos onkoTallennettu(String nimi) throws SQLException {
        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT id, nimi FROM RaakaAine WHERE nimi = ?");
            stmt.setString(1, nimi);

            ResultSet result = stmt.executeQuery();
            if (!result.next()) {
                return null;
            }
            conn.close();
            return new Annos(result.getInt("id"), result.getString("nimi"));
        }
    }


}
