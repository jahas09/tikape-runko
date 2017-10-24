package tikape.runko;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Spark;
import static spark.Spark.*;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import tikape.runko.database.AnnosDao;
import tikape.runko.database.AnnosRaakaAineDao;
import tikape.runko.database.Database;
import tikape.runko.database.RaakaAineDao;
import tikape.runko.domain.Annos;
import tikape.runko.domain.AnnosRaakaAine;
import tikape.runko.domain.RaakaAine;

public class Main {

    public static void main(String[] args) throws Exception {
        Database database = new Database("jdbc:sqlite:db/tietokanta.db");
        database.init();

        RaakaAineDao raakaaineDao = new RaakaAineDao(database);
        AnnosDao annosDao = new AnnosDao(database);
        AnnosRaakaAineDao annosraakaaineDao = new AnnosRaakaAineDao(database);

        get("/", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("viesti", "tervehdys");
            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());

        get("/annokset", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("annokset", annosDao.findAll());
            return new ModelAndView(map, "annokset");
        }, new ThymeleafTemplateEngine());

        get("/annokset/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            //Haetaan kaikki annoraakaaineet tietylle annokselle
            List<AnnosRaakaAine> lista = annosraakaaineDao.findAll(Integer.parseInt(req.params("id")));
            List<RaakaAine> laita = new ArrayList<>();
            //Muutetaan annosraakaaineet raakaaineiksi
            for (AnnosRaakaAine r : lista){
                int id = r.getRaakaaineid();
                laita.add(raakaaineDao.findOne(id));
            }
            map.put("raakaAineet", laita);
            return new ModelAndView(map, "ohje");
        }, new ThymeleafTemplateEngine());

        ArrayList<String> testi = new ArrayList<>();
        Spark.get("/AnnosLuonti", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("annokset", annosDao.findAll());
            return new ModelAndView(map, "AnnosLuonti");
        }, new ThymeleafTemplateEngine());


        Spark.post("/AnnosLuonti", (req, res) -> {
            annosDao.lisaaAnnos(req.queryParams("name"));
            //Otetaan talteen annoksen nimi, en osaa antaa sitä fiksusti html sivulle niin purkka ratkasu
            testi.add(req.queryParams("name"));
            //Ohjataan suoraan raakaaineluonti sivulle lisäämään raakaaineita
            res.redirect("/RaakaAineLuonti");
            return "";
        });

        Spark.get("/RaakaAineLuonti", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("raaka", raakaaineDao.findAll());
            //Annetaa raakaineluonti sivulle annoksen nimi
            map.put("annos", testi.get(0));
            return new ModelAndView(map, "RaakaAineLuonti");
        }, new ThymeleafTemplateEngine());
        testi.clear();

        Spark.post("/RaakaAineLuonti", (Request req, Response res) -> {
            
            raakaaineDao.lisaaRaakaAine(req.queryParams("Raakaaine"));
            String ohje = req.queryParams("Ohje");
            String maara = req.queryParams("Maara");
            String jarjestys = req.queryParams("Jarjestys");

            //Koska en osaa palauttaa annoksen nimeä html sivulta niin purkkaratkasulla otetaan annoksen nimi muistista ja luodaan siitä annos
            //muuttuja jotta osataan luoda oikea annosraakaaine. 
            annosDao.lisaaAnnos(testi.get(0));
            Annos a = annosDao.findOne(testi.get(0));
            RaakaAine r = raakaaineDao.findOne(req.queryParams("Raakaaine"));
            annosraakaaineDao.lisaa(a, r, maara, ohje, jarjestys);
            res.redirect("/RaakaAineLuonti");
            return "";
        });
    }
}
