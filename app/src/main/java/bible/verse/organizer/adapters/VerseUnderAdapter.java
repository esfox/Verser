package bible.verse.organizer.adapters;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import bible.verse.organizer.adapters.viewholders.VerseUnderViewHolder;
import bible.verse.organizer.objects.Verse;
import bible.verse.organizer.organizer.R;

public class VerseUnderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private List<Verse> verses;

    public VerseUnderAdapter(List<Verse> verses)
    {
        this.verses = verses;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new VerseUnderViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.verse_under_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        VerseUnderViewHolder newHolder = (VerseUnderViewHolder) holder;
        newHolder.bind(verses.get(position));
    }

    @Override
    public int getItemCount()
    {
        return verses.size();
    }
}
