package bible.verse.organizer.objects;

import java.util.UUID;

public class Category
{
    private String id;
    private String name;
    private int verseCount;
    private String iconIdentifier;

    public Category()
    {
        id = UUID.randomUUID().toString();
    }

    public Category(String name, String iconResourceName)
    {
        id = UUID.randomUUID().toString();
        this.name = name;
        this.iconIdentifier = iconResourceName;
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

    public void updateVerseCount()
    {
        verseCount++;
    }

    public String getIconIdentifier()
    {
        return iconIdentifier;
    }

    public void setIconIdentifier(String iconResourceName)
    {
        this.iconIdentifier = iconResourceName;
    }
}
