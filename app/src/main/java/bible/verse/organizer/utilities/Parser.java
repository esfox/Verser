package bible.verse.organizer.utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bible.verse.organizer.objects.Verse;

public class Parser
{
    public static List<Verse> parse(String formattedData)
    {
        List<Verse> verses = new ArrayList<>();

        /*
        * THE FOLLOWING CHARACTERS SHOULD NOT BE PRINTED AS IS IN THE TXT FILE:
        *
        * #
        * $
        * { }
        * [ ]
        * ( )
        *
        */

        List<String> entries = new ArrayList<>();

        //Parse each entry
        Pattern entryPattern = Pattern.compile("(?<=\\()(.*?)(?=\\)\\$)");
        Matcher entryMatcher = entryPattern.matcher(formattedData);

        while(entryMatcher.find())
            entries.add(entryMatcher.group());

        for(String entry : entries)
        {
            Verse verse = new Verse();

            //Initialize RegEx Patterns
            Pattern idPattern = Pattern.compile("(?<=\\#id\\{)(.*?)(?=\\})"),
                citationPattern = Pattern.compile("(?<=\\#cit\\{)(.*?)(?=\\})"),
                textPattern = Pattern.compile("(?<=\\#txt\\{)(.*?)(?=\\})"),
                titlePattern = Pattern.compile("(?<=\\#t\\{)(.*?)(?=\\})"),
                categoryPattern = Pattern.compile("(?<=\\#cat\\{)(.*?)(?=\\})"),
                tagsPattern = Pattern.compile("(?<=\\#tgs\\{)(.*?)(?=\\})"),
                notesPattern = Pattern.compile("(?<=\\#n\\{)(.*?)(?=\\})"),
                favoritedPattern = Pattern.compile("(?<=\\#f\\{)(.*?)(?=\\})");

            //Parse ID
            Matcher idMatcher = idPattern.matcher(entry);
            if(idMatcher.find())
                verse.setId(idMatcher.group(1));

            //Parse Citation
            Matcher citationMatcher = citationPattern.matcher(entry);
            if(citationMatcher.find())
                verse.setVerse(citationMatcher.group(1));

            //Parse Text
            Matcher textMatcher = textPattern.matcher(entry);
            if(textMatcher.find())
                verse.setVerseText(textMatcher.group(1));

            //Parse Title
            Matcher titleMatcher = titlePattern.matcher(entry);
            if(titleMatcher.find())
                verse.setTitle(titleMatcher.group(1));

            //Parse Category
            Matcher categoryMatcher = categoryPattern.matcher(entry);
//            if(categoryMatcher.find())
//                verse.setCategory(categoryMatcher.group(1));

            //Parse Tag
            Matcher tagsMatcher = tagsPattern.matcher(entry);
            while(tagsMatcher.find())
            {
                List<String> tagList = new ArrayList<>();
                String tagsString = tagsMatcher.group(1);

                //Parse each tag
                Pattern tagPattern = Pattern.compile("(?<=\\[)(.*?)(?=\\])");
                Matcher tagMatcher = tagPattern.matcher(tagsString);
                while(tagMatcher.find())
                    tagList.add(tagMatcher.group());

                String[] tags = new String[tagList.size()];
                tags = tagList.toArray(tags);
                verse.setTags(tags);
            }

            //Parse Notes
            Matcher notesMatcher = notesPattern.matcher(entry);
            if(notesMatcher.find())
                verse.setNotes(notesMatcher.group(1));

            //Parse favorited
            Matcher favoritedMatcher = favoritedPattern.matcher(entry);
            if(favoritedMatcher.find())
                verse.setFavorited(Boolean.parseBoolean(favoritedMatcher.group(1)));

            //Add verse object to list
            verses.add(verse);
        }

        return verses;
//        displayVerses();
    }

//    private void displayVerses()
//    {
//        for(Verse verse : verses)
//        {
//            System.out.println("ID: " + verse.getId());
//            System.out.println(verse.getVerse());
//            System.out.println(verse.getVerseText());
//            System.out.println("Title: " + verse.getTitle());
//            System.out.println("Category: " + verse.getCategory());
//
//            System.out.print("Tag: ");
//            String[] tags = verse.getTags();
//            for(int i = 0; i < tags.length; i++)
//            {
//                String output = tags[i];
//                if(i != tags.length - 1)
//                    output += ", ";
//
//                System.out.print(output);
//            }
//
//            System.out.println();
//
//            if(verse.isFavorite())
//                System.out.println("Marked as Favorite!");
//            else System.out.println("Not a Favorite.");
//
//            System.out.println("Notes: " + verse.getNotes());
//            System.out.println();
//        }
//    }
}
