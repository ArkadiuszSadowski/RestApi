package api.model;

public class TerritorialDetails {
    private String wojewodztwo;
    private String powiat;
    private String gmina;
    private String jednostkaEwidencyjna;
    private String obrebEwidencyjny;
    private String lat;
    private String lng;

    public TerritorialDetails() {
    }

    public TerritorialDetails(String wojewodztwo, String powiat, String gmina, String miasto, String lat, String lng) {
        this.wojewodztwo = wojewodztwo;
        this.powiat = powiat;
        this.gmina = gmina;
        this.jednostkaEwidencyjna = miasto;
        this.lat = lat;
        this.lng = lng;
    }

    public String getObrebEwidencyjny() {
        return obrebEwidencyjny;
    }

    public void setObrebEwidencyjny(String obrebEwidencyjny) {
        this.obrebEwidencyjny = obrebEwidencyjny;
    }

    public String getWojewodztwo() {
        return wojewodztwo;
    }

    public void setWojewodztwo(String wojewodztwo) {
        this.wojewodztwo = wojewodztwo;
    }

    public String getPowiat() {
        return powiat;
    }

    public void setPowiat(String powiat) {
        this.powiat = powiat;
    }

    public String getGmina() {
        return gmina;
    }

    public void setGmina(String gmina) {
        this.gmina = gmina;
    }

    public String getJednostkaEwidencyjna() {
        return jednostkaEwidencyjna;
    }

    public void setJednostkaEwidencyjna(String miasto) {
        this.jednostkaEwidencyjna = miasto;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }
}
