package com.example.leddemo;

public class listInfo {
    private String id;
    private String typeA;
    private String typeB;
    private String unit;

    public listInfo(String id,String typeA,String typeB,String unit) {
        this.id = id;
        this.typeA = typeA;
        this.typeB = typeB;
        this.unit = unit;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTypeA() {
        return typeA;
    }

    public void setTypeA(String typeA) {
        this.typeA = typeA;
    }

    public String getTypeB() {
        return typeB;
    }

    public void setTypeB(String typeB) {
        this.typeB = typeB;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        return "listInfo {" +
                "id=" + id +
                ", typeA='" + typeA + '\'' +
                ", typeB='" + typeB + '\'' +
                ", unit=" + unit + '}';
    }
}
