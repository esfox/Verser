package bible.verse.organizer.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import bible.verse.organizer.adapters.viewholders.VerseViewHolder;
import bible.verse.organizer.objects.Verse;
import bible.verse.organizer.organizer.R;

public class VersesAdapter extends RecyclerView.Adapter<VerseViewHolder>
{
    private List<Verse> verses;

    public VersesAdapter()
    {
        verses = new ArrayList<>();

        makeDummyData();
    }

    @Override
    public VerseViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new VerseViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.verse_card, parent, false));
    }

    @Override
    public void onBindViewHolder(VerseViewHolder holder, int position)
    {
        holder.bind(verses.get(position));
    }

    @Override
    public int getItemCount()
    {
        return verses.size();
    }

    private void makeDummyData()
    {
        for(int i = 0; i < 20; i++)
        {
            Verse verse = new Verse();
            verse.setVerse("John 3:16");
            verse.setVerseText("For God so loved the world... boi.");
            verse.setCategoryName("Dogetory");
            verse.setTags(new String[] { "tag-ulan", "tag-araw", "tag-pipiso" });
            verse.setTitle("Huling Kontrata");
            verse.setNotes("Fat Cakes");
            verse.setFavorited(true);

            verses.add(verse);
        }
    }
}
