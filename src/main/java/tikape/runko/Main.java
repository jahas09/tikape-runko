package tikape.runko;

import java.util.HashMap;
import spark.ModelAndView;
import spark.Spark;
import static spark.Spark.*;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import tikape.runko.database.AnnosDao;
import tikape.runko.database.Database;
import tikape.runko.database.RaakaAineDao;

public class Main {

    public static void main(String[] args) throws Exception {
        Database database = new Database("jdbc:sqlite:db/tietokanta.db");
        database.init();

        RaakaAineDao raakaaineDao = new RaakaAineDao(database);
        AnnosDao annosDao = new AnnosDao(database);
        
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
            map.put("raakaAineet", raakaaineDao.findOneWithId(Integer.parseInt(req.params("id"))));
            return new ModelAndView(map, "ohje");
        }, new ThymeleafTemplateEngine());

        Spark.get("/AnnosLuonti", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("annokset", annosDao.findAll());
            return new ModelAndView(map, "AnnosLuonti");
        }, new ThymeleafTemplateEngine());

        Spark.post("/AnnosLuonti", (req, res) -> {
             annosDao.lisaaAnnos(req.queryParams("name"));
            res.redirect("/AnnosLuonti");
            return "";
        });

        
        Spark.get("/RaakaAineLuonti", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("raaka", raakaaineDao.findAll());
            return new ModelAndView(map, "RaakaAineLuonti");
        }, new ThymeleafTemplateEngine());

        Spark.post("/RaakaAineLuonti", (req, res) -> {
             raakaaineDao.lisaaRaakaAine(req.queryParams("name"));
            res.redirect("/RaakaAineLuonti");
            return "";
        });
        
        
//
//        Spark.get("/RaakaAineLuonti", (req, res) -> {
//            return "<form method=\"POST\" action=\"/opiskelijat\">\n"
//                    + "Raaka-aineen nimi:<br/>\n"
//                    + "<input type=\"text\" name=\"nimi\"/><br/>\n"
//                    + "<input type=\"submit\" value=\"Lisää raaka-aine\"/>\n"
//                    + "</form>";
//            
//        });
//
//        Spark.post("/RaakaAineLuonti", (req, res) -> {
//            String nimi = req.queryParams("nimi");
//            raakaaineDao.lisaaRaakaAine(nimi);
//            return nimi;
//        });
    }
}
