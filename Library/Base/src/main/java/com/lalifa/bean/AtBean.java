package com.lalifa.bean;

public class AtBean {

    private int id;
    private String name;
    private int startPos;
    private int endPos;

    public AtBean(int id, String name, int startPos, int endPos) {
        this.id = id;
        this.name = name;
        this.startPos = startPos;
        this.endPos = endPos;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStartPos() {
        return startPos;
    }

    public void setStartPos(int startPos) {
        this.startPos = startPos;
    }

    public int getEndPos() {
        return endPos;
    }

    public void setEndPos(int endPos) {
        this.endPos = endPos;
    }

    @Override
    public String toString() {
        return "name --> " + name + "  startPos --> " + startPos + "  endPos --> " + endPos;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
