package bible.verse.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.faithcomesbyhearing.dbt.Dbt;

import java.util.List;

import bible.verse.organizer.fragments.FragmentTags;
import bible.verse.organizer.fragments.Home;
import bible.verse.organizer.interfaces.OnBackPressListener;
import bible.verse.organizer.objects.Category;
import bible.verse.organizer.objects.Verse;
import bible.verse.organizer.organizer.R;
import bible.verse.organizer.utilities.DatabaseHandler;

//import bible.verse.organizer.utilities.DataStorage;

public class MainActivity extends AppCompatActivity
{
    private static boolean applyDarkTheme;

    //    private DataStorage dataStorage;
    private DatabaseHandler databaseHandler;

    private List<Verse> verses;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Dbt.setApiKey("8a74dbcb2ba6d3d407c83a2a123f1cb5");

        setTheme(applyDarkTheme? R.style.AppThemeDark : R.style.AppTheme);

        setContentView(R.layout.activity_main);

//        dataStorage = new DataStorage(this);

        databaseHandler = new DatabaseHandler(this);

        launchHomeFragment();
    }

    private void launchHomeFragment()
    {
        //Create class (static fields) for Fragment tags
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.parent_layout, new Home(), FragmentTags.HOME)
            .commit();
    }

    public List<Verse> getVerses()
    {
        verses = databaseHandler.getAllEntries();
        return verses;
    }

    public void saveVerse(Verse verse)
    {
        //Save verse
        databaseHandler.addEntry(verse);

        //Update verse's category verse count
        Category category = verse.getCategory();
        if(category != null)
        {
            category.updateVerseCount();
            databaseHandler.updateCategory(category);
        }

        Log.i("Database", "Verse has been saved.");
        Toast.makeText(this, "Verse has been saved!", Toast.LENGTH_SHORT).show();
    }

    public List<Category> getCategories()
    {
        return databaseHandler.getAllCategories();
    }

    public void saveCategory(Category category)
    {
        databaseHandler.addCategory(category);
        Log.i("Database", "Category has been saved.");
    }

    @SuppressWarnings("StringConcatenationInLoop")
    public void d_showVerses()
    {
        verses = databaseHandler.getAllEntries();

        String message = "Entry Count: " + String.valueOf(verses.size()) + "\n\n";

        for (Verse verse : verses)
        {
            message +=
                    "ID: " + verse.getId() + "\n" +
                            verse.getVerse() + "\n" +
                            verse.getVerseText() + "\n" +
                            "Category: " + verse.getCategory().getName() + "\n" +
                            "Tag: ";

            for (String tag : verse.getTags())
                message += tag + ", ";

            message += "\n" +
                    "Title: " + verse.getTitle() + "\n" +
                    "Notes: " + verse.getNotes() + "\n" +
                    "Is Favorite: " + verse.isFavorite() + "\n\n\n";
        }

        new AlertDialog.Builder(this)
                .setTitle("Database Contents")
                .setMessage(message)
                .setPositiveButton("Done", null)
                .show();
    }

    public void d_clearDatabase()
    {
        databaseHandler.clearEntriesTable();
        databaseHandler.clearCategoriesTable();
        Toast.makeText(this, "Database Cleared", Toast.LENGTH_SHORT).show();
    }

    //TODO: Fix colors in dark theme
    @SuppressWarnings("unused")
    public void changeTheme()
    {
        applyDarkTheme = !applyDarkTheme;
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

//    public void saveVerse(Verse verse)
//    {
//        dataStorage.update(Formatter.format(verse));
//        Log.i("DataStorage", "Verse has been saved.");
//        Snackbar.make(parent, "Verse has been saved!", Snackbar.LENGTH_SHORT).show();
//    }

//    public void readEntries()
//    {
//        List<Verse> verses = Parser.parse(dataStorage.read());
//
//        String messageToDisplay = "";
//
//        messageToDisplay += "Number of entries: " + String.valueOf(verses.size()) + "\n\n\n";
//
//        for(Verse verse : verses)
//        {
//            messageToDisplay +=
//                "Citation: " + verse.getVerse() + "\n" +
//                "Verse: " + verse.getVerseText() + "\n" +
//                "Category: " + verse.getCategory() + "\n" +
//                "Tag:\n";
//
//            for(String tag : verse.getTags())
//                messageToDisplay += "- " + tag + "\n";
//
//            messageToDisplay +=
//                "Title: " + verse.getTitle() + "\n" +
//                "Notes: " + verse.getNotes() + "\n" +
//                "Marked as Favorite: " + String.valueOf(verse.isFavorite())
//                + "\n\n";
//        }
//
//        new AlertDialog.Builder(this)
//            .setTitle("Verses")
//            .setMessage(messageToDisplay)
//            .setPositiveButton("Done", null)
//            .show();
//    }

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
