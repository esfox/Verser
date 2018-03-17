package bible.verse.organizer.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import bible.verse.organizer.objects.Verse;
import bible.verse.organizer.organizer.R;


public class VerseViewHolder extends RecyclerView.ViewHolder implements
    View.OnClickListener
{
    private Verse verse;
    private TextView verseCitation, verseText, category;

    public VerseViewHolder(View itemView)
    {
        super(itemView);

        verseCitation = itemView.findViewById(R.id.verse_card_verse);
        verseText = itemView.findViewById(R.id.verse_card_verse_text);
        category = itemView.findViewById(R.id.verse_card_category);

        itemView.findViewById(R.id.verse_card).setOnClickListener(this);
    }

    public void bind(Verse verse)
    {
        this.verse = verse;

        verseCitation.setText(verse.getVerse());
        verseText.setText(verse.getVerseText());
//        category.setText(verse.getCategory());
    }

    @Override
    public void onClick(View view)
    {
        Toast.makeText(itemView.getContext(), "View " + verse.getVerse(), Toast.LENGTH_SHORT)
            .show();
    }
}
