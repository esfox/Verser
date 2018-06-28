package bible.verse.organizer.fragments;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import bible.verse.organizer.MainActivity;
import bible.verse.organizer.adapters.VerseUnderAdapter;
import bible.verse.organizer.objects.Category;
import bible.verse.organizer.objects.Verse;
import bible.verse.organizer.organizer.R;
import bible.verse.organizer.utilities.DatabaseHandler;

public class VerseUnder extends Fragment implements View.OnClickListener
{
    public VerseUnder() {}

    private VerseUnderAdapter adapter;
    private RecyclerView recyclerView;

    private Category category;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View layout = inflater.inflate(R.layout.fragment_verse_under, container, false);

        //Setup AppBar
        Toolbar appBar = layout.findViewById(R.id.verse_under_toolbar);
        appBar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        //Initial text
        TextView initialText = layout.findViewById(R.id.verse_under_initial_text);

        //Setup Categories list
        recyclerView = layout.findViewById(R.id.verse_under_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setTag(initialText);

        //Reverse layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        //Get verse from database
        DatabaseHandler databaseHandler = new DatabaseHandler(getContext());
        List<Verse> verses = databaseHandler.searchEntries(category.getName());

        //Hide initial text (no saved verses yet) when there are no verses
        if(verses.isEmpty())
            initialText.setVisibility(View.VISIBLE);

        //Set adapter to Verses list
        adapter = new VerseUnderAdapter(verses);
        recyclerView.setAdapter(adapter);

        layout.findViewById(R.id.verse_under_add).setOnClickListener(this);

        return layout;
    }

    public void setListCategory (Category category)
    {
        this.category = category;
    }

    @Override
    public void onClick(View view)
    {

    }
}
