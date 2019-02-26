package com.koenhabets.school.api.som;

public class AbsenceItem {

    private String omschrijving;
    private int beginLesuur;
    private int eindLesuur;
    private boolean geoorloofd;
    private String opmerkingen;
    private String eigenaar;
    private String beginDatumTijd;

    public AbsenceItem(String omschrijving, int beginLesuur, int eindLesuur, boolean geoorloofd, String opmerkingen, String eigenaar, String beginDatumTijd) {
        this.omschrijving = omschrijving;
        this.beginLesuur = beginLesuur;
        this.eindLesuur = eindLesuur;
        this.geoorloofd = geoorloofd;
        this.opmerkingen = opmerkingen;
        this.eigenaar = eigenaar;
        this.beginDatumTijd = beginDatumTijd;
    }

    public String getOmschrijving() {
        return omschrijving;
    }

    public int getBeginLesuur() {
        return beginLesuur;
    }

    public int getEindLesuur() {
        return eindLesuur;
    }

    public boolean isGeoorloofd() {
        return geoorloofd;
    }

    public String getOpmerkingen() {
        return opmerkingen;
    }

    public String getEigenaar() {
        return eigenaar;
    }

    public String getBeginDatumTijd() {
        return beginDatumTijd;
    }
}
