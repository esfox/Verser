package bible.verse.organizer.objects;

public class Unparser
{
    public Unparser() {}

    private String formattedData = "";

    public void format(String id, String citation, String text, String title, String category, String[] tags, boolean favorited, String notes)
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
    }

    public String formatMockData ()
    {
        String id = "111";
        String citation = "John 3:16";
        String text = "For God so loved the world in this way";
        String title = "The Gospel";
        String category = "Salvation";
        String[] tags = {"God's Love","Jesus","Gospel"};
        boolean favorited = true;
        String notes = "This verse is about Salvation.";

        format(id, citation, text, title, category, tags, favorited, notes);
        return formattedData;
    }
}
