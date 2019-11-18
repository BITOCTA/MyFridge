package com.bitocta.myfridge.util;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.provider.MediaStore;

import com.bitocta.myfridge.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Random;

public class TitlePhotoMaker {


    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public static String getRealPathFromURI(Uri uri, Context context) {

        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);

    }

    public static String createImage(String text, Resources resources, Context context) {

        int[] colors = resources.getIntArray(R.array.avatarColor);
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


        String path = context.getCacheDir().getPath() + "/" + System.currentTimeMillis() + text;
        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(new File(path)));

        } catch (FileNotFoundException e) {

            e.printStackTrace();
        }
        return path;
    }
}
