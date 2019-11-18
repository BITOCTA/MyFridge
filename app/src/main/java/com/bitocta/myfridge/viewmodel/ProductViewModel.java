package com.bitocta.myfridge.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.bitocta.myfridge.Repository;
import com.bitocta.myfridge.db.AppDatabase;
import com.bitocta.myfridge.db.entity.Product;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class ProductViewModel extends AndroidViewModel {

    public static final String TAG = "DB operation";

    private Repository mRepository;

    private LiveData<List<Product>> mAllProducts;

    public ProductViewModel(Application application) {
        super(application);
        mRepository = Repository.getInstance(AppDatabase.getDatabase(application));
        mAllProducts = mRepository.getAllProducts();
    }


    public void insert(Product product) {
        Completable.fromAction(() -> mRepository.insert(product)).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "Product Insert Successful");
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "Product Insert Error: " + e.getMessage());
            }
        });
    }


    public void delete(Product product) {
        Completable.fromAction(() -> mRepository.delete(product)).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "Product Delete Successful");
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "Product Delete Error: " + e.getMessage());
            }
        });
    }

    public LiveData<List<Product>> getAllProducts() {
        return mAllProducts;
    }


}
