package com.bitocta.myfridge.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.bitocta.myfridge.R;
import com.bitocta.myfridge.db.entity.Product;
import com.bitocta.myfridge.util.TitlePhotoMaker;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.text.format.DateFormat.getDateFormat;
import static com.bitocta.myfridge.util.TitlePhotoMaker.getImageUri;


public class NewProductDialog extends DialogFragment implements EasyPermissions.PermissionCallbacks, DatePickerDialog.OnDateSetListener {

    public static final String TAG = "new_product_dialog";
    private static final String DATE_PICKER_DIALOG_TAG = "Datepickerdialog";


    private static final int PERMISSIONS_REQUEST_CODE = 123;
    private static final int TAKE_PHOTO_REQUEST_CODE = 1;
    private static final int CHOOSE_FROM_GALLERY_REQUEST_CODE = 2;

    private static final int MINIMAL_TITLE_LENGTH = 2;

    private Toolbar toolbar;

    private EditText editTitle;
    private EditText editQuantity;
    private EditText editExpireDate;
    private ImageView choosePhoto;
    private String image_path;
    private DateFormat dateFormat;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogTheme);
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
            dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.new_product_dialog, container, false);
        toolbar = view.findViewById(R.id.toolbar);
        editTitle = view.findViewById(R.id.edit_title);
        editExpireDate = view.findViewById(R.id.edit_expire_date);
        editQuantity = view.findViewById(R.id.edit_quantity);
        choosePhoto = view.findViewById(R.id.choose_photo);
        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar.setNavigationOnClickListener(v -> dismiss());
        toolbar.setTitle(R.string.new_product_title);
        toolbar.inflateMenu(R.menu.menu_dialog);
        toolbar.setOnMenuItemClickListener(this::onOptionsItemSelected);


        dateFormat = getDateFormat(getContext());


        choosePhoto.setOnClickListener(view1 -> selectImage());

        editExpireDate.setOnClickListener(mView -> {

            Calendar now = Calendar.getInstance();
            DatePickerDialog dpd = DatePickerDialog.newInstance(
                    NewProductDialog.this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)

            );

            dpd.setAccentColor(getResources().getColor(R.color.colorPrimaryDark));

            dpd.show(getFragmentManager(), DATE_PICKER_DIALOG_TAG);
        });
    }


    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_save) {
            String trimmedTitle = editTitle.getText().toString().trim();
            String trimmedQuantity = editQuantity.getText().toString().trim();

            if (trimmedTitle.isEmpty()) {
                Toast.makeText(getContext(), R.string.no_title_entered, Toast.LENGTH_LONG).show();
            }  if (trimmedTitle.length() < MINIMAL_TITLE_LENGTH) {
                Toast.makeText(getContext(), R.string.product_title_min_length, Toast.LENGTH_LONG).show();
            }  if (trimmedQuantity.length() == 0 ) {
                Toast.makeText(getContext(), R.string.no_quantity_entered, Toast.LENGTH_LONG).show();
            } else {

                Intent replyIntent = new Intent();

                Date date = null;

                if (image_path == null) {
                    image_path = TitlePhotoMaker.createImage(trimmedTitle, getResources(), getContext());
                }

                if (!editExpireDate.getText().toString().isEmpty()) {
                    try {
                        date = dateFormat.parse(editExpireDate.getText().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                replyIntent.putExtra(ProductsListFragment.PRODUCT_TAG, new Product(trimmedTitle, date, editQuantity.getText().toString(), image_path));

                getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_OK, replyIntent);

                dismiss();
            }
        }

        return true;
    }


    @AfterPermissionGranted(PERMISSIONS_REQUEST_CODE)
    private void selectImage() {

        String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (EasyPermissions.hasPermissions(getContext(), permissions)) {

            final CharSequence[] options = {getResources().getString(R.string.take_photo_option), getResources().getString(R.string.gallery_option)};

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(getResources().getString(R.string.select_image_title));

            builder.setItems(options, (dialog, item) -> {

                if (options[item].equals(getResources().getString(R.string.take_photo_option))) {
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, TAKE_PHOTO_REQUEST_CODE);

                } else if (options[item].equals(getResources().getString(R.string.gallery_option))) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, CHOOSE_FROM_GALLERY_REQUEST_CODE);

                }
            });
            builder.show();
        } else {
            EasyPermissions.requestPermissions(this, getResources().getString(R.string.permission_grant_msg), PERMISSIONS_REQUEST_CODE, permissions);
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case TAKE_PHOTO_REQUEST_CODE:
                    if (resultCode == RESULT_OK && data != null) {

                        Bitmap photo = (Bitmap) data.getExtras().get("data");

                        Glide.with(getContext()).load(photo).apply(RequestOptions.circleCropTransform()).into(choosePhoto);

                        image_path = TitlePhotoMaker.getRealPathFromURI(getImageUri(getContext(), photo), getContext());

                    }
                    break;
                case CHOOSE_FROM_GALLERY_REQUEST_CODE:
                    if (resultCode == RESULT_OK && data != null) {
                        Glide.with(getContext()).load(data.getData()).apply(RequestOptions.circleCropTransform()).into(choosePhoto);
                        image_path = data.getDataString();
                    }

                    break;

            }
        }
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();

        }

    }

    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = dateFormat.format(new GregorianCalendar(year, monthOfYear, dayOfMonth).getTime());
        editExpireDate.setText(date);
    }

}