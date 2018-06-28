package bible.verse.organizer.fragments;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import bible.verse.organizer.objects.Verse;
import bible.verse.organizer.organizer.R;
import bible.verse.organizer.utilities.Color;
import bible.verse.organizer.utilities.DatabaseHandler;

public class ViewVerse extends Fragment implements View.OnClickListener
{
    private View editButton,
                 deleteButton,
                 favoriteButton,
                 viewNotesButton;

    private View[] tagButtons;

    private Toolbar toolbar;

    private TextView title,
                     text,
                     category,
                     notes;

    private ImageView star;

    private Verse verse;
    private boolean isFavorite;

    private DatabaseHandler databaseHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View layout = inflater.inflate(R.layout.fragment_view_verse, container, false);

        //TODO: Setup toolbar and assign verse citation as title
        toolbar = layout.findViewById(R.id.view_verse_toolbar);
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

        setData();

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
                                "\nTag: " +
                                "Tag1, Tag2, Tag3",
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.view_verse_delete:
                delete();
                break;
            case R.id.view_verse_favorite:
                toggleFavorite();
                break;
        }
    }

    private void setData()
    {
        toolbar.setTitle(verse.getVerse());
        title.setText(verse.getTitle());
        text.setText(verse.getVerseText());
//        this.title.setVerseText(title);
        category.setText(verse.getCategory().getName());
//        this.notes.setVerseText(notes);

        isFavorite = verse.isFavorite();
        toggleStarColor();
    }

    public void setVerse(Verse verse)
    {
        this.verse = verse;
    }

    private void delete ()
    {
//        getActivity().getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.parent_layout, new Home(), FragmentTags.HOME)
//                .addToBackStack(FragmentTags.HOME)
//                .commit();
//
//        databaseHandler.deleteEntry(verse);
    }

    private void toggleFavorite()
    {
        isFavorite = !isFavorite;
        verse.setFavorite(isFavorite);
//        databaseHandler.updateEntry(verse);
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
