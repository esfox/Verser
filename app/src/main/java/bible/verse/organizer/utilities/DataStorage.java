package bible.verse.organizer.utilities;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class DataStorage
{
    private File file;

    public DataStorage(Context context)
    {
        create(context);
    }

    private void create(Context context)
    {
        String filename = "entrylist.txt";

        file = new File(context.getFilesDir(), filename);
        FileOutputStream fileOutputStream;

        try
        {
            fileOutputStream = context.openFileOutput(filename, Context.MODE_APPEND);
            fileOutputStream.close();
        }
        catch (IOException e)
        {
            Log.e("Exception", "File write failed: " + e.toString());
        }

        Log.i("DataStorage", "Successfully created storage file");
    }

    public String read()
    {
        String fileContent = null;
        try
        {
            InputStream inputStream = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();

            boolean done = false;

            while (!done)
            {
                String line = reader.readLine();
                done = (line == null);

                if (line != null)
                    stringBuilder.append(line);
            }
            reader.close();
            inputStream.close();

            fileContent =  stringBuilder.toString();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        Log.i("DataStorage", "Successfully read storage file");

        return fileContent;
    }

    public void update(String formattedData)
    {
        try
        {
            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            fileOutputStream.write(formattedData.getBytes());
            fileOutputStream.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        Log.i("DataStorage", "Successfully updated storage file");
    }

    public void resetStorageFile()
    {
        PrintWriter writer = null;
        try
        {
            writer = new PrintWriter(file);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        writer.print("");
        writer.close();

        Log.i("DataStorage", "Storage file has been reset");
    }

}
