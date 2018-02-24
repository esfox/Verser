package bible.verse.organizer.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import bible.verse.organizer.organizer.R;

public class Home extends Fragment
{
    public Home() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View layout = inflater.inflate(R.layout.fragment_home, container, false);

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

        final NavigationView drawer = layout.findViewById(R.id.home_navigation_drawer);
        drawer.setNavigationItemSelectedListener
            (new NavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                drawerLayout.closeDrawer(GravityCompat.START);

                String message = "";

                switch(item.getItemId())
                {
                    case R.id.navigation_drawer_home:
                        message = "Home";
                        break;

                    case R.id.navigation_drawer_favorites:
                        message = "Favorites";
                        break;

                    case R.id.navigation_drawer_categories:
                        message = "Categories";
                        break;

                    case R.id.navigation_drawer_tags:
                        message = "Tags";
                        break;

                    case R.id.navigation_drawer_settings:
                        message = "Settings";
                        break;

                    case R.id.navigation_drawer_about:
                        message = "About";
                        break;

                    case R.id.navigation_drawer_help:
                        message = "Help";
                        break;
                }

                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

                return false;
            }
        });

        final FloatingActionButton newVerse = layout.findViewById(R.id.home_new_verse);
        newVerse.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                newVerse();
            }
        });

        return layout;
    }

    private void newVerse()
    {
        //Create class (static fields) for Fragment tags
        getActivity().getSupportFragmentManager()
            .beginTransaction()
            .setCustomAnimations
                (R.anim.slide_in_from_end, R.anim.slide_out_to_start,
                 R.anim.slide_in_from_start, R.anim.slide_out_to_end)
            .replace(R.id.parent_layout, new NewVerse(), "New Verse")
            .addToBackStack("New Verse")
            .commit();
    }
}
