package bible.verse.organizer.objects;

import java.util.UUID;

public class Verse
{
    //TODO: Make category field a Category object, and store category ID instead of category name

    private String
        id,
        citation,
        text,
        title,
        category,
        notes;

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

    public String getCitation()
    {
        return citation;
    }

    public void setCitation(String citation)
    {
        this.citation = citation;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
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

    public boolean isFavorited()
    {
        return favorited;
    }

    public void setFavorited(boolean favorited)
    {
        this.favorited = favorited;
    }
}
