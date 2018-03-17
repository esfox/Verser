package bible.verse.organizer.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import bible.verse.organizer.objects.Category;
import bible.verse.organizer.objects.Verse;

public class DatabaseHandler extends SQLiteOpenHelper
{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "verserdatabase.db";

    private static final String TABLE_ENTRIES = "entries";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_UUID = "uuid";
    private static final String COLUMN_VERSE = "verse";
    private static final String COLUMN_VERSE_TEXT = "verse_text";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_TAGS = "tags";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_NOTES = "notes";
    private static final String COLUMN_ISFAVORITE = "is_Favorite";

    private static final String TABLE_CATEGORIES = "categories";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_ICON = "icon";

    public DatabaseHandler(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        Log.d("SQL", "Sumlog");

        String createEntryTable = "CREATE TABLE " + TABLE_ENTRIES + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_UUID + " TEXT, " +
                COLUMN_VERSE + " TEXT, " +
                COLUMN_VERSE_TEXT + " TEXT," +
                COLUMN_CATEGORY + " TEXT," +
                COLUMN_TAGS + " TEXT," +
                COLUMN_TITLE + " TEXT," +
                COLUMN_NOTES + " TEXT," +
                COLUMN_ISFAVORITE + " INTEGER" +
                ");";

        String createCategoryTable = "CREATE TABLE " + TABLE_CATEGORIES + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_UUID + " TEXT," +
                COLUMN_NAME + " TEXT," +
                COLUMN_ICON + " INTEGER" +
                ");";

        sqLiteDatabase.execSQL(createEntryTable);
        sqLiteDatabase.execSQL(createCategoryTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_ENTRIES);
        onCreate(sqLiteDatabase);
    }

    public void addEntry(Verse verse)
    {
        getWritableDatabase().insert(TABLE_ENTRIES, null, getVerseValues(verse));
        getWritableDatabase().close();
    }

    public void addCategory (Category category)
    {
        getWritableDatabase().insert(TABLE_CATEGORIES, null, getCategoryValues(category));
        getWritableDatabase().close();
    }

    public void deleteEntry(Verse verse)
    {
        getWritableDatabase().delete(TABLE_ENTRIES,
                COLUMN_UUID + "=?", new String[] {verse.getId()});
        getWritableDatabase().close();
    }

    public void deleteCategory (Category category)
    {
        getWritableDatabase().delete(TABLE_CATEGORIES,
                COLUMN_UUID + "=?", new String[] {category.getId()});
        getWritableDatabase().close();
    }

    public Verse getEntry (String id)
    {
        Verse verse = new Verse();
        String query =
                "SELECT *" +
                " FROM " + TABLE_ENTRIES +
                " WHERE " + COLUMN_UUID + "=\'" + id + "\'";

        Cursor cursor = getWritableDatabase().rawQuery(query, null);

        if (cursor.moveToFirst())
            verse = transferSQLtoVerse(cursor);

        getWritableDatabase().close();
        return verse;
    }

    public Category getCategory (String id)
    {
        Category category = new Category();
        String query =
                "SELECT *" +
                " FROM " + TABLE_ENTRIES +
                " WHERE " + COLUMN_UUID + "=\'" + id + "\'";

        Cursor cursor = getWritableDatabase().rawQuery(query, null);

        if (cursor.moveToFirst())
            category = transferSQLtoCategory(cursor);

        getWritableDatabase().close();
        return category;
    }

    public void updateEntry (Verse verse)
    {
        getWritableDatabase().update(TABLE_ENTRIES, getVerseValues(verse),
                COLUMN_UUID + "=?", new String[] {verse.getId()});
        getWritableDatabase().close();
    }

    public void updateCategory (Category category)
    {
        getWritableDatabase().update(TABLE_CATEGORIES, getCategoryValues(category),
                COLUMN_UUID + "=?", new String[] {category.getId()});
        getWritableDatabase().close();
    }

    public List<Verse> searchEntries (String criteria)
    {
        List<Verse> verses = new ArrayList<>();

        String query = "SELECT *" +
                " FROM " + TABLE_ENTRIES +
                " WHERE " +
                COLUMN_UUID + " + " +
                COLUMN_VERSE + " + " +
                COLUMN_TITLE + " + " +
                COLUMN_CATEGORY + " + " +
                COLUMN_TAGS + " + " +
                " LIKE \'%" + criteria + "%\'";

        Cursor cursor = getWritableDatabase().rawQuery(query, null);

        while(cursor.moveToNext())
            verses.add(transferSQLtoVerse(cursor));

        getWritableDatabase().close();
        return verses;
    }

    public List<Category> searchCategories (String criteria)
    {
        List<Category> categories = new ArrayList<>();

        String query = "SELECT *" +
                " FROM " + TABLE_CATEGORIES +
                " WHERE " +
                COLUMN_UUID + " + " +
                COLUMN_NAME + " LIKE \'%" + criteria + "%\'";

        Cursor cursor = getWritableDatabase().rawQuery(query, null);

        while(cursor.moveToNext())
            categories.add(transferSQLtoCategory(cursor));

        getWritableDatabase().close();
        return categories;
    }

    public List<Verse> getAllEntries()
    {
        List<Verse> verses = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_ENTRIES;

        Cursor cursor = getWritableDatabase().rawQuery(query, null);

        while(cursor.moveToNext())
            verses.add(transferSQLtoVerse(cursor));

        getWritableDatabase().close();
        return verses;
    }

    public List<Category> getAllCategories()
    {
        List<Category> categories = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_CATEGORIES;

        Cursor cursor = getWritableDatabase().rawQuery(query, null);

        while(cursor.moveToNext())
            categories.add(transferSQLtoCategory(cursor));

        getWritableDatabase().close();
        return categories;
    }

    public void clearEntriesTable()
    {
        getWritableDatabase().delete(TABLE_ENTRIES, null, null);
    }

    public void clearCategoriesTable() { getWritableDatabase().delete(TABLE_CATEGORIES, null, null); }

    private Verse transferSQLtoVerse(Cursor cursor)
    {
        Verse verse = new Verse();

        verse.setId(cursor.getString(cursor.getColumnIndex(COLUMN_UUID)));
        verse.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
        verse.setVerse(cursor.getString(cursor.getColumnIndex(COLUMN_VERSE)));

        //TODO: Get category name by id
        verse.setCategoryName(cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY)));
        String tagString = cursor.getString(cursor.getColumnIndex(COLUMN_TAGS));
        String[] tags = tagString.split(",");
        verse.setTags(tags);

        verse.setVerseText(cursor.getString(cursor.getColumnIndex(COLUMN_VERSE_TEXT)));
        verse.setFavorited(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(COLUMN_ISFAVORITE))));
        verse.setNotes(cursor.getString(cursor.getColumnIndex(COLUMN_NOTES)));

        return verse;
    }

    private Category transferSQLtoCategory (Cursor cursor)
    {
        Category category = new Category();

        category.setId(cursor.getString(cursor.getColumnIndex(COLUMN_UUID)));
        category.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
        category.setIconResource(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_ICON))));

        return category;
    }

    private ContentValues getVerseValues(Verse verse)
    {
        ContentValues values = new ContentValues();
        values.put(COLUMN_UUID, verse.getId());
        values.put(COLUMN_TITLE, verse.getTitle());
        values.put(COLUMN_CATEGORY, verse.getCategoryName());
        values.put(COLUMN_VERSE, verse.getVerse());

        StringBuilder tags = new StringBuilder();
        for(String tag: verse.getTags())
            tags.append(tag).append(",");

        values.put(COLUMN_TAGS, tags. toString());
        values.put(COLUMN_VERSE_TEXT, verse.getVerseText());
        values.put(COLUMN_ISFAVORITE, String.valueOf(verse.isFavorite()));
        values.put(COLUMN_NOTES, verse.getNotes());

        return values;
    }

    private ContentValues getCategoryValues(Category category)
    {
        ContentValues values = new ContentValues();
        values.put(COLUMN_UUID, category.getId());
        values.put(COLUMN_NAME, category.getName());
        values.put(COLUMN_ICON, category.getIconResource());

        return values;
    }
}
