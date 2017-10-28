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
            String annos = annosDao.findOne(Integer.parseInt(req.params("id"))).getNimi();
            //Teksti lista luodaan jotta saadaan yhdistettyä tiedot raakaaineista ja annosraakaaineista.
            List<String> teksti = new ArrayList<>();
            //Muutetaan annosraakaaineet raakaaineiksi
            for (AnnosRaakaAine r : lista){
                int id = r.getRaakaaineid();
                laita.add(raakaaineDao.findOne(id));               
            }
            
            //Luodaan teksti listalle muuttaja tulostus muodossa.
            for (int i = 0; i < lista.size(); i++) {
             teksti.add(laita.get(i).getNimi() + " "+lista.get(i).getMaara());
            }
            map.put("annos", annos);
            map.put("teksti", lista.get(0));
            map.put("raakaAineet", teksti);
            return new ModelAndView(map, "ohje");
        }, new ThymeleafTemplateEngine());

        ArrayList<String> annoksenNimi = new ArrayList<>();
        ArrayList<String> annosOhje = new ArrayList<>();
        
        Spark.get("/AnnosLuonti", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("annokset", annosDao.findAll());
            return new ModelAndView(map, "AnnosLuonti");
        }, new ThymeleafTemplateEngine());


        Spark.post("/AnnosLuonti", (req, res) -> {
            annosDao.lisaaAnnos(req.queryParams("name"));
            //Otetaan talteen annoksen nimi, en osaa antaa sitä fiksusti html sivulle niin purkka ratkasu
            annoksenNimi.clear();
            annoksenNimi.add(req.queryParams("name"));
            annosOhje.clear();
            annosOhje.add(req.queryParams("ohje"));
            //Ohjataan suoraan raakaaineluonti sivulle lisäämään raakaaineita
            res.redirect("/RaakaAineLuonti");
            return "";
        });

        Spark.get("/RaakaAineLuonti", (req, res) -> {
            HashMap map = new HashMap<>();
            List<String> joLaitettu = new ArrayList<>();
            List<AnnosRaakaAine> annosraakaaineet = new ArrayList<>();
            Integer id = annosDao.findOne(annoksenNimi.get(0)).getId();
            annosraakaaineet = annosraakaaineDao.findAll(id);
            for (int i = 0; i < annosraakaaineet.size(); i++) {
                joLaitettu.add(raakaaineDao.findOne(annosraakaaineet.get(i).getRaakaaineid()).getNimi() +" " +annosraakaaineet.get(i).getMaara());
            }
            map.put("raakaAineet", joLaitettu);
            map.put("raaka", raakaaineDao.findAll());
            //Annetaa raakaineluonti sivulle annoksen nimi
            map.put("annos", annoksenNimi.get(0));
            Annos a = annosDao.findOne(annoksenNimi.get(0));
           // Annetaan lista jossa on vain lisättävän annoksen raakaaineet
            map.put("raakaAine", annosraakaaineDao.findAll(a.getId()));
            return new ModelAndView(map, "RaakaAineLuonti");
        }, new ThymeleafTemplateEngine());

        Spark.post("/RaakaAineLuonti", (Request req, Response res) -> {
            
            raakaaineDao.lisaaRaakaAine(req.queryParams("Raakaaine"));
            String ohje = annosOhje.get(0);
            String maara = req.queryParams("Maara");
            String jarjestys = req.queryParams("Jarjestys");

            //Koska en osaa palauttaa annoksen nimeä html sivulta niin purkkaratkasulla otetaan annoksen nimi muistista ja luodaan siitä annos
            //muuttuja jotta osataan luoda oikea annosraakaaine. 
            annosDao.lisaaAnnos(annoksenNimi.get(0));
            Annos a = annosDao.findOne(annoksenNimi.get(0));
            raakaaineDao.lisaaRaakaAine(req.queryParams("Raakaaine"));
            RaakaAine r = raakaaineDao.findOne(req.queryParams("Raakaaine"));
            annosraakaaineDao.lisaa(a, r, maara, ohje, jarjestys);
            res.redirect("/RaakaAineLuonti");
            return "";
        });
    }
}
