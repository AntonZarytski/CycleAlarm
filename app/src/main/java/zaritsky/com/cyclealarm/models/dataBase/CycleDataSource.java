package zaritsky.com.cyclealarm.models;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

public class CycleDataSource {
    private DataBaseHelper dbHelper;
    private SQLiteDatabase database;

    private String[] notesAllColumn = {
            DataBaseHelper.COLUMN_ID,
            DataBaseHelper.COLUMN_NAME,
            DataBaseHelper.COLUMN_TIME,
            DataBaseHelper.COLUMN_COLOR
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

        database.insert(DataBaseHelper.TABLE_TYPES, null, values);
    }

    public void editNote(long id) {
        ContentValues editedNote = new ContentValues();
        editedNote.put(DataBaseHelper.COLUMN_ID, id);
        editedNote.put(DataBaseHelper.COLUMN_NAME, "Wine dandelion");
        editedNote.put(DataBaseHelper.COLUMN_TIME, "Ray Bradbury");
        editedNote.put(DataBaseHelper.COLUMN_COLOR, "Ray Bradbury");

        database.update(DataBaseHelper.TABLE_TYPES,
                editedNote,
                DataBaseHelper.COLUMN_ID + "=" + id,
                null);
    }

    public void deleteNote(TypeOfDay typeOfDay) {
        String name = typeOfDay.getName();
        database.delete(DataBaseHelper.TABLE_TYPES, DataBaseHelper.COLUMN_NAME
                + " = " + name, null);
    }

    public void deleteAll() {
        database.delete(DataBaseHelper.TABLE_TYPES, null, null);
    }

    public List<TypeOfDay> getAllNotes() {
        List<TypeOfDay> notes = new ArrayList<>();

        Cursor cursor = database.query(DataBaseHelper.TABLE_TYPES,
                notesAllColumn, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            TypeOfDay type = cursorToNote(cursor);
            notes.add(type);
            cursor.moveToNext();
        }
        
        // после закрыть курсор
        cursor.close();
        return notes;
    }

    private TypeOfDay cursorToNote(Cursor cursor) {
        TypeOfDay type = new TypeOfDay();
        //note.setId(cursor.getLong(0));
        type.setName(cursor.getString(1));
        type.setTimeOfWakeUp(cursor.getString(2));
        //cursor.getInt(3)
        type.setColor(new Color());
        return type;
    }

}
