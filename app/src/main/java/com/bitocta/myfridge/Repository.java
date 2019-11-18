package com.bitocta.myfridge;

import androidx.lifecycle.LiveData;

import com.bitocta.myfridge.db.AppDatabase;
import com.bitocta.myfridge.db.dao.ProductDao;
import com.bitocta.myfridge.db.entity.Product;

import java.util.List;

public class Repository {

    private static Repository instance;
    private final AppDatabase database;

    private ProductDao mProductDao;
    private LiveData<List<Product>> mAllProducts;

    Repository(final AppDatabase database) {
        this.database = database;
        mProductDao = this.database.productDao();
        mAllProducts = mProductDao.getAll();
    }

    public static Repository getInstance(final AppDatabase database) {
        if (instance == null) {
            synchronized (Repository.class) {
                if (instance == null) {
                    instance = new Repository(database);
                }
            }
        }
        return instance;
    }

    public LiveData<List<Product>> getAllProducts() {
        return mAllProducts;
    }

    public void insert(Product product) {
        mProductDao.insert(product);
    }

    public void delete(Product product) {
        mProductDao.delete(product);
    }
}
