package bible.verse.organizer.utilities;

import android.util.Log;

import com.faithcomesbyhearing.dbt.Dbt;
import com.faithcomesbyhearing.dbt.callback.VerseCallback;
import com.faithcomesbyhearing.dbt.model.Verse;

import java.util.List;

//TODO: Implement
public class BibleUtils
{
    private static
        String[]
        //Old Testament
        oldTestamentBookIDs =
            {
                "Gen", "Exod", "Lev", "Num", "Deut", "Josh", "Judg", "Ruth", "1Sam", "2Sam",
                "1Kgs", "2Kgs", "1Chr", "2Chr", "Ezra", "Neh", "Esth", "Job", "Ps", "Prov",
                "Eccl", "Song", "Isa", "Jer", "Lam", "Ezek", "Dan", "Hos", "Joel", "Amos",
                "Obad", "Jonah", "Mic", "Nah", "Hab", "Zeph", "Hag", "Zech", "Mal",

            },
        //New Testament
        newTestamentBookIDs =
            {
                "Matt", "Mark", "Luke", "John", "Acts", "Rom", "1Cor", "2Cor", "Gal", "Eph",
                "Eph", "Phil", "Col", "1Thess", "2Thess", "1Tim", "2Tim", "Titus", "Phlm", "Heb",
                "Jas", "1Pet", "2Pet", "1John", "2John", "3John", "Jude", "Rev"
            };

    private static String damID;
    private static String response;

    public static String getVerse
        (String book, String chapter, String verses)
    {
        response = "";
        String bookID = getBookID(book);

        String[] verseRanges = verses.split(", ");
        for(String verseRange : verseRanges)
        {
            String[] rangeSplit = verseRange.split("-");
            String start = rangeSplit[0];
            String end = "";
            if(rangeSplit.length > 1)
                end = rangeSplit[1];

            Dbt.getTextVerse(damID, bookID, chapter, start, end, new VerseCallback()
            {
                @Override
                public void success(List<Verse> verses)
                {
                    for(Verse verse : verses)
                        response += verse.getVerseText();

                    Log.d("response", response);
                }

                @Override
                public void failure(Exception e) {}
            });
        }

        return response;
    }
//
//    static void onRequestResponse(String response)
//    {
//        BibleUtils.response += response;
//    }

    public static void testDBT(String book, String chapter, String verses)
    {

    }

    private static String getBookID(String book)
    {
        for(String bookID : oldTestamentBookIDs)
        {
            if(book.contains(bookID))
            {
                damID = "ENGESVO2ET";
                return bookID;
            }
        }

        for(String bookID : newTestamentBookIDs)
        {
            if(book.contains(bookID))
            {
                damID ="ENGESVN2ET";
                return bookID;
            }
        }

        return null;
    }
}
