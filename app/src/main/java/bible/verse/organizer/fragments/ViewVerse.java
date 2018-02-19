package bible.verse.organizer.fragments;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import bible.verse.organizer.organizer.R;

public class ViewVerse extends Fragment implements View.OnClickListener
{
    private View editButton,
                 deleteButton,
                 favoriteButton,
                 viewNotesButton;

    private View[] tagButtons;

    private TextView citation,
                     text,
                     title,
                     category,
                     notes;

    private ImageView star;

    private boolean favorited;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View layout = inflater.inflate(R.layout.fragment_view_verse, container, false);

        //Button initialization
        editButton = layout.findViewById(R.id.view_verse_edit);
        viewNotesButton = layout.findViewById(R.id.view_verse_view_notes);
        deleteButton = layout.findViewById(R.id.view_verse_delete);
        favoriteButton = layout.findViewById(R.id.view_verse_favorite);

        View[] buttonList =
            {
                editButton,
                viewNotesButton,
                deleteButton,
                favoriteButton
            };

        for (View view : buttonList)
            view.setOnClickListener(this);

        //TextView Initializations
        citation = layout.findViewById(R.id.view_verse_citation);
        text = layout.findViewById(R.id.view_verse_text);
        category = layout.findViewById(R.id.view_verse_category);

        //ImageView Initializations
        star = layout.findViewById(R.id.view_verse_star);
        return layout;
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.view_verse_edit:
                Toast.makeText(getContext(), "Edit", Toast.LENGTH_SHORT).show();
                break;
            case R.id.view_verse_view_notes:
                Toast.makeText(
                        getContext(),
                        "Title: " +
                                "Sum Butipul Taitol" +
//                                title.getText() +
                                "\nNotes: " +
                                "Le not" +
//                                notes.getText() +
                                "\nTags: " +
                                "Tag1, Tag2, Tag3",
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.view_verse_delete:
                Toast.makeText(getContext(), "Delete", Toast.LENGTH_SHORT).show();
                break;
            case R.id.view_verse_favorite:
                Toast.makeText(getContext(), "Favorite", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void setData (String citation, String text, String title, String category, String[] tags, String notes, boolean favorited)
    {
        this.citation.setText(citation);
        this.text.setText(text);
//        this.title.setText(title);
        this.category.setText(category);
//        this.notes.setText(notes);

        this.favorited = favorited;
        toggleStarColor();
    }

    private void toggleFavorite ()
    {
        favorited = !favorited;
        toggleStarColor();
    }

    private void toggleStarColor()
    {
        if (favorited)
            star.setColorFilter(ContextCompat.getColor(getContext(), R.color.favoriteOn), PorterDuff.Mode.SRC_ATOP);
        else
            star.setColorFilter(ContextCompat.getColor(getContext(), R.color.textColorSecondary), PorterDuff.Mode.SRC_ATOP);
    }
}
