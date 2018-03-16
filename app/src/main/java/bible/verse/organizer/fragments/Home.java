package bible.verse.organizer.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;
import java.util.UUID;

import bible.verse.organizer.MainActivity;
import bible.verse.organizer.adapters.VersesAdapter;
import bible.verse.organizer.interfaces.VerseWebRequestListener;
import bible.verse.organizer.objects.Category;
import bible.verse.organizer.objects.Verse;
import bible.verse.organizer.organizer.R;
import bible.verse.organizer.utilities.DatabaseHandler;
import bible.verse.organizer.utilities.VerseWebRequest;

public class Home extends Fragment implements
        View.OnClickListener,
        VerseWebRequestListener
{
    private VersesAdapter versesAdapter;
    private RecyclerView versesList;

    private static final String verseOfTheDayURL =
            "http://labs.bible.org/api/?passage=votd&type=json&formatting=plain";

    private static Verse verseOfTheDay = null;

    public Home() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        if(verseOfTheDay == null)
            new VerseWebRequest(this).execute(verseOfTheDayURL);

        View layout = inflater.inflate(R.layout.fragment_home, container, false);

        setupToolbarAndDrawer(layout);

        setupList(layout);

        final FloatingActionButton newVerse = layout.findViewById(R.id.home_new_verse);
        newVerse.setOnClickListener(this);
        layout.findViewById(R.id.debug_button).setOnClickListener(this);

        return layout;
    }

    @Override
    public void onClick(View view)
    {
        switch(view.getId())
        {
            case R.id.home_new_verse:
                newVerse();
                break;

            case R.id.debug_button:
                ((MainActivity) getActivity()).d_showVerses();
                break;
        }
    }

    @Override
    public void onRequestResponse(Verse verse)
    {
        verseOfTheDay = verse;
        showVerseOfTheDay();
    }

    private void setupToolbarAndDrawer(View layout)
    {
        final DrawerLayout drawerLayout = layout.findViewById(R.id.home_parent);

        Toolbar toolbar = layout.findViewById(R.id.home_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        //FOR DEBUGGING ONLY - CLEAR DATABASE
        toolbar.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View view)
            {
                ((MainActivity) getActivity()).d_clearDatabase();
                return true;
            }
        });

        final NavigationView drawer = layout.findViewById(R.id.home_navigation_drawer);
        drawer.setNavigationItemSelectedListener
            (new NavigationView.OnNavigationItemSelectedListener()
            {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item)
                {
                    drawerLayout.closeDrawer(GravityCompat.START);

                    switch(item.getItemId())
                    {
                        case R.id.navigation_drawer_home:
                            snack("Home");
                            break;

                        case R.id.navigation_drawer_favorites:
                            snack("Favorites");
                            break;

                        case R.id.navigation_drawer_categories:
                            categories();
                            break;

                        case R.id.navigation_drawer_tags:
                            tags();
                            break;

                        case R.id.navigation_drawer_settings:
                            snack("Settings");
                            break;

                        case R.id.navigation_drawer_about:
                            snack("About");
                            break;

                        case R.id.navigation_drawer_help:
                            snack("Help");
                            break;
                    }

                    return false;
                }
            });
    }

    private void setupList(View layout)
    {
        LinearLayoutManager versesListLayoutManager = new LinearLayoutManager(getContext());
        versesListLayoutManager.setReverseLayout(true);
        versesListLayoutManager.setStackFromEnd(true);

        versesList = layout.findViewById(R.id.home_list);
        versesList.setHasFixedSize(true);
        versesList.setLayoutManager(versesListLayoutManager);

        versesAdapter = new VersesAdapter();
        versesList.setAdapter(versesAdapter);

        if(verseOfTheDay != null)
            showVerseOfTheDay();

        versesList.post(new Runnable()
        {
            @Override
            public void run()
            {
                versesList.scrollToPosition(versesAdapter.getItemCount() - 1);
            }
        });

        updateVersesList();
    }

    private void updateVersesList()
    {
        List<Verse> verses = ((MainActivity) getActivity()).getVerses();

        if(verses == null)
            return;

        for(Verse verse : verses)
            versesAdapter.addVerse(verse);
    }

    private void showVerseOfTheDay()
    {
        versesAdapter.showVerseOfTheDay(verseOfTheDay);
        versesList.smoothScrollToPosition(versesAdapter.getItemCount() - 1);
    }

    private void newVerse()
    {
        //Create class (static fields) for Fragment tags
        getActivity().getSupportFragmentManager()
            .beginTransaction()
            .setCustomAnimations
                (R.anim.slide_in_from_end, R.anim.slide_out_to_start,
                 R.anim.slide_in_from_start, R.anim.slide_out_to_end)
            .replace(R.id.parent_layout, new NewVerse(), FragmentTags.NEW_VERSE)
            .addToBackStack(FragmentTags.NEW_VERSE)
            .commit();
    }

    private void categories()
    {
        getActivity().getSupportFragmentManager()
        .beginTransaction()
        .setCustomAnimations
            (0, 0,
             R.anim.slide_in_from_end, R.anim.slide_out_to_start)
        .replace(R.id.parent_layout, new Categories(), FragmentTags.CATEGORIES)
        .addToBackStack(FragmentTags.CATEGORIES)
        .commit();
    }

    private void tags()
    {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.parent_layout, new Tags(), FragmentTags.TAGS)
                .addToBackStack(FragmentTags.TAGS)
                .commit();
    }

    //TEMPORARY
    private void snack(String message)
    {
        Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
    }
}
