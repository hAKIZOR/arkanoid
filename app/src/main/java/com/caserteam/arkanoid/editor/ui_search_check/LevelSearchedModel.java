package com.caserteam.arkanoid.editor.ui_search_check;

public class LevelSearchedModel {
    private String nomeLivello;
    private String struttura;
    private String nickname;

    public LevelSearchedModel(){
    }

    public LevelSearchedModel(String nickname, String nomeLivello,String struttura){
        this.nomeLivello = nomeLivello;
        this.struttura = struttura;
        this.nickname = nickname;
    }

    public String getNomeLivello() {
        return nomeLivello;
    }

    public void setNomeLivello(String nomeLivello) {
        this.nomeLivello = nomeLivello;
    }

    public String getStruttura() {
        return struttura;
    }

    public void setStruttura(String structure) {
        this.struttura = structure;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public String toString() {
        return "LevelCreated{" +
                "nomeLivello='" + nomeLivello + '\'' +
                ", struttura='" + struttura + '\'' +
                ", struttura='" + nickname + '\'' +
                '}';
    }
}
