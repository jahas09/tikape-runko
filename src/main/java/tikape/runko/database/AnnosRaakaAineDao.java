/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.runko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import tikape.runko.domain.Annos;
import tikape.runko.domain.AnnosRaakaAine;
import tikape.runko.domain.RaakaAine;

public class AnnosRaakaAineDao implements Dao<AnnosRaakaAine, Integer> {

    private Database database;

    public AnnosRaakaAineDao(Database database) {
        this.database = database;
    }

    @Override
    public AnnosRaakaAine findOne(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM AnnosRaakaAine WHERE id = ?");
        stmt.setObject(1, key);

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }
        Integer annosid = rs.getInt("annosid");
        String maara = rs.getString("maara");
        String ohje = rs.getString("ohje");
        String jarjestys = rs.getString("jarjestys");
        Integer raakaaineid = rs.getInt("raakaaineid");

        AnnosRaakaAine o = new AnnosRaakaAine(annosid, raakaaineid, maara, ohje, jarjestys);

        rs.close();
        stmt.close();
        connection.close();

        return o;
    }

    @Override
    public List<AnnosRaakaAine> findAll() throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM AnnosRaakaAine");

        ResultSet rs = stmt.executeQuery();
        List<AnnosRaakaAine> annokset = new ArrayList<>();
        while (rs.next()) {
            Integer annosid = rs.getInt("annosid");
            String maara = rs.getString("maara");
            String ohje = rs.getString("ohje");
            String jarjestys = rs.getString("jarjestys");
            Integer raakaaineid = rs.getInt("raakaaineid");

            annokset.add(new AnnosRaakaAine(annosid, raakaaineid, maara, ohje, jarjestys));
        }

        rs.close();
        stmt.close();
        connection.close();

        return annokset;
    }
    public List<AnnosRaakaAine> findAll(Integer annosId) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM AnnosRaakaAine WHERE annos_id = ?");
        stmt.setObject(1, annosId);

        ResultSet rs = stmt.executeQuery();
        List<AnnosRaakaAine> annokset = new ArrayList<>();
        while (rs.next()) {
            Integer annosid = rs.getInt("annos_id");
            String maara = rs.getString("maara");
            String ohje = rs.getString("ohje");
            String jarjestys = rs.getString("jarjestys");
            Integer raakaaineid = rs.getInt("raaka_aine_id");

            annokset.add(new AnnosRaakaAine(annosid, raakaaineid, maara, ohje, jarjestys));
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

    public void lisaa(Annos annos, RaakaAine raakaaine, String maara, String ohje, String jarjestys) throws SQLException {
            try (Connection conn = database.getConnection()) {
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO AnnosRaakaAine (annos_id, raaka_aine_id, maara, ohje, jarjestys) VALUES (?,?,?,?,?)");
                stmt.setInt(1, annos.getId());
                stmt.setInt(2, raakaaine.getId());
                stmt.setString(5, jarjestys);
                stmt.setString(3, maara);
                stmt.setString(4, ohje);
                stmt.executeUpdate();
            }
    }
}
