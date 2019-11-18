package com.bitocta.myfridge.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.bitocta.myfridge.db.converter.DateConverter;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;


@Data
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

}
