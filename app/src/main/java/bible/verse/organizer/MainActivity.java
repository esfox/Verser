package bible.verse.organizer;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

import bible.verse.organizer.fragments.Home;
import bible.verse.organizer.objects.DataStorer;
import bible.verse.organizer.organizer.R;

public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        launchHomeFragment();
    }

    private void executeTestGround ()
    {
        Button read = (Button) findViewById(R.id.temporary_read);
        Button update = (Button) findViewById(R.id.temporary_update);
        Button reset = (Button) findViewById(R.id.temporary_reset);
        final DataStorer dataStorer = new DataStorer(this);

        read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String x = dataStorer.read();
                Log.d("TestingGround", x);
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataStorer.update("wow");
                Log.d("TestingGround", "Updated");
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataStorer.resetStorageFile();
                Log.d ("TestingGround", "Reset");
            }
        });
    }

    private void launchHomeFragment()
    {
        executeTestGround();
        //Create class (static fields) for Fragment tags
        getSupportFragmentManager()
            .beginTransaction()
            .add(R.id.parent_layout, new Home(), "Home")
            .commit();
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
        int id = item.getItemId();

        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
