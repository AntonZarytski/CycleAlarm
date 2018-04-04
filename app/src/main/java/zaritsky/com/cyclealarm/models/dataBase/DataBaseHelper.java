package zaritsky.com.cyclealarm.models.dataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Class for create and update the DB
 */
public class DataBaseHelper extends SQLiteOpenHelper {
    // названия столбцов
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_TIME = "wakeup";
    public static final String COLUMN_COLOR = "color";
    public static final String COLUMN_ALARM_POSITION = "alarm_position";
    static final String TABLE_TYPES = "types_of_day"; // название таблицы в бд
    private static final int DATABASE_VERSION = 2; // версия базы данных
    private static final String DATABASE_NAME = "types.db"; // название бд

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + TABLE_TYPES + " (" + COLUMN_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_NAME + " TEXT," + COLUMN_TIME + " TEXT," + COLUMN_COLOR + " INTEGER," + COLUMN_ALARM_POSITION + " INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1 && newVersion == 2) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TYPES);
            onCreate(db);
        }
    }
}
