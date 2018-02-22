package bible.verse.organizer.utilities;

public class Formatter
{
    public Formatter() {}

    private String formattedData = "";

    public String format(String id, String citation, String text, String title, String category, String[] tags, boolean favorited, String notes)
    {
        formattedData +=
                "(" +
                    "#id{" + id + "}" +
                    "#cit{" + citation + "}" +
                    "#txt{" + text + "}" +
                    "#t{" + title + "}" +
                    "#cat{" + category  + "}" +
                    "#tgs{";


        for (String tag : tags)
            formattedData += "[" + tag + "]";

        formattedData += "}" +
                    "#f{" + String.valueOf(favorited) + "}" +
                    "#n{" + notes + "}" +
                 ")$";

        return formattedData;
    }
}
