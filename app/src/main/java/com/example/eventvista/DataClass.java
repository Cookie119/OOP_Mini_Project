package com.example.eventvista;

public class DataClass {
    private String dataTitle;
    private String dataDesc;
    private String dataDate; // Changed from dataLang to dataDate
    private String dataImage;
    private String key;
    private String block;



    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDataTitle() {
        return dataTitle;
    }

    public String getDataDesc() {
        return dataDesc;
    }

    public String getDataDate() { // Changed method name from getDataLang to getDataDate
        return dataDate;
    }

    public String getDataImage() {
        return dataImage;
    }

    // Constructor updated to reflect the change from language to date
    public DataClass(String dataTitle, String dataDesc, String dataDate, String dataImage,String block) {
        this.dataTitle = dataTitle;
        this.dataDesc = dataDesc;
        this.dataDate = dataDate; // Updated variable name
        this.dataImage = dataImage;
        this.block = block;
    }

    public DataClass() {
    }

    public String getBlock() {
        return block;
    }
}
