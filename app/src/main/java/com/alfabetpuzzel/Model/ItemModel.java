package com.alfabetpuzzel.Model;

public class ItemModel {

//    private final String name;
//    private final String type;
private final int image;
    private final int suara;




    public ItemModel( int image,int suara) {

        this.image=image;
        this.suara=suara;
    }

//    public String getName() {
//        return name;
//    }
//
//    public String getType() {
//        return type;
//    }

    public int getImage() {
        return image;
    }
    public int getsuara() {
        return suara;
    }
}
