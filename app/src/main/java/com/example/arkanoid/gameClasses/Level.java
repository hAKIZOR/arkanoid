package com.example.arkanoid.gameClasses;

import java.util.ArrayList;

public class Level{

    private ArrayList<Integer> list;
    private int numberLevel;
    private String nameLevel;

    public Level(ArrayList<Integer> list, int numberLevel, String nameLevel) {
        this.list = new ArrayList<Integer>();
        this.list.addAll(list);
        this.numberLevel = numberLevel;
        this.nameLevel = nameLevel;
    }

    public int getNumberLevel() {
        return numberLevel;
    }

    public String getNameLevel() {
        return nameLevel;
    }

    public int getA(int a) {
        return list.get(a);
    }

}
