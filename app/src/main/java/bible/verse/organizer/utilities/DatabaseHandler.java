package bible.verse.organizer.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import bible.verse.organizer.objects.Verse;

public class DatabaseHandler extends SQLiteOpenHelper
{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "entries.db";
    private static final String TABLE_ENTRIES = "entries";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_UUID = "uuid";
    private static final String COLUMN_CITATION = "citation";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_TAGS = "tags";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_VERSE_TEXT = "verse_text";
    private static final String COLUMN_ISFAVORITE = "is_Favorite";
    private static final String COLUMN_NOTES = "notes";

    public DatabaseHandler(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        Log.wtf("SQL", "Sumlog ");

        String query = "CREATE TABLE " + TABLE_ENTRIES + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_UUID + " TEXT, " +
                COLUMN_CITATION + " TEXT, " +
                COLUMN_VERSE_TEXT + " TEXT," +
                COLUMN_CATEGORY + " TEXT," +
                COLUMN_TAGS + " TEXT," +
                COLUMN_TITLE + " TEXT," +
                COLUMN_NOTES + " TEXT," +
                COLUMN_ISFAVORITE + " INTEGER" +
                ");";
        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_ENTRIES);
        onCreate(sqLiteDatabase);
    }

    public void addEntry (Verse verse)
    {
        SQLiteDatabase database = getWritableDatabase();
        database.insert(TABLE_ENTRIES, null, getVerseValues(verse));
        database.close();
    }

//    public void deleteEntry (String verseTitle)
//    {
//        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
//        sqLiteDatabase.rawQuery("DELETE FROM " + TABLE_ENTRIES + " WHERE " + COLUMN_TITLE + "=\""  + verseTitle + " \";", null);
//    }

    public Verse getEntry (String id)
    {
        Verse verse = new Verse();
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String query =
                "SELECT *" +
                " FROM " + TABLE_ENTRIES +
                " WHERE " + COLUMN_ID + " LIKE \'%" + id + "%\'";

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        if (cursor.moveToFirst())
            verse = transferSQLtoVerse(cursor);

        sqLiteDatabase.close();
        return verse;
    }

    public void updateEntry (Verse verse)
    {
        getWritableDatabase().update(TABLE_ENTRIES, getVerseValues(verse),
                "uuid=" + verse.getId(), null);
    }

    public List<Verse> search (String criteria)
    {
        List<Verse> verses = new ArrayList<>();

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String query = "SELECT *" +
                " FROM " + TABLE_ENTRIES +
                " WHERE " +
                COLUMN_UUID + " + " +
                COLUMN_CITATION + " + " +
                COLUMN_TITLE + " + " +
                COLUMN_CATEGORY + " + " +
                COLUMN_TAGS + " + " +
                " LIKE \'%" + criteria + "%\'";

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        if (cursor.moveToFirst())
            while (cursor.moveToNext())
                verses.add(transferSQLtoVerse(cursor));

        sqLiteDatabase.close();
        return verses;
    }

    public List<Verse> getAllEntries ()
    {
        List<Verse> verses = new ArrayList<>();

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_ENTRIES;

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        while (cursor.moveToNext())
            verses.add(transferSQLtoVerse(cursor));

        return verses;
    }

    private Verse transferSQLtoVerse (Cursor cursor)
    {
        Verse verse = new Verse();

        verse.setId(cursor.getString(cursor.getColumnIndex(COLUMN_UUID)));
        verse.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
        verse.setCitation(cursor.getString(cursor.getColumnIndex(COLUMN_CITATION)));
        verse.setCategory(cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY)));

        String tagString = cursor.getString(cursor.getColumnIndex(COLUMN_TAGS));
        String[] tags = tagString.split(",");
        verse.setTags(tags);

        verse.setText(cursor.getString(cursor.getColumnIndex(COLUMN_VERSE_TEXT)));
        verse.setFavorited(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(COLUMN_ISFAVORITE))));
        verse.setNotes(cursor.getString(cursor.getColumnIndex(COLUMN_NOTES)));

        return verse;
    }

    private ContentValues getVerseValues(Verse verse)
    {
        ContentValues values = new ContentValues();
        values.put(COLUMN_UUID, verse.getId());
        values.put(COLUMN_TITLE, verse.getTitle());
        values.put(COLUMN_CATEGORY, verse.getCategory());
        values.put(COLUMN_CITATION, verse.getCitation());

        StringBuilder tags = new StringBuilder();
        for(String tag: verse.getTags())
            tags.append(tag).append(",");

        values.put(COLUMN_TAGS, tags. toString());
        values.put(COLUMN_VERSE_TEXT, verse.getText());
        values.put(COLUMN_ISFAVORITE, String.valueOf(verse.isFavorited()));
        values.put(COLUMN_NOTES, verse.getNotes());

        return values;
    }

}
