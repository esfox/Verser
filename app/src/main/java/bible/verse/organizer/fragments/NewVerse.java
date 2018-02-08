package bible.verse.organizer.fragments;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.PopupWindow;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bible.verse.organizer.organizer.R;

public class NewVerse extends Fragment implements View.OnClickListener
{
    private CoordinatorLayout parent;
    private AutoCompleteTextView verseReference;
    private TextInputEditText verseText;
    private View
        addTitle,
        addCategory,
        addTags,
        addNotes,
        markAsFavorite;

    //verseReference AutoCompleteTextView fields
    private ArrayList<String> books;
    private int chapters, verses;
    private String verseIndex, book, chapter, verse;

    //TODO: Parse once and assign to data structure and check data structure instead
    private JSONArray booksArray;
    private JSONObject chaptersObject;

    public NewVerse() {}

    @Nullable
    @Override
    public View onCreateView
        (LayoutInflater inflater, @Nullable ViewGroup container,
         @Nullable Bundle savedInstanceState)
    {
        //Prevent keyboard from pushing bottom buttons
//        getActivity().getWindow()
//            .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        View layout = inflater.inflate(R.layout.fragment_new_verse, container, false);

        parent = layout.findViewById(R.id.new_verse_parent);

        verseReference = (AutoCompleteTextView)
            ((TextInputLayout) layout.findViewById(R.id.new_verse_reference_book)).getEditText();
        verseText = (TextInputEditText) ((TextInputLayout)
            layout.findViewById(R.id.new_verse_text)).getEditText();

        addTitle = layout.findViewById(R.id.new_verse_add_title);
        addCategory = layout.findViewById(R.id.new_verse_add_category);
        addTags = layout.findViewById(R.id.new_verse_add_tags);
        addNotes = layout.findViewById(R.id.new_verse_add_notes);
        markAsFavorite = layout.findViewById(R.id.new_verse_mark_as_favorite);

        //Create array of the views to be used as buttons
        View[] buttons =
            {
                verseReference,
                addTitle,
                addCategory,
                addTags,
                addNotes,
                markAsFavorite
            };

        //Loop through array to assign click listeners
        for(View button : buttons)
            button.setOnClickListener(this);

        //Setup (set listeners) verse reference book input box
        setupBookInputBox(layout);

        //Load verse index (to be done on Loading Screen)
        loadVerseIndex();
        loadBooks();

        return layout;
    }

//    @Override
//    public void onDestroy()
//    {
//        //Restore softInputMode to default
//        getActivity().getWindow()
//            .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED);
//        super.onDestroy();
//    }

    private void setupBookInputBox(View layout)
    {
        final TextInputLayout verseReferenceLayout = layout.findViewById(R.id.new_verse_reference_book);

        //Show drop-down when verseReference EditText is focused
        verseReference.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View view, boolean hasFocus)
            {
                if(hasFocus)
                    verseReference.showDropDown();
                else
                {
                    String input = verseReference.getText().toString();
                    processInput(input);

                    //Show error if an item from the drop-down was not selected (validation)
//                    boolean hasSelectedAnItem = books.contains
//                        (verseReference.getText().toString());
//
//                    if(!hasSelectedAnItem)
//                        verseReferenceLayout
//                            .setError("Please select a book from the drop-down list.");
//                    else verseReferenceLayout.setErrorEnabled(false);
                }
            }
        });

        //Remove error on item selected
        verseReference.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                verseReference.append(" ");
//                verseReferenceLayout.setErrorEnabled(false);

                book = adapterView.getItemAtPosition(i).toString();
                loadChaptersForBook();

                //TODO: Show chapter numbers popup and load verses for chapter selected

                //Hide keyboard when a book is selected.
                InputMethodManager inputManager = (InputMethodManager) getActivity()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow
                    (getActivity().getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        //Remove error on valid book typed
        verseReference.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                if(books.contains(charSequence.toString()))
                    verseReferenceLayout.setErrorEnabled(false);
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable)  {}
        });
    }

    @Override
    public void onClick(View view)
    {
        switch(view.getId())
        {
            //Show drop-down on re-click
            case R.id.new_verse_reference_book_input:
                verseReference.showDropDown();
                break;

            case R.id.new_verse_add_title:
//                showSnackbar("Add a Title");
                loadVersesForChapter();
                break;

            case R.id.new_verse_add_category:
                showSnackbar("Add Category");
                break;

            case R.id.new_verse_add_tags:
                showSnackbar("Add Tags");
                break;

            case R.id.new_verse_add_notes:
                showSnackbar("Add Notes");
                break;

            case R.id.new_verse_mark_as_favorite:
                showSnackbar("Mark as Favorite");
                break;
        }
    }

    //Read verse_index.json
    private void loadVerseIndex()
    {
        try
        {
            InputStream inputStream = getActivity().getAssets().open("verse_index.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            verseIndex = new String(buffer, "UTF-8");
        }
        catch (IOException exception)
        {
            Log.e("loadVerseIndex()", "Cannot read verse_index.json");
            exception.printStackTrace();
        }
    }

    private void loadBooks()
    {
        books = new ArrayList<>();
        if(verseIndex != null)
        {
            try
            {
                booksArray = new JSONArray(verseIndex);
                for(int i = 0; i < booksArray.length(); i++)
                {
                    JSONObject item = new JSONObject(booksArray.getString(i));
                    books.add(item.keys().next());
                }
            }
            catch (JSONException e) { e.printStackTrace(); }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>
            (getContext(), android.R.layout.simple_list_item_1, books);
        verseReference.setAdapter(adapter);
    }

    private void loadChaptersForBook()
    {
        try
        {
            if(booksArray != null)
            {
                for(int i = 0; i < booksArray.length(); i++)
                {
                    JSONObject bookObject = booksArray.getJSONObject(i);
                    if(bookObject.keys().next().equals(book))
                    {
                        chaptersObject = bookObject
                            .getJSONObject(book);

                        Iterator<String> keys = chaptersObject.keys();
                        int length = 0;
                        while(keys.hasNext())
                        {
                            keys.next();
                            length++;
                        }

                        chapters = length;

                        Toast.makeText(getContext(),
                            "Number of chapters for " + book + ": " + String.valueOf(chapters),
                            Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        catch (JSONException e) { e.printStackTrace(); }
    }

    private void loadVersesForChapter()
    {
//        if(chaptersObject != null)
//        {

        //TODO: Make it work (Popup)
        LayoutInflater inflater = (LayoutInflater) getActivity()
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View popupView = inflater.inflate(R.layout.verse_index, (ViewGroup)
            getActivity().findViewById(R.id.verse_index));

        popupView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Toast.makeText(getContext(), "POP", Toast.LENGTH_SHORT).show();
            }
        });

        float
        width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 240,
            getContext().getResources().getDisplayMetrics()),
        height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 240,
            getContext().getResources().getDisplayMetrics());

        PopupWindow chapterIndex = new PopupWindow
            (
                popupView,
                (int) width,
                (int) height,
                true
            );
        chapterIndex.setBackgroundDrawable(new ColorDrawable());
        chapterIndex.setOutsideTouchable(true);
        chapterIndex.showAsDropDown(verseReference);
//        }
    }

    private boolean inputIsValid(String input)
    {
        //Check input if it matches the format of a verse citation
        Pattern citationPattern = Pattern.compile
            ("([a-zA-Z]+\\s*){1,3} \\d{1,3}:\\d{1,2}");
        Matcher citationMatcher = citationPattern.matcher(input);
        return citationMatcher.matches();
    }

    private void processInput(String input)
    {
        if(inputIsValid(input))
        {
            //Split string to book and numbers
            String[]
                splitBook = input.split("\\s\\d{1,3}:\\d{1,2}"),
                splitNumbers = input.split("([a-zA-Z]+\\s*){1,3}");

            String numbers = splitNumbers[1];
//            book = splitBook[0];

            //Split numbers to chapter and verse
            String[] numbersSplit = numbers.split(":");
            chapter = numbersSplit[0];
            verse = numbersSplit[1];

            String message = "Input is Valid\n" + book + " " + chapter + ":" + verse;
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
        else
        {
            //change to setError()
            Toast.makeText(getContext(), "Invalid Input", Toast.LENGTH_SHORT).show();
        }
    }

    private void showSnackbar(String message)
    {
        Snackbar.make(parent, message, Snackbar.LENGTH_SHORT).show();
    }
}
