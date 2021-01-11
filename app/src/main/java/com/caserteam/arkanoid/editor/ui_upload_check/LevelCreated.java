package com.caserteam.arkanoid.editor.ui_upload_check;

public class LevelCreated {
    private String nomeLivello;
    private String struttura;

    public LevelCreated(){
    }
    public LevelCreated(String nameLevel, String structure){
        this.nomeLivello = nameLevel;
        this.struttura = structure;
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
                '}';
    }

    public void setStruttura(String structure) {
        this.struttura = structure;
    }
}
