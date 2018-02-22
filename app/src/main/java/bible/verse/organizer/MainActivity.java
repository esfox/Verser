package bible.verse.organizer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import bible.verse.organizer.fragments.Home;
import bible.verse.organizer.interfaces.OnBackPressListener;
import bible.verse.organizer.organizer.R;

public class MainActivity extends AppCompatActivity
{
//    private Toolbar appBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        launchHomeFragment();
    }

    private void launchHomeFragment()
    {
        //TODO: Create class (static fields) for Fragment tags
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.parent_layout, new Home(), "Home")
            .commit();
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
