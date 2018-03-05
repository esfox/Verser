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

import bible.verse.organizer.objects.Verse;
import bible.verse.organizer.organizer.R;
import bible.verse.organizer.utilities.Color;

public class ViewVerse extends Fragment implements View.OnClickListener
{
    private View editButton,
                 deleteButton,
                 favoriteButton,
                 viewNotesButton;

    private View[] tagButtons;

    private TextView title,
                     text,
                     category,
                     notes;

    private ImageView star;

    private boolean isFavorite;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View layout = inflater.inflate(R.layout.fragment_view_verse, container, false);

        //TODO: Setup toolbar and assign verse citation as title

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
        title = layout.findViewById(R.id.view_verse_title);
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
//                                title.getVerseText() +
                                "\nNotes: " +
                                "Le not" +
//                                notes.getVerseText() +
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

    public void setData (Verse verse)
    {
        this.title.setText(verse.getTitle());
        this.text.setText(verse.getVerseText());
//        this.title.setVerseText(title);
        this.category.setText(verse.getCategoryName());
//        this.notes.setVerseText(notes);

        this.isFavorite = verse.isFavorite();
        toggleStarColor();
    }

    private void toggleFavorite ()
    {
        isFavorite = !isFavorite;
        toggleStarColor();
    }

    private void toggleStarColor()
    {
        int color = isFavorite?
            ContextCompat.getColor(getContext(), R.color.favoriteColor) :
            Color.getColor(getContext(), android.R.attr.textColorSecondary);

        star.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }
}
