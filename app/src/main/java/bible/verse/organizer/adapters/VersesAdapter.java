package bible.verse.organizer.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import bible.verse.organizer.adapters.viewholders.VerseOfTheDayViewHolder;
import bible.verse.organizer.adapters.viewholders.VerseViewHolder;
import bible.verse.organizer.objects.Category;
import bible.verse.organizer.objects.Verse;
import bible.verse.organizer.organizer.R;

public class VersesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private List<Verse> verses;
    private Verse verseOfTheDay;

    private boolean verseOfTheDayIsLoaded;

    public VersesAdapter(List<Verse> verses)
    {
        this.verses = verses;

//        makeDummyData();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        switch(viewType)
        {
            case TYPE_HEADER:
                return new VerseOfTheDayViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.verse_of_the_day_card, parent, false));

            default:
                return new VerseViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.verse_card, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        if(holder instanceof VerseViewHolder)
            ((VerseViewHolder) holder).bind(verses.get(position));
        else if(holder instanceof VerseOfTheDayViewHolder)
        {
            if(verseOfTheDay == null)
                return;

            ((VerseOfTheDayViewHolder) holder).bind(verseOfTheDay);
        }
    }

    @Override
    public int getItemViewType(int position)
    {
        if(verseOfTheDayIsLoaded)
        {
            if(position == verses.size())
                return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    @Override
    public int getItemCount()
    {
        return verseOfTheDayIsLoaded? verses.size() + 1 : verses.size();
    }

//    public void addVerse(Verse verse)
//    {
//        verses.add(verse);
//        notifyItemInserted(verses.size() - 1);
//    }

    public void showVerseOfTheDay(Verse verse)
    {
        verseOfTheDayIsLoaded = true;
        verseOfTheDay = verse;
        notifyItemInserted(verses.size());
    }

    private void makeDummyData()
    {
        for(int i = 0; i < 20; i++)
        {
            Verse verse = new Verse();
            verse.setVerse(String.valueOf(i+1) + "\tJohn 3:16");
            verse.setVerseText("For God so loved the world... boi.");

            Category category = new Category();
            category.setName("Dogetory");
            verse.setCategory(category);

            verse.setTags(new String[] { "tag-ulan", "tag-araw", "tag-pipiso" });
            verse.setTitle("Huling Kontrata");
            verse.setNotes("Fat Cakes");
            verse.setFavorited(true);

            verses.add(verse);
        }
    }
}
