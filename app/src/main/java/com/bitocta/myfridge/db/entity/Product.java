package com.bitocta.myfridge.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.bitocta.myfridge.db.converter.DateConverter;

import java.io.Serializable;
import java.util.Date;

@Entity
public class Product implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public int pid;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "expireDate")
    @TypeConverters(DateConverter.class)
    private Date expireDate;

    @ColumnInfo(name = "itemsLeft")
    private String itemsLeft;

    @ColumnInfo(name = "image")
    private String imagePath;

    public Product(String title, Date expireDate, String itemsLeft, String imagePath) {
        this.title = title;
        this.expireDate = expireDate;
        this.itemsLeft = itemsLeft;
        this.imagePath = imagePath;
    }


    public String getTitle() {
        return title;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public String getItemsLeft() {
        return itemsLeft;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setItemsLeft(String itemsLeft) {
        this.itemsLeft = itemsLeft;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
