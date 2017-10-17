
package tikape.runko.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Annos {
    
    private Integer id;
    private String nimi;
    private List<RaakaAine> raakaAineet;
    private Map<RaakaAine, String> maarat;

    public Annos(Integer id, String nimi) {
        this.id = id;
        this.nimi = nimi;
        this.raakaAineet = new ArrayList<>();
        this.maarat = new HashMap<>();
    }

    public void lisaaRaakaAine(RaakaAine raakaAine, String maara){
        raakaAineet.add(raakaAine);
        maarat.put(raakaAine, maara);
    }
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }


}
