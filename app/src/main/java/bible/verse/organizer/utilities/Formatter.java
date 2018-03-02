package bible.verse.organizer.utilities;

import bible.verse.organizer.objects.Verse;

public class Formatter
{
    public Formatter() {}

    public static String format(Verse verse)
    {
        String formattedData =
                "(" +
                    "#id{" + verse.getId() + "}" +
                    "#cit{" + verse.getVerse() + "}" +
                    "#txt{" + verse.getVerseText() + "}" +
                    "#cat{" + verse.getCategoryName()  + "}" +
                    "#tgs{";

        for (String tag : verse.getTags())
            formattedData += "[" + tag + "]";

        formattedData += "}" +
                    "#t{" + verse.getTitle() + "}" +
                    "#n{" + verse.getNotes() + "}" +
                    "#f{" + String.valueOf(verse.isFavorite()) + "}" +
                 ")$";

        return formattedData;
    }
}