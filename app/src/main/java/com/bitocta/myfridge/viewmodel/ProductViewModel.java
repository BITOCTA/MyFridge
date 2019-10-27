package com.bitocta.myfridge.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.bitocta.myfridge.Repository;
import com.bitocta.myfridge.db.AppDatabase;
import com.bitocta.myfridge.db.entity.Product;


import java.util.List;


public class ProductViewModel extends AndroidViewModel {

    private Repository mRepository;

    private LiveData<List<Product>> mAllProducts;

    public ProductViewModel (Application application) {
        super(application);
        mRepository = Repository.getInstance(AppDatabase.getDatabase(application));
        mAllProducts = mRepository.getAllProducts();
    }

    public LiveData<List<Product>> getAllProducts() { return mAllProducts; }

    public void delete(Product product) { mRepository.delete(product);}

    public void insert(Product product) { mRepository.insert(product); }

    public void update(Product product) {mRepository.update(product);}
}
