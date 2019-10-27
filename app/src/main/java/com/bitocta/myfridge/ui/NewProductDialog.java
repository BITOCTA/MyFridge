package com.bitocta.myfridge.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


public class NewProductDialog extends DialogFragment implements EasyPermissions.PermissionCallbacks, DatePickerDialog.OnDateSetListener {

    public static final String TAG = "new_product_dialog";

    private Toolbar toolbar;

    private EditText editTitle;
    private EditText editQuantity;
    private EditText editExpireDate;
    private ImageView choosePhoto;
    private String image_path;
    private SimpleDateFormat simpleDateFormat;


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

        simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");


        choosePhoto.setOnClickListener(view1 -> {
            selectImage();
        });

        editExpireDate.setOnClickListener(mView -> {

            Calendar now = Calendar.getInstance();
            DatePickerDialog dpd = DatePickerDialog.newInstance(
                    NewProductDialog.this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)

            );

            dpd.setAccentColor(getResources().getColor(R.color.colorPrimaryDark));

            dpd.show(getFragmentManager(), "Datepickerdialog");
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_save) {
            String trimmedTitle = editTitle.getText().toString().trim();
            String trimmedQuantity = editQuantity.getText().toString().trim();

            if (trimmedTitle.isEmpty()) {
                Toast.makeText(getContext(), R.string.no_title_entered, Toast.LENGTH_LONG).show();
            } else if (trimmedTitle.length() < 2) {
                Toast.makeText(getContext(), R.string.product_title_min_length, Toast.LENGTH_LONG).show();
            } else if (trimmedQuantity.length() == 0 && editQuantity.getText().toString().length() > 0) {
                Toast.makeText(getContext(), R.string.no_quantity_entered, Toast.LENGTH_LONG).show();

            } else {

                Intent replyIntent = new Intent();

                Date date = null;

                if (image_path == null) {
                    image_path = createImage(trimmedTitle);
                }

                if (!editExpireDate.getText().toString().isEmpty()) {
                    try {
                        date = simpleDateFormat.parse(editExpireDate.getText().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                replyIntent.putExtra("product", new Product(trimmedTitle, date, editQuantity.getText().toString(), image_path));

                getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_OK, replyIntent);

                dismiss();
            }
        }

        return super.onOptionsItemSelected(item);
    }


    @AfterPermissionGranted(123)
    private void selectImage() {

        String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (EasyPermissions.hasPermissions(getContext(), permissions)) {

            final CharSequence[] options = {"Take Photo", "Choose from Gallery"};

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Choose product picture");

            builder.setItems(options, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int item) {

                    if (options[item].equals("Take Photo")) {
                        Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(takePicture, 1);

                    } else if (options[item].equals("Choose from Gallery")) {
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto, 0);

                    }
                }
            });
            builder.show();
        } else {
            EasyPermissions.requestPermissions(this, "Grant this permissions to add photos of your product", 123, permissions);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        Glide.with(getContext()).load(data.getData()).apply(RequestOptions.circleCropTransform()).into(choosePhoto);
                        image_path = data.getDataString();
                    }

                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {

                        Bitmap photo = (Bitmap) data.getExtras().get("data");

                        Glide.with(getContext()).load(photo).apply(RequestOptions.circleCropTransform()).into(choosePhoto);

                        image_path = getRealPathFromURI(getImageUri(getContext(), photo));

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

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();

        }

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date;
        if (monthOfYear >= 9)
            date = dayOfMonth + "." + (monthOfYear + 1) + "." + year;
        else
            date = dayOfMonth + ".0" + (monthOfYear + 1) + "." + year;

        editExpireDate.setText(date);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }


    String createImage(String text) {

        int[] colors = getResources().getIntArray(R.array.avatarColor);
        Random rnd = new Random();

        Bitmap bitmap = Bitmap.createBitmap(128, 128, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();


        paint.setColor(colors[rnd.nextInt(5)]);
        canvas.drawRect(0F, 0F, (float) 128, (float) 128, paint);

        int xPos = (canvas.getWidth() / 2);
        int yPos = (canvas.getHeight() / 2) + 10;

        paint.setColor(Color.WHITE);
        paint.setTextSize(40);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));


        canvas.drawText(text.substring(0, 2), xPos, yPos, paint);


        String path = getContext().getCacheDir().getPath() + "/" + System.currentTimeMillis() + text;
        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(new File(path)));

        } catch (FileNotFoundException e) {

            e.printStackTrace();
        }
        return path;
    }
}