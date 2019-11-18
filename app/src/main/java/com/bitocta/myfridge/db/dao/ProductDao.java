package com.bitocta.myfridge.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.bitocta.myfridge.db.entity.Product;

import java.util.List;

@Dao
public interface ProductDao {

    @Query("SELECT * FROM product ORDER BY expireDate ASC")
    LiveData<List<Product>> getAll();

    @Delete
    void delete(Product client);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Product client);

}
