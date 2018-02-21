package bible.verse.organizer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import bible.verse.organizer.fragments.Home;
import bible.verse.organizer.organizer.R;

public class MainActivity extends AppCompatActivity
{
//    private Toolbar appBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        appBar = (Toolbar) findViewById(R.id.parent_toolbar);
//        setSupportActionBar(appBar);

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

//    public Toolbar getAppBar()
//    {
//        return appBar;
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.action_settings:
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
