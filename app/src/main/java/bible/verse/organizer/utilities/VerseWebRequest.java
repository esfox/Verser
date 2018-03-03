package bible.verse.organizer.utilities;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import bible.verse.organizer.interfaces.VerseWebRequestListener;


public class VerseWebRequest extends AsyncTask<String, String, String>
{
    private VerseWebRequestListener listener;

    public VerseWebRequest(VerseWebRequestListener listener)
    {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... urlString)
    {
        StringBuilder response = new StringBuilder();
        InputStream inputStream;
        InputStreamReader inputStreamReader;
        BufferedReader reader;
        try
        {
            URL url = new URL(urlString[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            inputStream = new BufferedInputStream(connection.getInputStream());
            inputStreamReader = new InputStreamReader(inputStream);
            if(connection.getResponseCode() == HttpURLConnection.HTTP_OK)
            {
                reader = new BufferedReader(inputStreamReader);
                String line;
                while((line = reader.readLine()) != null)
                    response.append(line);

                inputStream.close();
                inputStreamReader.close();
                reader.close();
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
            Log.e("votd_request", "error requesting");
        }
        return response.toString();
    }

    @Override
    protected void onPostExecute(String response)
    {
        super.onPostExecute(response);
        listener.onRequestResponse(response);
    }
}
