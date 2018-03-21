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
import bible.verse.organizer.objects.Tag;
import bible.verse.organizer.objects.Verse;

public class DatabaseHandler extends SQLiteOpenHelper
{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "database.db";

    private static final String ID = "_id";
    private static final String UUID = "uuid";

    private static final String TABLE_VERSES = "verses";
    private static final String VERSES_VERSE = "verse";
    private static final String VERSES_VERSE_TEXT = "verse_text";
    private static final String VERSES_CATEGORY = "category";
    private static final String VERSES_TAGS = "tags";
    private static final String VERSES_TITLE = "title";
    private static final String VERSES_NOTES = "notes";
    private static final String VERSES_ISFAVORITE = "is_Favorite";

    private static final String TABLE_CATEGORIES = "categories";
    private static final String CATEGORIES_NAME = "name";
    private static final String CATEGORIES_ICON = "icon";
    private static final String CATEGORIES_VERSE_COUNT = "verse_count";

    private static final String TABLE_TAGS = "tags";
    private static final String TAGS_NAME = "name";
    private static final String TAGS_COLOR = "color";
    private static final String TAGS_VERSE_COUNT = "verse_count";

    public DatabaseHandler(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        Log.d("SQL", "Sumlog");

        String createEntryTable = "CREATE TABLE " + TABLE_VERSES + "(" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                UUID + " TEXT," +
                VERSES_VERSE + " TEXT," +
                VERSES_VERSE_TEXT + " TEXT," +
                VERSES_CATEGORY + " TEXT," +
                VERSES_TAGS + " TEXT," +
                VERSES_TITLE + " TEXT," +
                VERSES_NOTES + " TEXT," +
                VERSES_ISFAVORITE + " INTEGER" +
                ");";

        String createCategoryTable = "CREATE TABLE " + TABLE_CATEGORIES + "(" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                UUID + " TEXT," +
            CATEGORIES_NAME + " TEXT," +
            CATEGORIES_ICON + " TEXT," +
            CATEGORIES_VERSE_COUNT + " INTEGER" +
                ");";

        String createTagTable = "CREATE TABLE " + TABLE_TAGS + "(" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                UUID + " TEXT," +
                TAGS_NAME + " TEXT," +
                TAGS_COLOR + " INTEGER," +
                TAGS_VERSE_COUNT + " INTEGER" +
                ");";

        sqLiteDatabase.execSQL(createEntryTable);
        sqLiteDatabase.execSQL(createCategoryTable);
        sqLiteDatabase.execSQL(createTagTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_VERSES);
        onCreate(sqLiteDatabase);
    }

    public void addEntry(Verse verse)
    {
        getWritableDatabase().insert
            (TABLE_VERSES, null, getVerseValues(verse));
        getWritableDatabase().close();
    }

    public void addCategory (Category category)
    {
        getWritableDatabase().insert
            (TABLE_CATEGORIES, null, getCategoryValues(category));
        getWritableDatabase().close();
    }

    public void addTag (Tag tag)
    {
        getWritableDatabase().insert
                (TABLE_TAGS, null, getTagValues(tag));
        getWritableDatabase().close();
    }

    public void deleteEntry(Verse verse)
    {
        getWritableDatabase().delete(TABLE_VERSES,
                UUID + "=?", new String[] { verse.getId() });
        getWritableDatabase().close();
    }

    public void deleteCategory (Category category)
    {
        getWritableDatabase().delete(TABLE_CATEGORIES,
                UUID + "=?", new String[] { category.getId() });
        getWritableDatabase().close();
    }

    public void deleteTag (Tag tag)
    {
        getWritableDatabase().delete(TABLE_CATEGORIES,
                UUID + "=?", new String[] { tag.getId() });
        getWritableDatabase().close();
    }

    public Verse getEntry (String id)
    {
        Verse verse = new Verse();
        String query =
                "SELECT *" +
                " FROM " + TABLE_VERSES +
                " WHERE " + UUID + "=\'" + id + "\'";

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
                " FROM " + TABLE_CATEGORIES +
                " WHERE " + UUID + "=\'" + id + "\'";

        Cursor cursor = getWritableDatabase().rawQuery(query, null);

        if(cursor.moveToFirst())
            category = transferSQLtoCategory(cursor);

        getWritableDatabase().close();
        return category;
    }

    public Tag getTag (String id)
    {
        Tag tag = new Tag();
        String query =
                "SELECT *" +
                        " FROM " + TABLE_CATEGORIES +
                        " WHERE " + UUID + "=\'" + id + "\'";

        Cursor cursor = getWritableDatabase().rawQuery(query, null);

        if(cursor.moveToFirst())
            tag = transferSQLtoTag(cursor);

        getWritableDatabase().close();
        return tag;
    }

    public void updateEntry (Verse verse)
    {
        getWritableDatabase().update(TABLE_VERSES, getVerseValues(verse),
                UUID + "=?", new String[] {verse.getId()});
        getWritableDatabase().close();
    }

    public void updateCategory (Category category)
    {
        getWritableDatabase().update(TABLE_CATEGORIES, getCategoryValues(category),
                UUID + "=?", new String[] { category.getId() });
        getWritableDatabase().close();
    }

    public void updateTag (Tag tag)
    {
        getWritableDatabase().update(TABLE_TAGS, getTagValues(tag),
                UUID + "=?", new String[] { tag.getId() });
        getWritableDatabase().close();
    }

    public List<Verse> searchEntries (String criteria)
    {
        List<Verse> verses = new ArrayList<>();

        String query = "SELECT *" +
                " FROM " + TABLE_VERSES +
                " WHERE " +
                UUID + " + " +
                VERSES_VERSE + " + " +
                VERSES_TITLE + " + " +
                VERSES_CATEGORY + " + " +
                VERSES_TAGS + " + " +
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
                UUID + " + " +
            CATEGORIES_NAME + " LIKE \'%" + criteria + "%\'";

        Cursor cursor = getWritableDatabase().rawQuery(query, null);

        while(cursor.moveToNext())
            categories.add(transferSQLtoCategory(cursor));

        getWritableDatabase().close();
        return categories;
    }

    public List<Tag> searchTags (String criteria)
    {
        List<Tag> tags = new ArrayList<>();

        String query = "SELECT *" +
                " FROM " + TABLE_TAGS +
                " WHERE " +
                UUID + " + " +
                TAGS_NAME + " LIKE \'%" + criteria + "%\'";

        Cursor cursor = getWritableDatabase().rawQuery(query, null);

        while(cursor.moveToNext())
            tags.add(transferSQLtoTag(cursor));

        getWritableDatabase().close();
        return tags;
    }

    public List<Verse> getAllEntries()
    {
        List<Verse> verses = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_VERSES;

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

    public List<Tag> getAllTags()
    {
        List<Tag> tags = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_TAGS;

        Cursor cursor = getWritableDatabase().rawQuery(query, null);

        while(cursor.moveToNext())
            tags.add(transferSQLtoTag(cursor));

        getWritableDatabase().close();
        return tags;
    }

    public void clearEntriesTable()
    {
        getWritableDatabase().delete(TABLE_VERSES, null, null);
    }

    public void clearCategoriesTable() { getWritableDatabase().delete(TABLE_CATEGORIES, null, null); }

    public void clearTagsTable() { getWritableDatabase().delete(TABLE_TAGS, null, null); }

    private Verse transferSQLtoVerse(Cursor cursor)
    {
        Verse verse = new Verse();

        verse.setId(cursor.getString(cursor.getColumnIndex(UUID)));
        verse.setVerse(cursor.getString(cursor.getColumnIndex(VERSES_VERSE)));
        verse.setVerseText(cursor.getString(cursor.getColumnIndex(VERSES_VERSE_TEXT)));

        Category category = getCategory(cursor.getString(cursor.getColumnIndex(VERSES_CATEGORY)));
        verse.setCategory(category);

        String tagString = cursor.getString(cursor.getColumnIndex(VERSES_TAGS));
        String[] tags = tagString.split(",");
        verse.setTags(tags);

        verse.setTitle(cursor.getString(cursor.getColumnIndex(VERSES_TITLE)));
        verse.setNotes(cursor.getString(cursor.getColumnIndex(VERSES_NOTES)));
        verse.setFavorited
            (Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(VERSES_ISFAVORITE))));

        return verse;
    }

    private Category transferSQLtoCategory (Cursor cursor)
    {
        Category category = new Category();

        category.setId(cursor.getString(cursor.getColumnIndex(UUID)));
        category.setName(cursor.getString(cursor.getColumnIndex(CATEGORIES_NAME)));
        category.setIconIdentifier(cursor.getString(cursor.getColumnIndex(CATEGORIES_ICON)));
        category.setVerseCount(cursor.getInt(cursor.getColumnIndex(CATEGORIES_VERSE_COUNT)));

        return category;
    }

    private Tag transferSQLtoTag (Cursor cursor)
    {
        Tag tag = new Tag();

        tag.setId(cursor.getString(cursor.getColumnIndex(UUID)));
        tag.setName(cursor.getString(cursor.getColumnIndex(TAGS_NAME)));
        tag.setColor(Integer.parseInt(cursor.getString(cursor.getColumnIndex(TAGS_COLOR))));
        tag.setVerseCount(cursor.getInt(cursor.getColumnIndex(TAGS_VERSE_COUNT)));

        return tag;
    }

    private ContentValues getVerseValues(Verse verse)
    {
        ContentValues values = new ContentValues();
        values.put(UUID, verse.getId());
        values.put(VERSES_VERSE, verse.getVerse());
        values.put(VERSES_VERSE_TEXT, verse.getVerseText());

        if(verse.getCategory() != null)
            values.put(VERSES_CATEGORY, verse.getCategory().getId());

        StringBuilder tags = new StringBuilder();
        for(String tag: verse.getTags())
            tags.append(tag).append(",");

        values.put(VERSES_TAGS, tags. toString());
        values.put(VERSES_TITLE, verse.getTitle());
        values.put(VERSES_NOTES, verse.getNotes());
        values.put(VERSES_ISFAVORITE, String.valueOf(verse.isFavorite()));

        return values;
    }

    private ContentValues getCategoryValues(Category category)
    {
        ContentValues values = new ContentValues();
        values.put(UUID, category.getId());
        values.put(CATEGORIES_NAME, category.getName());
        values.put(CATEGORIES_ICON, category.getIconIdentifier());
        values.put(CATEGORIES_VERSE_COUNT, category.getVerseCount());
        return values;
    }

    private ContentValues getTagValues(Tag tag)
    {
        ContentValues values = new ContentValues();
        values.put(UUID, tag.getId());
        values.put(TAGS_NAME, tag.getName());
        values.put(TAGS_COLOR, tag.getColor());
        values.put(TAGS_VERSE_COUNT, tag.getVerseCount());
        return values;
    }
}
