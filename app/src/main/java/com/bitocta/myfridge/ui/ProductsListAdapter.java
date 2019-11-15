package com.bitocta.myfridge.ui;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bitocta.myfridge.R;
import com.bitocta.myfridge.db.entity.Product;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ProductsListAdapter extends RecyclerView.Adapter<ProductsListAdapter.ViewHolder> {

    private View.OnClickListener mOnItemClickListener;
    private View.OnLongClickListener mOnLongItemClickListener;
    private ArrayList<Product> productsList = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_list_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productsList.get(position);

        TextView title = holder.productTitle;
        TextView expireDate = holder.productExpireDate;
        TextView itemsLeft = holder.productItemsLeft;
        ImageView image = holder.productImage;

        String productTitle = product.getTitle();
        Date productExpireDate = product.getExpireDate();
        String productItemsLeft = product.getItemsLeft();
        String productImagePath = product.getImagePath();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");


        title.setText(productTitle);


        if (!productItemsLeft.isEmpty()) {
            itemsLeft.setText(productItemsLeft);
        }


        if (productExpireDate != null) {
            expireDate.setText(simpleDateFormat.format(product.getExpireDate()));

        } else {
            expireDate.setText(R.string.no_expire_date);
        }


        if (!productImagePath.isEmpty()) {
            Glide.with(ProductsListFragment.context)
                    .load(productImagePath)
                    .apply(RequestOptions.circleCropTransform())
                    .into(image);
        }
        else{
            Glide.with(ProductsListFragment.context)
                    .load(ProductsListFragment.context.getResources().getDrawable(R.drawable.default_product_pic))
                    .apply(RequestOptions.circleCropTransform())
                    .into(image);

        }


    }

    public void setOnItemClickListener(View.OnClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setOnLongItemClickListener(View.OnLongClickListener mOnLongItemClickListener) {
        this.mOnLongItemClickListener = mOnLongItemClickListener;
    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    public void updateEmployeeListItems(List<Product> clients) {
        final ProductDiffCallback diffCallback = new ProductDiffCallback(this.productsList, clients);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        this.productsList.clear();
        this.productsList.addAll(clients);
        diffResult.dispatchUpdatesTo(this);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView productTitle;
        public TextView productExpireDate;
        public TextView productItemsLeft;
        public ImageView productImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            productTitle = itemView.findViewById(R.id.product_title);
            productExpireDate = itemView.findViewById(R.id.product_expire_date);
            productItemsLeft = itemView.findViewById(R.id.product_items_left);
            productImage = itemView.findViewById(R.id.product_image);

            itemView.setTag(this);
            itemView.setOnClickListener(mOnItemClickListener);
            itemView.setOnLongClickListener(mOnLongItemClickListener);

        }
    }
}
