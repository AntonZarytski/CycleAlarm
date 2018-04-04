package zaritsky.com.cyclealarm.models.dataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import zaritsky.com.cyclealarm.models.TypeOfDay;

/**
 * class contains methods with prepared statement to DB
 */
public class CycleDataSource {
    private DataBaseHelper dbHelper;
    private SQLiteDatabase database;
    private String[] notesAllColumn = {
            DataBaseHelper.COLUMN_ID,
            DataBaseHelper.COLUMN_NAME,
            DataBaseHelper.COLUMN_TIME,
            DataBaseHelper.COLUMN_COLOR,
            DataBaseHelper.COLUMN_ALARM_POSITION
    };

    public CycleDataSource(Context context) {
        dbHelper = new DataBaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void addType(TypeOfDay type) {
        ContentValues values = new ContentValues();
        values.put(DataBaseHelper.COLUMN_NAME, type.getName());
        values.put(DataBaseHelper.COLUMN_TIME, type.getTimeOfWakeUp());
        values.put(DataBaseHelper.COLUMN_COLOR, type.getColor());
        values.put(DataBaseHelper.COLUMN_ALARM_POSITION, type.getAlarmPosition());

        database.insert(DataBaseHelper.TABLE_TYPES, null, values);
    }

    public void editType(TypeOfDay oldType, TypeOfDay newType) {
        ContentValues editedType = new ContentValues();
        editedType.put(DataBaseHelper.COLUMN_NAME, newType.getName());
        editedType.put(DataBaseHelper.COLUMN_TIME, newType.getTimeOfWakeUp());
        editedType.put(DataBaseHelper.COLUMN_COLOR, newType.getColor());
        editedType.put(DataBaseHelper.COLUMN_ALARM_POSITION, newType.getAlarmPosition());
        database.update(DataBaseHelper.TABLE_TYPES,
                editedType,
                DataBaseHelper.COLUMN_NAME + "=" + oldType.getName(),
                null);
    }

    public void deleteType(TypeOfDay typeOfDay) {
        String name = typeOfDay.getName();
        database.delete(DataBaseHelper.TABLE_TYPES, DataBaseHelper.COLUMN_NAME
                + "=" + name, null);
    }

    public void deleteAll() {
        database.delete(DataBaseHelper.TABLE_TYPES, null, null);
    }

    public List<TypeOfDay> getAllTypes() {
        List<TypeOfDay> types = new ArrayList<>();

        Cursor cursor = database.query(DataBaseHelper.TABLE_TYPES,
                notesAllColumn, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            TypeOfDay type = cursorToType(cursor);
            types.add(type);
            cursor.moveToNext();
        }
        cursor.close();
        return types;
    }

    private TypeOfDay cursorToType(Cursor cursor) {
        TypeOfDay type = new TypeOfDay();
        type.setName(cursor.getString(1));
        type.setTimeOfWakeUp(cursor.getString(2));
        type.setColor(cursor.getInt(3));
        type.setAlarmPosition(cursor.getInt(4));
        return type;
    }

}
