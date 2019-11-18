package com.bitocta.myfridge.db;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.bitocta.myfridge.db.dao.ProductDao;
import com.bitocta.myfridge.db.entity.Product;

@Database(entities = {Product.class}, version = 1)

public abstract class AppDatabase extends RoomDatabase {

    public abstract ProductDao productDao();


    private static volatile AppDatabase instance;

    public static final String DATABASE_NAME = "products-db";

    public static AppDatabase getDatabase(final Context context) {

        instance = Room.databaseBuilder(context.getApplicationContext(),
                AppDatabase.class, DATABASE_NAME).fallbackToDestructiveMigration().addCallback(sRoomDatabaseCallback)
                .build();

        return instance;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);

        }
    };

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final ProductDao mDao;

        PopulateDbAsync(AppDatabase db) {
            mDao = db.productDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {

            return null;
        }
    }
}
