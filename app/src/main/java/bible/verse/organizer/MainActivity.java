package bible.verse.organizer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.List;
import java.util.UUID;

import bible.verse.organizer.fragments.Home;
import bible.verse.organizer.interfaces.OnBackPressListener;
import bible.verse.organizer.objects.Verse;
import bible.verse.organizer.organizer.R;
import bible.verse.organizer.utilities.DatabaseHandler;
import bible.verse.organizer.utilities.DataStorage;
import bible.verse.organizer.utilities.Formatter;
import bible.verse.organizer.utilities.Parser;

public class MainActivity extends AppCompatActivity
{
    //Parent view for Snackbars
    private View parent;

    private DataStorage dataStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        parent = findViewById(R.id.parent_layout);

        dataStorage = new DataStorage(this);

        launchHomeFragment();
    }

    private void launchHomeFragment()
    {
        //Create class (static fields) for Fragment tags
        //TODO: Create class (static fields) for Fragment tags
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.parent_layout, new Home(), "Home")
            .commit();
    }

    public void saveVerse(Verse verse)
    {
        dataStorage.update(Formatter.format(verse));
        Log.i("DataStorage", "Verse has been saved.");
        Snackbar.make(parent, "Verse has been saved!", Snackbar.LENGTH_SHORT).show();
    }

    public void readEntries()
    {
        List<Verse> verses = Parser.parse(dataStorage.read());

        String messageToDisplay = "";

        messageToDisplay += "Number of entries: " + String.valueOf(verses.size()) + "\n\n\n";

        for(Verse verse : verses)
        {
            messageToDisplay +=
                "Citation: " + verse.getCitation() + "\n" +
                "Verse: " + verse.getText() + "\n" +
                "Category: " + verse.getCategory() + "\n" +
                "Tags:\n";

            for(String tag : verse.getTags())
                messageToDisplay += "- " + tag + "\n";

            messageToDisplay +=
                "Title: " + verse.getTitle() + "\n" +
                "Notes: " + verse.getNotes() + "\n" +
                "Marked as Favorite: " + String.valueOf(verse.isFavorited())
                + "\n\n";
        }

        new AlertDialog.Builder(this)
            .setTitle("Verses")
            .setMessage(messageToDisplay)
            .setPositiveButton("Done", null)
            .show();
    }

    @Override
    public void onBackPressed()
    {
        boolean backOverridden = false;
        Fragment latestFragment = getLatestFragment();
        if (latestFragment != null)
            if(latestFragment instanceof OnBackPressListener)
                backOverridden = ((OnBackPressListener) latestFragment).onBackPressed();

        if(!backOverridden)
            super.onBackPressed();
    }

    @Nullable
    private Fragment getLatestFragment()
    {
        int backStackCount = getSupportFragmentManager().getBackStackEntryCount();

        if (backStackCount <= 0)
            return null;

        int lastBackStackIndex = backStackCount - 1;
        FragmentManager.BackStackEntry latestEntry =
            getSupportFragmentManager().getBackStackEntryAt(lastBackStackIndex);
        return getSupportFragmentManager().findFragmentByTag(latestEntry.getName());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_settings:
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
