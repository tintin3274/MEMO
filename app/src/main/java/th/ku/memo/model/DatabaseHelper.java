package th.ku.memo.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "MEMO.db";
    public static final int DATABASE_VERSION = 2;


    public DatabaseHelper(@Nullable @org.jetbrains.annotations.Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE1 = "CREATE TABLE 'memo' ('id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 'text' TEXT, 'color_background' TEXT, 'color_text' TEXT, 'alignment' TEXT, 'text_size' INT, 'time_create' TIMESTAMP, 'time_update' TIMESTAMP)" ;
        String CREATE_TABLE2 = "CREATE TABLE 'day' ('id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 'name' TEXT, 'detail' TEXT, 'color_background' TEXT, 'color_text' TEXT, 'calculate' TEXT, 'date' DATE, 'time_create' TIMESTAMP, 'time_update' TIMESTAMP)" ;

        sqLiteDatabase.execSQL(CREATE_TABLE1);
        sqLiteDatabase.execSQL(CREATE_TABLE2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String DROP_TABLE1 = "DROP TABLE IF EXISTS memo";
        String DROP_TABLE2 = "DROP TABLE IF EXISTS day";
        sqLiteDatabase.execSQL(DROP_TABLE1);
        sqLiteDatabase.execSQL(DROP_TABLE2);
        onCreate(sqLiteDatabase);
    }
}
