
package tikape.runko.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnnosRaakaAine {
    
    private Integer annosid;
    private List<RaakaAine> raakaAineet;
    private Map<RaakaAine, String> maarat;
    private Integer raakaaineid;
    private String ohje;
    private String jarjestys;
    private String maara;
    
    public AnnosRaakaAine(Integer annosid, Integer raakaaineid, String maara, String ohje, String jarjestys) {
        this.annosid = annosid;
        this.raakaaineid = raakaaineid;
        this.raakaAineet = new ArrayList<>();
        this.maarat = new HashMap<>();
        this.ohje = ohje;
        this.maara = maara;
        this.jarjestys = jarjestys;
        
    }

    public void lisaaRaakaAine(RaakaAine raakaAine, String maara){
        raakaAineet.add(raakaAine);
        maarat.put(raakaAine, maara);
    }
    
    public Integer getAnnosId() {
        return annosid;
    }

    public void setannosId(Integer id) {
        this.annosid = id;
    }
    public Integer getRaakaaineid() {
        return this.raakaaineid;
    }
    public void setRaakaineid(Integer id){
        this.raakaaineid = id;
    }

    public String getOhje(){
        return this.ohje;
    }
    public String getMaara(){
        return this.maara;
    }

    public String getJarjestys(){
        return this.jarjestys;
    }

}
