package dev.idm.vkp.db;

import android.database.Cursor;


public interface MapFunction<T> {
    T map(Cursor cursor);
}
