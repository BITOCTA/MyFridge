package com.bitocta.myfridge.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bitocta.myfridge.R;
import com.bitocta.myfridge.db.entity.Product;
import com.bitocta.myfridge.viewmodel.ProductViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

import static android.app.Activity.RESULT_OK;

public class ProductsListFragment extends Fragment {

    private static final int NEW_PRODUCT_REQUEST_CODE = 1;
    private static final int CHANGE_PRODUCT_REQUEST_CODE = 2;

    private RecyclerView recyclerView;
    private ProductsListAdapter productsListAdapter;
    public static ProductViewModel mProductViewModel;
    private FloatingActionButton fab;
    protected static Context context;
    private LinearLayoutManager layoutManager;

    private final CompositeDisposable mDisposable = new CompositeDisposable();

    public static ProductsListFragment getInstance() {
        ProductsListFragment fragment = new ProductsListFragment();
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.product_list_fragment, container, false);

        recyclerView = view.findViewById(R.id.products_list);
        fab = view.findViewById(R.id.fab);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        context = getActivity();
        layoutManager = new LinearLayoutManager(context);

        fab.setOnClickListener(view1 -> openDialog());

        mProductViewModel = ViewModelProviders.of(this).get(ProductViewModel.class);


        mProductViewModel.getAllProducts().observe(this, products -> productsListAdapter.updateEmployeeListItems(products));

        productsListAdapter = new ProductsListAdapter();

        productsListAdapter.setOnItemClickListener(onItemClickListener);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(productsListAdapter);


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    fab.show();
                } else {
                    fab.hide();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }

        });


        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider));
        recyclerView.addItemDecoration(dividerItemDecoration);

    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == ProductChangeDialog.REMOVED_PRODUCT) {
            Product product = (Product) data.getSerializableExtra("product");
            mProductViewModel.delete(product);

            Snackbar snackbar = Snackbar
                    .make(getView(), product.getTitle() + " " + getResources().getString(R.string.removed), Snackbar.LENGTH_LONG).setAction(R.string.undo, view -> mProductViewModel.insert(product));


            snackbar.show();

            recyclerView.requestLayout();
            recyclerView.forceLayout();


        }

        if (resultCode == RESULT_OK) {
            if (requestCode == NEW_PRODUCT_REQUEST_CODE) {


                Product product = (Product) data.getSerializableExtra("product");
                mProductViewModel.insert(product);

                productsListAdapter.notifyDataSetChanged();
                recyclerView.requestLayout();
                recyclerView.forceLayout();


            }
            if (requestCode == CHANGE_PRODUCT_REQUEST_CODE) {
                Product product = (Product) data.getSerializableExtra("product");
                mProductViewModel.insert(product);
                productsListAdapter.notifyDataSetChanged();

                recyclerView.requestLayout();
                recyclerView.forceLayout();

            }
        }


    }

    private View.OnClickListener onItemClickListener = view -> {

        RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
        int position = viewHolder.getAdapterPosition();

        Bundle bundle = new Bundle();

        bundle.putInt("position", position);

        displayProductChangeDialog(getFragmentManager(), bundle);

    };

    private void openDialog() {
        displayNewProductDialog(getFragmentManager());
    }


    public NewProductDialog displayNewProductDialog(FragmentManager fragmentManager) {
        NewProductDialog newProductDialog = new NewProductDialog();
        newProductDialog.setTargetFragment(ProductsListFragment.this, NEW_PRODUCT_REQUEST_CODE);
        newProductDialog.show(fragmentManager, newProductDialog.TAG);
        return newProductDialog;
    }

    public ProductChangeDialog displayProductChangeDialog(FragmentManager fragmentManager, Bundle bundle) {
        ProductChangeDialog productChangeDialog = new ProductChangeDialog();
        productChangeDialog.setTargetFragment(ProductsListFragment.this, CHANGE_PRODUCT_REQUEST_CODE);
        productChangeDialog.setArguments(bundle);
        productChangeDialog.show(fragmentManager, productChangeDialog.TAG);
        return productChangeDialog;
    }

}
