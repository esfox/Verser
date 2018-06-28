package bible.verse.organizer.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import bible.verse.organizer.objects.Verse;
import bible.verse.organizer.organizer.R;

public class VerseUnderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    private TextView title, citation;
    private Verse verse;

    public VerseUnderViewHolder(View itemView)
    {
        super(itemView);

        title = itemView.findViewById(R.id.verse_under_title);
        citation = itemView.findViewById(R.id.verse_under_citation);

        itemView.findViewById(R.id.verse_under_item).setOnClickListener(this);
    }

    public void bind(Verse verse)
    {
        this.verse = verse;

        title.setText(verse.getTitle());
        citation.setText(verse.getVerse());
    }

    @Override
    public void onClick(View view)
    {
        //Toast
    }



}
