package bible.verse.organizer.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import bible.verse.organizer.organizer.R;

import static java.lang.System.in;

public class ViewVerse extends Fragment implements View.OnClickListener
{
    private View editButton,
                 deleteButton,
                 favoriteButton;

    private TextView citation,
                     text,
                     title,
                     category,
                     notes;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View layout = inflater.inflate(R.layout.fragment_view_verse, container, false);

        //Button initialization
        editButton = layout.findViewById(R.id.verse_entry_edit_button);
        deleteButton = layout.findViewById(R.id.verse_entry_delete_button);
        favoriteButton = layout.findViewById(R.id.verse_entry_favorite_button);

        View[] buttonList =
            {
                editButton,
                deleteButton,
                favoriteButton
            };

        for (View view : buttonList)
            view.setOnClickListener(this);

        //TextView Initializations
        citation = layout.findViewById(R.id.verse_entry_citation);
        text = layout.findViewById(R.id.verse_entry_text);
        category = layout.findViewById(R.id.verse_entry_category);

        return layout;
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.verse_entry_edit_button:
                Toast.makeText(getContext(), "Edit", Toast.LENGTH_SHORT).show();
                break;
            case R.id.verse_entry_delete_button:
                Toast.makeText(getContext(), "Delete", Toast.LENGTH_SHORT).show();
                break;
            case R.id.verse_entry_favorite_button:
                Toast.makeText(getContext(), "Favorite", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void setData (String citation, String text, String title, String category, String[] tags, String notes, boolean isFavorite)
    {
        this.citation.setText(citation);
        this.text.setText(text);
        this.category.setText(category);
    }
}
