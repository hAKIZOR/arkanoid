package com.caserteam.arkanoid.editor.ui_search_check;

public class LevelSearchedModel {
    private String nomeLivello;
    private String struttura;
    private String autore;

    public LevelSearchedModel(){
    }
    public LevelSearchedModel(String nameLevel, String structure,String autore){
        this.nomeLivello = nameLevel;
        this.struttura = structure;
        this.autore = autore;
    }

    public String getNomeLivello() {
        return nomeLivello;
    }

    public void setNomeLivello(String nameLevel) {
        this.nomeLivello = nameLevel;
    }

    public String getStruttura() {
        return struttura;
    }

    @Override
    public String toString() {
        return "LevelCreated{" +
                "nomeLivello='" + nomeLivello + '\'' +
                ", struttura='" + struttura + '\'' +
                ", struttura='" + autore + '\'' +
                '}';
    }

    public void setStruttura(String structure) {
        this.struttura = structure;
    }
}
