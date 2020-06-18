package com.info.rehberprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.HashSet;

public class MyProvider extends ContentProvider {

    static final String PROVIDER_NAME = "com.info.rehberprovider.MyProvider";
    static final String URL = "content://" + PROVIDER_NAME + "/rehber";
    static final Uri CONTENT_URI = Uri.parse(URL);

    private Veritabani veritabani;
    private SQLiteDatabase database;

    static final String TABLE_NAME = "rehber";
    static final String ID = "id";
    static final String AD = "ad";
    static final String TEL = "tel";

    //Veri çekerken Sistem bizden HashMap beklediği için.
    private static HashMap<String, String> RehberMap;

    static final int REHBER = 1;
    static final int REHBER_PARAMETRE = 2;

    static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "rehber", REHBER);
        uriMatcher.addURI(PROVIDER_NAME, "rehber/*", REHBER_PARAMETRE);
        //Buradaki yıldız String,int her şey olabileceği için tüm tipler geçerli demek.

    }

    @Override
    public boolean onCreate() {

        Context context = getContext();
        veritabani = new Veritabani(context);
        database = veritabani.getWritableDatabase();

        if (database == null)
            return false;
        else
            return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        //Veri çekme metodu(query)
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case REHBER:
                queryBuilder.setProjectionMap(RehberMap);
                break;
            case REHBER_PARAMETRE:
                queryBuilder.appendWhere(ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Bilinmeyen URI" + uri);
        }

        Cursor cursor = queryBuilder.query(database, projection, selection, selectionArgs, null, null, ID);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        //veri kaydetme metodu.
        long row = database.insert(TABLE_NAME, "", values);
        if (row > 0) {
            Uri newUri = ContentUris.withAppendedId(CONTENT_URI, row);
            getContext().getContentResolver().notifyChange(newUri, null);
            return newUri;
        }
        throw new SQLException("Yeni Kayıt Oluşturma Hatası" + uri);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        //Kaç tane veri silindiğini göstermek için.
        int count = 0;

        switch (uriMatcher.match(uri)) {
            case REHBER:
                count = database.delete(TABLE_NAME, selection, selectionArgs);
                break;
            case REHBER_PARAMETRE:
                String id = uri.getLastPathSegment();	//id'i alır.
                count = database.delete( TABLE_NAME, ID +  " = " + id, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Bilinmeyen URI" + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        int count = 0;
        switch (uriMatcher.match(uri)) {
            case REHBER:
                count = database.update(TABLE_NAME, values, selection, selectionArgs);
                break;
            case REHBER_PARAMETRE:
                count = database.update(TABLE_NAME, values, ID + "=" + uri.getLastPathSegment(), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Bilinmeyen URI" + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
