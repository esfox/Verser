package bible.verse.organizer.objects;

import java.util.UUID;

public class Category
{
    private String id;
    private String name;
    private int verseCount;
    private int iconResource;

    public Category()
    {
        id = UUID.randomUUID().toString();
    }

    public Category(String name, int iconResource)
    {
        id = UUID.randomUUID().toString();
        this.name = name;
        this.iconResource = iconResource;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getVerseCount()
    {
        return verseCount;
    }

    public void setVerseCount(int verseCount)
    {
        this.verseCount = verseCount;
    }

    public int getIconResource()
    {
        return iconResource;
    }

    public void setIconResource(int iconResource)
    {
        this.iconResource = iconResource;
    }
}
