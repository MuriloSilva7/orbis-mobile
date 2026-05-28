package com.orbis.mobile.model;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {ChatSession.class, ChatMessageEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract ChatDao chatDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "orbis_database")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries() // Nota: Para produção, use threads separadas
                    .build();
        }
        return instance;
    }
}
