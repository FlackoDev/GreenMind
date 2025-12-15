package com.example.greenmind.data.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.greenmind.data.db.DBHelper;
import com.example.greenmind.model.User;

public class UserDao {

    private final DBHelper dbHelper;

    public UserDao(Context context) {
        dbHelper = new DBHelper(context);
    }

    public long upsert(User u) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", u.getId());
        cv.put("name", u.getName());
        cv.put("email", u.getEmail());
        cv.put("passwordHash", u.getPasswordHash());

        return db.insertWithOnConflict(
                DBHelper.T_USER,
                null,
                cv,
                SQLiteDatabase.CONFLICT_REPLACE
        );
    }

    public User getByEmail(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor c = db.query(
                DBHelper.T_USER,
                null,
                "email=?",
                new String[]{email},
                null, null, null
        );

        if (c.moveToFirst()) {
            User u = new User(
                    c.getInt(c.getColumnIndexOrThrow("id")),
                    c.getString(c.getColumnIndexOrThrow("name")),
                    email,
                    c.getString(c.getColumnIndexOrThrow("passwordHash"))
            );
            c.close();
            return u;
        }
        c.close();
        return null;
    }
}
