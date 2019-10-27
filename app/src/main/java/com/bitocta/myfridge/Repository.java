package com.bitocta.myfridge;

import android.app.Application;
import android.os.AsyncTask;

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
        this.database=database;
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

    public void insert (Product product) {
        new insertAsyncTask(mProductDao).execute(product);
    }
    public void delete (Product product) { new deleteAsyncTask(mProductDao).execute(product);}
    public void update (Product product) {new updateAsyncTask(mProductDao).execute(product);}

    private static class insertAsyncTask extends AsyncTask<Product, Void, Void> {

        private ProductDao mAsyncTaskDao;

        insertAsyncTask(ProductDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Product... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    private static class deleteAsyncTask extends AsyncTask<Product, Void, Void>{
        private ProductDao mAsyncTaskDao;

        deleteAsyncTask(ProductDao dao) {mAsyncTaskDao = dao;}

        @Override
        protected Void doInBackground(final Product... params){
            mAsyncTaskDao.delete(params[0]);
            return null;
        }
    }

    private static class updateAsyncTask extends AsyncTask<Product, Void, Void> {
        private ProductDao mAsyncTaskDao;

        updateAsyncTask(ProductDao dao) { mAsyncTaskDao = dao;}

        @Override
        protected Void doInBackground(final Product... params){
            mAsyncTaskDao.update(params[0]);
            return null;
        }
    }
}
