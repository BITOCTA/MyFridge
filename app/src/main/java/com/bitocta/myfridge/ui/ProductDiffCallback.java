package com.bitocta.myfridge.ui;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.bitocta.myfridge.db.entity.Product;

import java.util.List;

public class ProductDiffCallback extends DiffUtil.Callback {

    private final List<Product> mOldProductsList;
    private final List<Product> mNewProductsList;

    public ProductDiffCallback(List<Product> oldProductsList, List<Product> newProductsList) {
        this.mOldProductsList = oldProductsList;
        this.mNewProductsList = newProductsList;
    }

    @Override
    public int getOldListSize() {
        return mOldProductsList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewProductsList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldProductsList.get(oldItemPosition).pid == mNewProductsList.get(
                newItemPosition).pid;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        final Product oldProduct = mOldProductsList.get(oldItemPosition);
        final Product newProduct = mNewProductsList.get(newItemPosition);



        return oldProduct.getTitle().equals(newProduct.getTitle());
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {

        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}

