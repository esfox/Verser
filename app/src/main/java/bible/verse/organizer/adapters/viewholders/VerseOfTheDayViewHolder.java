package bible.verse.organizer.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import bible.verse.organizer.objects.Verse;
import bible.verse.organizer.organizer.R;

public class VerseOfTheDayViewHolder extends RecyclerView.ViewHolder
{
    private TextView verse, verseText, date;

    //TODO: Make this work (use webservice)
    public VerseOfTheDayViewHolder(final View itemView)
    {
        super(itemView);
        itemView.findViewById(R.id.verse_of_the_day_card)
                .setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Toast.makeText(itemView.getContext(), "View Verse of the Day",
                        Toast.LENGTH_SHORT).show();
            }
        });

        verse = itemView.findViewById(R.id.verse_of_the_day_verse);
        verseText = itemView.findViewById(R.id.verse_of_the_day_verse_text);
        date = itemView.findViewById(R.id.verse_of_the_day_date);
    }

    public void bind(Verse verseOfTheDay)
    {
        verse.setText(verseOfTheDay.getVerse());
        verseText.setText(verseOfTheDay.getVerseText());

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
        String dateString = dateFormat.format(Calendar.getInstance().getTime());
        date.setText(dateString);
    }
}
