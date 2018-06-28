package bible.verse.organizer.objects;

import java.util.UUID;

public class Verse
{
    //TODO: Make categoryName field a Category object, and store categoryName ID instead of categoryName name

    /*
        Order of Fields

        - ID
        - Citation
        - Verse Text
        - Category
        - Tag
        - Title
        - Notes
        - Is Favorite
    */

    private String
        id,
        verse,
        verseText,
        categoryID,
        title,
        notes;

    //TODO: Implement category object on verse object
    private Category category;
    private String[] tags;
    private boolean favorited;

    public Verse()
    {
        id = UUID.randomUUID().toString();
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getVerse()
    {
        return verse;
    }

    public void setVerse(String verseCitation)
    {
        verse = verseCitation;
    }

    public String getVerseText()
    {
        return verseText;
    }

    public void setVerseText(String verseText)
    {
        this.verseText = verseText;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public Category getCategory()
    {
        return category;
    }

    public void setCategory(Category category)
    {
        this.category = category;
    }

    public String getNotes()
    {
        return notes;
    }

    public void setNotes(String notes)
    {
        this.notes = notes;
    }

    public String[] getTags()
    {
        return tags;
    }

    public void setTags(String[] tags)
    {
        this.tags = tags;
    }

    public boolean isFavorite()
    {
        return favorited;
    }

    public void setFavorite(boolean favorited)
    {
        this.favorited = favorited;
    }
}
