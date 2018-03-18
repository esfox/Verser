package bible.verse.organizer.utilities;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import bible.verse.organizer.interfaces.VerseWebRequestListener;
import bible.verse.organizer.objects.Verse;


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

        String
            book,
            chapter,
            verse = "",
            verseCitation = "",
            verseText = "";

        JSONArray verseJsonArray;
        try
        {
            verseJsonArray = new JSONArray(response);

            for(int i = 0; i < verseJsonArray.length(); i++)
            {
                JSONObject verseJsonObject = verseJsonArray.getJSONObject(i);
                book = verseJsonObject.getString("bookname");
                chapter = verseJsonObject.getString("chapter");

                if(i == 0)
                    verse += verseJsonObject.getString("verse");
                else if(i == verseJsonArray.length() - 1)
                    verse += "-" + verseJsonObject.getString("verse");

                verseCitation = book + " " + chapter + ":" + verse;
                verseText += verseJsonObject.getString("text");

                if(!verseText.endsWith(" "))
                    verseText += " ";
            }

            Verse verseObject = new Verse();
            verseObject.setVerse(verseCitation);
            verseObject.setVerseText(verseText);

            listener.onRequestResponse(verseObject);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
}
