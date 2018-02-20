package bible.verse.organizer.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import bible.verse.organizer.organizer.R;
import bible.verse.organizer.verse_index.VerseIndexAdapter;

public class NewVerse extends Fragment implements
    View.OnClickListener
{
    private CoordinatorLayout parent;
    private EditText
        citation,
        verseText;

    private PopupWindow verseIndex;

    private String
        verseIndexJson,
        selectedBook,
        selectedChapter;

    private ArrayList<String> books;
    private int
        chapters,
        verses;

    /*
        Save JSON classes for faster parsing.
        Number of chapters are parsed only when a book is selected
        and number of verses are parsed only when a chapter is selected.
    */
    private JSONArray booksArray;
    private JSONObject chaptersObjects;

    public NewVerse() {}

    @Nullable
    @Override
    public View onCreateView
        (LayoutInflater inflater, @Nullable ViewGroup container,
         @Nullable Bundle savedInstanceState)
    {
        View layout = inflater.inflate(R.layout.fragment_new_verse, container, false);

        //Load verse index (to be done on Loading Screen)
        loadVerseIndex();
        loadBooks();

        //Setup ActionBar
        setupActionBar(layout);

        //Initialize parent layout for using in Snackbars
        parent = layout.findViewById(R.id.new_verse_parent);

        //Setup citation EditText
        setupInputFields(layout);

        //Setup verse index drop down
        setupVerseIndexDropDown();

        //Initialize views to be used as buttons
        View
            addTitle = layout.findViewById(R.id.new_verse_add_title),
            addCategory = layout.findViewById(R.id.new_verse_add_category),
            addTags = layout.findViewById(R.id.new_verse_add_tags),
            addNotes = layout.findViewById(R.id.new_verse_add_notes),
            markAsFavorite = layout.findViewById(R.id.new_verse_mark_as_favorite);

        //Create array of the views to be used as buttons
        View[] buttons =
            {
                citation,
                addTitle,
                addCategory,
                addTags,
                addNotes,
                markAsFavorite
            };

        //Loop through array to assign click listeners
        for(View button : buttons)
            button.setOnClickListener(this);

        return layout;
    }

    @Override
    public void onClick(View view)
    {
        switch(view.getId())
        {
            case R.id.new_verse_citation_input:
                showVerseIndex();
                break;

            case R.id.new_verse_add_title:
                showSnackbar("Add a Title");
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

    //Setup ActionBar
    private void setupActionBar(View layout)
    {
        Toolbar toolbar = layout.findViewById(R.id.new_verse_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                //Cancel Confirmation Dialog
                new AlertDialog.Builder(getContext())
                    .setTitle("Discard your changes?")
                    .setMessage("You have not saved this verse yet. " +
                        "Are you sure you want to cancel adding a new verse?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            getActivity().getSupportFragmentManager().popBackStack();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
            }
        });
    }

    //Setup verse citation and verse text EditTexts
    private void setupInputFields(View layout)
    {
        citation = layout.findViewById(R.id.new_verse_citation_input);
        citation.setKeyListener(null);
        citation.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View view, boolean hasFocus)
            {
                if(hasFocus)
                {
                    if((boolean) citation.getTag())
                        toggleKeyboard(null, false);

                    citation.callOnClick();
                }
                //TODO: Get citation text when pressing done FAB
//                else
//                    Log.d("citation", citation.getText().toString());
            }
        });

        /*
            TODO: Add smart functionality where if a valid verse citation
            (RegEx - "(\d\s)?([a-zA-Z]+\s*){1,3}\s\d{1,3}:\d{1,3}") is included
            in the text, parse it and assign it to citation EditText.
         */
        verseText = ((TextInputLayout)layout.findViewById(R.id.new_verse_text)).getEditText();
        verseText.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(final View view, boolean hasFocus)
            {
                if(hasFocus)
                    view.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            toggleKeyboard(view, true);
                        }
                    });
            }
        });
    }

    //Setup verse index drop down
    private void setupVerseIndexDropDown()
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        final View verseIndexLayout = inflater.inflate(R.layout.verse_index, null);

        //Initializing page buttons
        View
            verseIndexBook = verseIndexLayout.findViewById(R.id.verse_index_book),
            verseIndexChapter = verseIndexLayout.findViewById(R.id.verse_index_chapter),
            verseIndexVerse = verseIndexLayout.findViewById(R.id.verse_index_verse);

        //Make array of page buttons to loop through each and assign click listener later on
        View[] verseIndexPageButtons = new View[]
            {
                verseIndexBook,
                verseIndexChapter,
                verseIndexVerse
            };

        //Initialize page button icons and labels
        final ImageView
            bookIcon = verseIndexLayout.findViewById(R.id.verse_index_book_icon),
            chapterIcon = verseIndexLayout.findViewById(R.id.verse_index_chapter_icon),
            verseIcon = verseIndexLayout.findViewById(R.id.verse_index_verse_icon);

        final TextView
            bookLabel = verseIndexLayout.findViewById(R.id.verse_index_book_label),
            chapterLabel = verseIndexLayout.findViewById(R.id.verse_index_chapter_label),
            verseLabel = verseIndexLayout.findViewById(R.id.verse_index_verse_label);

        //Make array of page button icons and labels
        final ImageView[] icons = new ImageView[] { bookIcon, chapterIcon, verseIcon };
        final TextView[] labels = new TextView[] { bookLabel, chapterLabel, verseLabel };

        //Initialize book search EditText on book selection page
        final EditText bookSearch = verseIndexLayout.findViewById(R.id.verse_index_books_search);

        /*
            TODO: Try to make height dynamic
            (height based on content EXCEPT on book selection to
            not obstruct page buttons if keyboard is shown
         */
        //Initialize verse index PopupWindow
        verseIndex = new PopupWindow
            (
                verseIndexLayout,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
//                (int) TypedValue.applyDimension
//                    (TypedValue.COMPLEX_UNIT_DIP, 350, getResources().getDisplayMetrics()),
                true
            );
        verseIndex.setBackgroundDrawable(new ColorDrawable());
        verseIndex.setOutsideTouchable(true);
        verseIndex.update();

        //Initialize ViewPager for verse index
        final ViewPager verseIndexViewPager =
            verseIndexLayout.findViewById(R.id.verse_index_view_pager);
        verseIndexViewPager.setOffscreenPageLimit(2);

        //Initializing of verse index pages
        View[] pageLayouts = new View[]
            {
                verseIndexLayout.findViewById(R.id.verse_index_books),
                verseIndexLayout.findViewById(R.id.verse_index_chapters),
                verseIndexLayout.findViewById(R.id.verse_index_verses)
            };

        //int constants for verse index pages
        final int
            BOOK_SELECTION = 0,
            CHAPTER_SELECTION = 1,
            VERSE_SELECTION = 2;

        //Adding each page to ViewPager adapter
        VerseIndexAdapter pagerAdapter = new VerseIndexAdapter();
        for(View pageLayout : pageLayouts)
            pagerAdapter.addPage(pageLayout);
        verseIndexViewPager.setAdapter(pagerAdapter);

        /*
            Set boolean tag to ViewPager to track if user is in chapter selection
            (To know what adapter to change, whether chapters or verses)
        */
        verseIndexViewPager.setTag(false);

        /*
            Set boolean tag to know if in not in book selection to
            citation EditText as well for usage on its OnFocusChangeListener
        */
        citation.setTag(false);

        //Colors for the states of the page buttons (buttons on bottom)
        final int
            activeColor = ContextCompat.getColor(getContext(), R.color.colorAccent),
            normalColor = ContextCompat.getColor(getContext(), R.color.textColorSecondary);

        //Set book button to selected state (active color)
        icons[0].setColorFilter(activeColor, PorterDuff.Mode.SRC_ATOP);
        labels[0].setTextColor(activeColor);

        verseIndexViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageSelected(int page)
            {
                //Show keyboard if in book selection page and hide keyboard if not
//                toggleKeyboard(bookSearch, page == BOOK_SELECTION);

                //Toggle color of page buttons
                toggleVerseIndexPageButtonState(icons, labels, page, activeColor, normalColor);

                //Reset book search when going back to book selection page
                if(page == BOOK_SELECTION)
                    bookSearch.setText("");

                /*
                    Set boolean tag of ViewPager and citation EditText to indicate
                    what is currently being selected (if chapter or verse).
                 */
                boolean isInChapterSelection = false;
                if(page == CHAPTER_SELECTION)
                    isInChapterSelection = true;
                else if(page == VERSE_SELECTION)
                    isInChapterSelection = false;

                //Update ViewPager's boolean tag
                verseIndexViewPager.setTag(isInChapterSelection);

                /*
                    If not in book selection page, set citation's boolean tag to true
                    (meaning not in book selection)
                */
                if(page != BOOK_SELECTION)
                    citation.setTag(true);
             }

            @Override
            public void onPageScrolled
                (int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        //Change ViewPager current page on page button clicks
        View.OnClickListener clickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                int page = 0;
                switch (view.getId())
                {
                    case R.id.verse_index_book:
                        page = BOOK_SELECTION;
                        break;

                    case R.id.verse_index_chapter:
                        page = CHAPTER_SELECTION;
                        break;

                    case R.id.verse_index_verse:
                        page = VERSE_SELECTION;
                        break;
                }

                verseIndexViewPager.setCurrentItem(page);

                //Toggle color of page buttons
                toggleVerseIndexPageButtonState(icons, labels, page, activeColor, normalColor);
            }
        };

        //Assign Click Listener for page buttons
        for(View verseIndexPageButton : verseIndexPageButtons)
            verseIndexPageButton.setOnClickListener(clickListener);

        //Initialize books list adapter
        final ArrayAdapter<String> booksAdapter = new ArrayAdapter<>
            (getContext(), R.layout.verse_index_books_item, R.id.verse_index_books_item, books);

        //Add search functionality to adapter
        bookSearch.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                booksAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        //Toggle keyboard when book search input has been focused
//        bookSearch.setOnFocusChangeListener(new View.OnFocusChangeListener()
//        {
//            @Override
//            public void onFocusChange(final View view, boolean hasFocus)
//            {
//                if(hasFocus)
//                    view.post(new Runnable()
//                    {
//                        @Override
//                        public void run()
//                        {
//                            toggleKeyboard(view, true);
//                        }
//                    });
//            }
//        });

        //Initialize numbers grid in drop down (chapters/verses)
        final GridView
            chapterGrid = verseIndexLayout.findViewById(R.id.verse_index_chapters_grid),
            versesGrid = verseIndexLayout.findViewById(R.id.verse_index_verses_grid);
        final TextView
            chaptersWarning = verseIndexLayout.findViewById(R.id.verse_index_chapters_warning),
            versesWarning = verseIndexLayout.findViewById(R.id.verse_index_verses_warning);

        //Initialize books list and its adapter
        ListView booksList = verseIndexLayout.findViewById(R.id.verse_index_books_list);
        booksList.setAdapter(booksAdapter);
        booksList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {

                String bookItem = adapterView.getItemAtPosition(i).toString();
                citation.setText("");
                citation.append(bookItem);
                selectedBook = bookItem;
                loadChaptersForBook();
                updateVerseIndexNumberGrid(chapterGrid, chaptersWarning, chapters);
                verseIndexViewPager.setCurrentItem(CHAPTER_SELECTION);
            }
        });

        //Setup adapter on item click listener
        AdapterView.OnItemClickListener numberGridClickListener =
            new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
                {
                    //if(has not selected selectedBook, show toast saying to select selectedBook first)

                    String
                        item = adapterView.getItemAtPosition(i).toString(),
                        stringToAppend = "";
                    stringToAppend += selectedBook + " ";
                    if((boolean) verseIndexViewPager.getTag())
                    {
                        stringToAppend += item + ":";
                        selectedChapter = item;
                        loadVersesForChapter();
                        updateVerseIndexNumberGrid(versesGrid, versesWarning, verses);
                        verseIndexViewPager.setCurrentItem(VERSE_SELECTION);
                    }
                    else
                    {
                        stringToAppend += selectedChapter + ":" + item;
                        verseIndex.dismiss();
                        verseText.requestFocus();
                    }

                    citation.setText("");
                    citation.append(stringToAppend);
                }
            };

        chapterGrid.setOnItemClickListener(numberGridClickListener);
        versesGrid.setOnItemClickListener(numberGridClickListener);
    }

    //Show verse index drop down
    private void showVerseIndex()
    {
        try
        {
            verseIndex.showAsDropDown(citation);
        }
        catch (WindowManager.BadTokenException exception) { exception.printStackTrace(); }
    }

    //Update number grid for chapters or verses
    private void updateVerseIndexNumberGrid(GridView numberGrid, TextView warningText, int count)
    {
        warningText.setVisibility(View.GONE);

        Integer[] numbers = new Integer[count];
        for(int i = 0; i < count; i++)
            numbers[i] = i+1;

        ArrayAdapter<Integer> adapter = new ArrayAdapter<>
            (getContext(), R.layout.verse_index_numbers_item, R.id.verse_index_item, numbers);

        numberGrid.setAdapter(adapter);
    }

    private void toggleVerseIndexPageButtonState
        (ImageView[] icons, TextView[] labels, int activeButton, int activeColor, int normalColor)
    {
        for(int i = 0; i < icons.length; i++)
        {
            int color;
            if(i == activeButton)
                color = activeColor;
            else color = normalColor;

            icons[i].setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
            labels[i].setTextColor(color);
        }
    }

    //Read verse_index_numbers.json
    private void loadVerseIndex()
    {
        try
        {
            InputStream inputStream = getActivity().getAssets().open("verse_index.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            verseIndexJson = new String(buffer, "UTF-8");
        }
        catch (IOException exception)
        {
            Log.e("loadVerseIndex()", "Cannot read verse_index_numbers.json");
            exception.printStackTrace();
        }
    }

    //Load (parse) list of books
    private void loadBooks()
    {
        books = new ArrayList<>();
        if(verseIndexJson != null)
        {
            try
            {
                booksArray = new JSONArray(verseIndexJson);
                for(int i = 0; i < booksArray.length(); i++)
                {
                    JSONObject item = new JSONObject(booksArray.getString(i));
                    books.add(item.keys().next());
                }
            }
            catch (JSONException e) { e.printStackTrace(); }
        }
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<>
//            (getContext(), android.R.layout.simple_list_item_1, books);
//        verseReference.setAdapter(adapter);
//        citation.setAdapter(adapter);
    }

    //Load (parse) chapters for selected book
    private void loadChaptersForBook()
    {
        if(booksArray != null)
        {
            try
            {
                List<Integer> chapters = new ArrayList<>();

                for(int i = 0; i < booksArray.length(); i++)
                {
                    //Parse chapters
                    JSONObject bookObjects = booksArray.getJSONObject(i);
                    if(bookObjects.keys().next().equals(selectedBook))
                    {
                        chaptersObjects = bookObjects
                            .getJSONObject(selectedBook);

                        Iterator<String> keys = chaptersObjects.keys();
                        int length = 0;
                        while(keys.hasNext())
                        {
                            keys.next();
                            length++;
                        }

                        //Create List of chapters
                        for(int j = 1; j <= length; j++)
                            chapters.add(j);
                    }
                }
                this.chapters = chapters.size();
            }
            catch (JSONException e) { e.printStackTrace(); }
        }
    }

    //Load (parse) verses for seleted chapter
    private void loadVersesForChapter()
    {
        if(chaptersObjects != null)
        {
            List<Integer> verses = new ArrayList<>();

            try
            {
                int length = chaptersObjects.getInt(selectedChapter);

                for(int i = 1; i <= length; i++)
                    verses.add(i);
            }
            catch (JSONException e) { e.printStackTrace(); }

            this.verses = verses.size();
        }
    }

    //Method to show/hide keyboard
    private void toggleKeyboard(View viewInFocus, boolean show)
    {
        InputMethodManager inputManager = (InputMethodManager) getActivity()
            .getSystemService(Context.INPUT_METHOD_SERVICE);

        IBinder windowToken = viewInFocus == null?
            getActivity().getCurrentFocus().getWindowToken() :
            viewInFocus.getWindowToken();

        if(show)
        {
            if(viewInFocus != null)
                inputManager.showSoftInput(viewInFocus, InputMethodManager.SHOW_FORCED);
        }
        else inputManager.hideSoftInputFromWindow(windowToken, 0);
    }

    private void showSnackbar(String message)
    {
        Snackbar.make(parent, message, Snackbar.LENGTH_SHORT).show();
    }
}
