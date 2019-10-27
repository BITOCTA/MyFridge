package com.bitocta.myfridge.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.bitocta.myfridge.db.entity.Product;

import java.util.List;

@Dao
public interface ProductDao {

    @Query("SELECT * FROM product ORDER BY expireDate ASC")
    LiveData<List<Product>> getAll();

    @Delete
    void delete(Product client);

    @Insert
    void insert(Product client);

    @Update
    void update(Product update);
}
