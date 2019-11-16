package com.bitocta.myfridge.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.bitocta.myfridge.R;
import com.bitocta.myfridge.db.entity.Product;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;


import static android.text.format.DateFormat.getDateFormat;

import static android.app.Activity.RESULT_OK;


public class ProductChangeDialog extends DialogFragment {



    public final static int REMOVED_PRODUCT = 2;

    public static final String TAG = "product_change_dialog";

    private Toolbar toolbar;
    private int position;

    private Product product;

    private TextView productTitle;
    private TextView productExpireDate;
    private EditText editQuantity;

    private ImageView productImage;
    private TextView buttonDelete;
    private java.text.DateFormat dateFormat;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            position = bundle.getInt("position", 0);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setWindowAnimations(R.style.DialogAnimationSlide);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.product_change_dialog, container, false);
        toolbar = view.findViewById(R.id.product_change_toolbar);
        editQuantity = view.findViewById(R.id.edit_change_quantity);
        productExpireDate = view.findViewById(R.id.product_expire_date_text);
        productTitle = view.findViewById(R.id.product_title_text);

        buttonDelete = view.findViewById(R.id.delete_button);
        productImage = view.findViewById(R.id.product_change_image);

        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        product = ProductsListFragment.mProductViewModel.getAllProducts().getValue().get(position);


        dateFormat = getDateFormat(getContext());

        toolbar.setNavigationOnClickListener(v -> dismiss());
        toolbar.setTitle(R.string.change_product_title);
        toolbar.inflateMenu(R.menu.menu_dialog);
        toolbar.setOnMenuItemClickListener(this::onOptionsItemSelected);

        productTitle.setText(product.getTitle().replaceAll("\\r\\n|\\r|\\n", " "));
        editQuantity.setText(product.getItemsLeft().replaceAll("\\r\\n|\\r|\\n", " "));

        if (product.getExpireDate() != null) {
            productExpireDate.setText(dateFormat.format(product.getExpireDate()));
        } else {
            productExpireDate.setText(getResources().getText(R.string.no_expire_date));
        }


        Glide.with(getContext()).load(product.getImagePath()).apply(RequestOptions.circleCropTransform()).into(productImage);


        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent replyIntent = new Intent();
                replyIntent.putExtra("product", product);
                getTargetFragment().onActivityResult(getTargetRequestCode(), REMOVED_PRODUCT, replyIntent);
                dismiss();
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_save) {

            if (!editQuantity.getText().toString().isEmpty() || product.getItemsLeft().isEmpty()) {

                Intent replyIntent = new Intent();
                product.setItemsLeft(editQuantity.getText().toString());
                replyIntent.putExtra("product", product);
                getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_OK, replyIntent);
                dismiss();
            } else {
                Toast.makeText(getContext(), R.string.no_quantity, Toast.LENGTH_LONG).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }
}