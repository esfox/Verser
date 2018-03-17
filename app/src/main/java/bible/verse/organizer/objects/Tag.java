package bible.verse.organizer.objects;

import java.util.UUID;

public class Tag
{
    private String id;
    private String name;
    private int color;
    private int verseCount;

    public Tag(){ id = UUID.randomUUID().toString(); }

    public void setId(String id) { this.id = id ;}

    public String getId() {return id;}

    public void setName(String name) { this.name = name; }

    public String getName() { return name; }

    public void setColor(int color) { this.color = color; }

    public int getColor() {return color;}

    public void setVerseCount(int verseCount) { this.verseCount = verseCount; }

    public int getVerseCount() {return verseCount;}
}
