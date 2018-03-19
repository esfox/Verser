package bible.verse.organizer.fragments;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bible.verse.organizer.MainActivity;
import bible.verse.organizer.adapters.CategoriesAdapter;
import bible.verse.organizer.adapters.VerseIndexPageAdapter;
import bible.verse.organizer.interfaces.CategoriesListItemListener;
import bible.verse.organizer.interfaces.OnBackPressListener;
import bible.verse.organizer.interfaces.VerseWebRequestListener;
import bible.verse.organizer.objects.Category;
import bible.verse.organizer.objects.Verse;
import bible.verse.organizer.organizer.R;
import bible.verse.organizer.utilities.Color;
import bible.verse.organizer.utilities.VerseWebRequest;

//TODO: Tag implementation
public class NewVerse extends Fragment implements
    View.OnClickListener,
    OnBackPressListener,
    VerseWebRequestListener
{
    //Fields of views where data is taken from
    private EditText
            citation,
            verseText,
            notesInput;

    //Verse index drop-down
    private PopupWindow verseIndex;

    //Hidden views (categories, notes & tags)
    private View
            notesView,
            categoriesView,
            tagsView;

    private ProgressBar verseTextProgressBar;

    //Screen height (set hidden views y position to this to make them hidden)
    private float screenHeight;

    private String
            verseIndexJson,
            selectedBook,
            selectedChapter;

    private List<String> books;
    private int
            chapters,
            verses;

    //Boolean fields for hidden views to capture back button
    private boolean
            isChoosingCategory,
            isSelectingTags,
            isEditingNotes;

    /*
        Save JSON classes for faster parsing.
        Number of chapters are parsed only when a book is selected
        and number of verses are parsed only when a chapter is selected.
    */
    private JSONArray booksArray;
    private JSONObject chaptersObject;

    //Fields of data needed that should be fields
    private String title;
    private boolean isFavorite;
    private Category category;

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

        screenHeight = getResources().getDisplayMetrics().heightPixels;

        //Setup AppBar
        Toolbar appBar = layout.findViewById(R.id.new_verse_toolbar);
        appBar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                cancel();
            }
        });

        //Setup citation EditText
        setupInputFields(layout);

        //Setup verse index drop down
        setupVerseIndexDropDown();

        //Initialize views to be used as buttons
        View
            addTitle = layout.findViewById(R.id.new_verse_title),
            addCategory = layout.findViewById(R.id.new_verse_category),
            addTags = layout.findViewById(R.id.new_verse_tags),
            addNotes = layout.findViewById(R.id.new_verse_notes),
            markAsFavorite = layout.findViewById(R.id.new_verse_favorite);

        FloatingActionButton doneButton = layout.findViewById(R.id.new_verse_done);

        //Create array of the views to be used as buttons
        View[] buttons =
            {
                citation,
                addTitle,
                addCategory,
                addTags,
                addNotes,
                markAsFavorite,
                doneButton
            };

        //Loop through array to assign click listeners
        for (View button : buttons)
            button.setOnClickListener(this);

        //Setup layout for selecting categories
        setupCategoriesView(layout, addCategory);

        //Setup layout for selecting tags
        setupTagsView(layout);

        //Setup layout for editing notes
        setupNotesView(layout);

        return layout;
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.new_verse_citation_input:
                showVerseIndex();
                break;

            case R.id.new_verse_title:
                addTitle(view);
                break;

            case R.id.new_verse_category:
                toggleCategoriesView(true);
                break;

            case R.id.new_verse_tags:
                toggleTagsView(true);
                break;

            case R.id.new_verse_notes:
                toggleNotesView(true);
                break;

            case R.id.new_verse_favorite:
                markAsFavorite(view);
                break;

            case R.id.new_verse_done:
                addVerse();
                break;

            case R.id.new_verse_categories_cancel:
                toggleCategoriesView(false);
                break;

            case R.id.new_verse_notes_done:
                toggleNotesView(false);
                break;

            case R.id.new_verse_tags_done:
                toggleTagsView(false);
                break;
        }
    }

    @Override
    public boolean onBackPressed()
    {
        if (isEditingNotes)
            toggleNotesView(false);
        else if (isChoosingCategory)
            toggleCategoriesView(false);
        else if (isSelectingTags)
            toggleTagsView(false);
        else
            cancel();
        return true;
    }

    @Override
    public void onRequestResponse(Verse verse)
    {
        verseTextProgressBar.setVisibility(View.GONE);
        verseText.setText(verse.getVerseText());
    }

    //Setup verse citation and verse text EditTexts
    private void setupInputFields(View layout)
    {
        citation = layout.findViewById(R.id.new_verse_citation_input);
        citation.setTag(layout.findViewById(R.id.new_verse_citation));
        citation.setKeyListener(null);
        citation.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View view, boolean hasFocus)
            {
                if (hasFocus)
                {
                    toggleKeyboard(null, false);
                    citation.callOnClick();
                }
            }
        });

        citation.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View view)
            {
                requestVerse();
                return true;
            }
        });

        /*
            TODO: Add smart functionality where if a valid verse citation
            (RegEx - "(\d\s)?([a-zA-Z]+\s*){1,3}\s\d{1,3}:\d{1,3}") is included
            in the text, parse it and assign it to citation EditText.
         */
        verseText = layout.findViewById(R.id.new_verse_text_input);
        verseTextProgressBar = layout.findViewById(R.id.new_verse_text_progress_bar);

        TextWatcher textWatcher = new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                if (citation.hasFocus())
                    ((TextInputLayout) citation.getTag()).setErrorEnabled(false);
                else if (verseText.hasFocus())
                    verseText.setError(null);
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
            }

            @Override
            public void afterTextChanged(Editable editable)
            {
            }
        };

        citation.addTextChangedListener(textWatcher);
        verseText.addTextChangedListener(textWatcher);
    }

    //TODO: Make custom view of verse index drop down
    //Setup verse index drop down
    private void setupVerseIndexDropDown()
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        final View verseIndexLayout = inflater.inflate(R.layout.verse_index, null);

        //Initializing page buttons
        final View
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
        final ImageView[] icons = new ImageView[]{bookIcon, chapterIcon, verseIcon};
        final TextView[] labels = new TextView[]{bookLabel, chapterLabel, verseLabel};

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
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
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
        VerseIndexPageAdapter pagerAdapter = new VerseIndexPageAdapter();
        for (View pageLayout : pageLayouts)
            pagerAdapter.addPage(pageLayout);
        verseIndexViewPager.setAdapter(pagerAdapter);

        /*
            Set boolean tag to ViewPager to track if user is in chapter selection
            (To know what adapter to change, whether chapters or verses)
        */
        verseIndexViewPager.setTag(false);

        //Colors for the states of the page buttons (buttons on bottom)
        final int
            activeColor = Color.getColor(getContext(), R.attr.colorAccent),
            normalColor = Color.getColor(getContext(), android.R.attr.textColorSecondary);

        //Set book button to selected state (active color)
        icons[0].setColorFilter(activeColor, PorterDuff.Mode.SRC_ATOP);
        labels[0].setTextColor(activeColor);

        verseIndexViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageSelected(int page)
            {
                //Toggle color of page buttons
                toggleVerseIndexPageButtonState(icons, labels, page, activeColor, normalColor);

                //Reset book search when going back to book selection page
                if (page == BOOK_SELECTION)
                    bookSearch.setText("");
                else
                    toggleKeyboard(null, false);

                /*
                    Set boolean tag of ViewPager and citation EditText to indicate
                    what is currently being selected (if chapter or verse).
                 */
                boolean isInChapterSelection = false;
                if (page == CHAPTER_SELECTION)
                    isInChapterSelection = true;
                else if (page == VERSE_SELECTION)
                    isInChapterSelection = false;

                //Update ViewPager's boolean tag
                verseIndexViewPager.setTag(isInChapterSelection);
            }

            @Override
            public void onPageScrolled
                    (int position, float positionOffset, int positionOffsetPixels)
            {
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {
            }
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
        for (View verseIndexPageButton : verseIndexPageButtons)
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
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
            }

            @Override
            public void afterTextChanged(Editable editable)
            {
            }
        });

        //Initialize numbers grid in drop down (chapters/verses)
        final GridView
                chapterGrid = verseIndexLayout.findViewById(R.id.verse_index_chapters_grid),
                versesGrid = verseIndexLayout.findViewById(R.id.verse_index_verses_grid);
        final TextView
                chaptersWarning = verseIndexLayout.findViewById(R.id.verse_index_chapters_warning),
                versesWarning = verseIndexLayout.findViewById(R.id.verse_index_verses_warning);
        final Button
            versesDoneButton = verseIndexLayout.findViewById(R.id.verse_index_verses_done),
            versesClearButton = verseIndexLayout.findViewById(R.id.verse_index_verses_clear);

        //Initialize books list and its adapter
        ListView booksList = verseIndexLayout.findViewById(R.id.verse_index_books_list);
        booksList.setAdapter(booksAdapter);
        booksList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {

                toggleKeyboard(bookSearch, false);
                String bookItem = adapterView.getItemAtPosition(i).toString();
                citation.setText("");
                citation.append(bookItem);
                selectedBook = bookItem;
                loadChaptersForBook();
                updateVerseIndexNumberGrid(chapterGrid, chaptersWarning, chapters);
                verseIndexViewPager.setCurrentItem(CHAPTER_SELECTION);
            }
        });

        //Initialize list for storing selected verses
        final List<Integer> selectedVerses = new ArrayList<>();

        //Colors for item states
        final int
            itemNormalColor = Color.getColor(getContext(), android.R.color.transparent),
            itemSelectedColor = Color.getColor(getContext(), R.attr.colorAccent),
            textColorSecondary = Color.getColor(getContext(), android.R.attr.textColorSecondary),
            textColorLight= Color.getColor(getContext(), R.attr.textColorLight);

        //Setup adapter on item click listener
        AdapterView.OnItemClickListener numberGridClickListener =
            new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
                {
                    String
                        item = adapterView.getItemAtPosition(i).toString(),
                        stringToAppend = selectedBook + " ";
                    if ((boolean) verseIndexViewPager.getTag())
                    {
                        stringToAppend += item + ":";
                        selectedChapter = item;
                        loadVersesForChapter();
                        verseIndexViewPager.setCurrentItem(VERSE_SELECTION);
                        selectedVerses.clear();
                        updateVerseIndexNumberGrid(versesGrid, versesWarning, verses);

                        citation.setText("");
                        citation.append(stringToAppend);
                    }
                    else
                    {
                        TextView itemView = ((TextView) view);
                        boolean itemIsSelected;
                        if(itemView.getTag() == null)
                            itemIsSelected = false;
                        else
                            itemIsSelected = (Boolean) itemView.getTag();

                        itemView.setBackgroundColor
                            (itemIsSelected? itemNormalColor : itemSelectedColor);
                        itemView.setTextColor
                            (itemIsSelected? textColorSecondary : textColorLight);

                        itemView.setTag(!itemIsSelected);

                        int itemNumber = Integer.parseInt(item);
                        if(!itemIsSelected)
                            selectedVerses.add(itemNumber);
                        else
                            selectedVerses.remove(selectedVerses.indexOf(itemNumber));
                    }
                }
            };

        chapterGrid.setOnItemClickListener(numberGridClickListener);
        versesGrid.setOnItemClickListener(numberGridClickListener);

        View.OnClickListener versesButtonListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String stringToAppend = selectedBook + " " + selectedChapter + ":";

                switch(view.getId())
                {
                    case R.id.verse_index_verses_done:
                        if(selectedVerses.isEmpty())
                        {
                            Toast.makeText(getContext(), "No verse selected",
                                Toast.LENGTH_SHORT).show();
                            break;
                        }

                        stringToAppend += getSelectedVerses(selectedVerses);
                        verseIndex.dismiss();
                        break;

                    case R.id.verse_index_verses_clear:
                        selectedVerses.clear();
                        updateVerseIndexNumberGrid(versesGrid, versesWarning, verses);
                        break;
                }

                citation.setText("");
                citation.append(stringToAppend);
            }
        };

        versesDoneButton.setOnClickListener(versesButtonListener);
        versesClearButton.setOnClickListener(versesButtonListener);

        //TODO: Long press selects range of verses
//        versesGrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
//        {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l)
//            {
//                String
//                    item = adapterView.getItemAtPosition(i).toString(),
//                    stringToAppend = selectedBook + " " + selectedChapter + ":" ;
//
//                fromVerse = item + "-";
//                stringToAppend += fromVerse;
//
//                citation.setText("");
//                citation.append(stringToAppend);
//                return true;
//            }
//        });
    }

    //Setup layout for selecting category
    private void setupCategoriesView(final View layout, final View button)
    {
        //Button label and icon
        final TextView label = button.findViewById(R.id.new_verse_category_label);
        final ImageView icon = button.findViewById(R.id.new_verse_category_icon);

        //Clear category button
        final View clearButton = layout.findViewById(R.id.new_verse_category_clear);

        //Categories view layout
        categoriesView = layout.findViewById(R.id.new_verse_categories_view);

        //Set view y position to be the screen height (hidden below screen)
        categoriesView.setY(screenHeight);

        //Initial text (no saved categories yet)
        final TextView initialText = layout.findViewById(R.id.new_verse_categories_initial_text);

        //Toolbar & Search EditText ViewSwitcher
        final ViewSwitcher viewSwitcher = categoriesView.findViewById
            (R.id.new_verse_categories_viewswitcher);

        //Set ViewSwitcher as tag to categoriesView to be used when toggling it
        categoriesView.setTag(viewSwitcher);

        //Category search EditText
        final EditText searchInput = categoriesView.findViewById
            (R.id.new_verse_categories_search_input);

        //Setup categories list
        final RecyclerView categoriesList = layout.findViewById(R.id.new_verse_categories_list);
        categoriesList.setHasFixedSize(true);
        categoriesList.setLayoutManager(new LinearLayoutManager(getContext()));

        //Setup categories adapter and listener for the adapter
        CategoriesListItemListener listener = new CategoriesListItemListener()
        {
            @Override
            public void onCategoryItemClick(Category category)
            {
                NewVerse.this.category = category;

                toggleCategoriesView(false);
                label.setText(category.getName());

                int iconID = getResources().getIdentifier
                    (category.getIconIdentifier(), "drawable",
                        getContext().getPackageName());
                icon.setImageResource(iconID);
                clearButton.setVisibility(View.VISIBLE);

                if(!searchInput.getText().toString().equals(""))
                    searchInput.setText("");

                toggleKeyboard(searchInput, false);
            }

            @Override
            public void onCategoryAdd(Category category)
            {
                categoriesList.smoothScrollToPosition(0);
                ((MainActivity) getActivity()).saveCategory(category);
                if(initialText.getVisibility() == View.VISIBLE)
                    initialText.setVisibility(View.GONE);
            }
        };

        //Get categories from Database
        List<Category> categories =  ((MainActivity) getActivity()).getCategories();

        //Hide initial text (no saved categories yet) when there are no categories
        if(categories.isEmpty())
            initialText.setVisibility(View.VISIBLE);

        //Category list adapter
        final CategoriesAdapter adapter = new CategoriesAdapter(listener, categories);
        categoriesList.setAdapter(adapter);

        //Search functionality
        searchInput.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                adapter.getFilter().filter(charSequence);
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        //Click listener for categories-related buttons
        final View.OnClickListener onClickListener = new View.OnClickListener()
        {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view)
            {

                //Used for resetting ViewSwitcher to default when categoriesView hidden
                boolean isSearchingCategory = false;

                switch(view.getId())
                {
                    case R.id.new_verse_category_clear:
                        category = null;
                        clearButton.setVisibility(View.GONE);
                        label.setText("Add Category");
                        icon.setImageResource(R.drawable.category);
                        break;

                    case R.id.new_verse_categories_search:
                        viewSwitcher.setInAnimation(getContext(), R.anim.viewswitcher_in);
                        viewSwitcher.setOutAnimation(getContext(), R.anim.viewswitcher_out);
                        viewSwitcher.showNext();
                        isSearchingCategory = true;

                        searchInput.requestFocus();
                        toggleKeyboard(searchInput, true);
                        break;

                    case R.id.new_verse_categories_search_cancel:
                        viewSwitcher.setInAnimation(getContext(), R.anim.viewswitcher_out_reverse);
                        viewSwitcher.setOutAnimation(getContext(), R.anim.viewswitcher_in_reverse);
                        viewSwitcher.showPrevious();
                        isSearchingCategory = false;

                        toggleKeyboard(null, false);

                        if(!searchInput.getText().toString().equals(""))
                            searchInput.setText("");
                        break;

                    case R.id.new_verse_categories_add:
                        adapter.addCategory(getContext());
                        break;
                }

                //Set boolean as tag to ViewSwitcher
                viewSwitcher.setTag(isSearchingCategory);
            }
        };

        clearButton.setOnClickListener(onClickListener);

        //Search category button
        categoriesView.findViewById(R.id.new_verse_categories_search)
            .setOnClickListener(onClickListener);

        //Category search cancel button
        categoriesView.findViewById(R.id.new_verse_categories_search_cancel)
            .setOnClickListener(onClickListener);

        //Category add button
        categoriesView.findViewById(R.id.new_verse_categories_add)
            .setOnClickListener(onClickListener);

        //Enable dragging the toolbar
        SwipeTouchListener swipeTouchListener = new SwipeTouchListener(categoriesView);
        viewSwitcher.setOnTouchListener(swipeTouchListener);

        //Set on click listener for cancel button
        categoriesView.findViewById(R.id.new_verse_categories_cancel)
            .setOnClickListener(this);
    }

    //Setup layout for selecting tags
    private void setupTagsView(View layout)
    {
        tagsView = layout.findViewById(R.id.new_verse_tags_view);

        //Set view y position to be the screen height (hidden below screen)
        tagsView.setY(screenHeight);

        //Enable dragging the toolbar
        SwipeTouchListener swipeTouchListener = new SwipeTouchListener(tagsView);
        tagsView.setOnTouchListener(swipeTouchListener); //TEMPORARY
        tagsView.findViewById(R.id.new_verse_tags_toolbar)
            .setOnTouchListener(swipeTouchListener);

        //Set on click listener for done button
        tagsView.findViewById(R.id.new_verse_tags_done)
            .setOnClickListener(this);
    }

    //Setup layout for editing notes, put the view below the screen
    private void setupNotesView(View layout)
    {
        notesView = layout.findViewById(R.id.new_verse_notes_view);
        notesInput = notesView.findViewById(R.id.new_verse_notes_input);

        //Set view y position to be the screen height (hidden below screen)
        notesView.setY(screenHeight);

        //Set the button label as tag to be used in toggleNotesView (to change its text)
        notesView.setTag(layout.findViewById(R.id.new_verse_notes_label));

        //Enable dragging the view
        SwipeTouchListener swipeTouchListener = new SwipeTouchListener(notesView);
        notesInput.setOnTouchListener(swipeTouchListener);
        notesView.findViewById(R.id.new_verse_notes_toolbar)
            .setOnTouchListener(swipeTouchListener);

        //Set on click listener for done button
        notesView.findViewById(R.id.new_verse_notes_done)
            .setOnClickListener(this);
    }

    //Cancel confirmation dialog
    private void cancel()
    {
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

    private void addTitle(final View button)
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        final View titleInputLayout = inflater.inflate(R.layout.dialog_new_verse_add_title, null);
        final TextInputLayout titleInput =
            titleInputLayout.findViewById(R.id.new_verse_add_title_input);

        if(title != null)
        {
            titleInput.getEditText().setText("");
            titleInput.getEditText().append(title);
        }

        final AlertDialog dialog = new AlertDialog.Builder(getContext())
            .setTitle("Add a Title")
            .setView(titleInputLayout)
            .setPositiveButton("Done", null)
            .setNeutralButton("Remove Title", null)
            .setNegativeButton("Cancel", null)
            .create();

        final TextView label = button.findViewById(R.id.new_verse_title_label);
        dialog.setOnShowListener(new DialogInterface.OnShowListener()
        {
            @Override
            public void onShow(DialogInterface dialogInterface)
            {
                Button
                    done = dialog.getButton(DialogInterface.BUTTON_POSITIVE),
                    removeTitle = dialog.getButton(DialogInterface.BUTTON_NEUTRAL);

                done.setTag(1);
                removeTitle.setTag(2);

                removeTitle.setVisibility(title == null? View.GONE : View.VISIBLE);

                if(title != null)
                    removeTitle.setVisibility(title.equals("")? View.GONE : View.VISIBLE);

                View.OnClickListener listener = new View.OnClickListener()
                {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onClick(View view)
                    {
                        switch((Integer) view.getTag())
                        {
                            case 1:

                                title = titleInput.getEditText().getText().toString();
                                if(title.equals(""))
                                    titleInput.setError("Please enter a title.");
                                else
                                {
                                    label.setText(title);
                                    label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);
                                    dialog.dismiss();
                                }
                                break;

                            case 2:
                                title = "";
                                label.setText("Add a Title");
                                label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                                dialog.dismiss();
                                break;
                        }
                    }
                };

                done.setOnClickListener(listener);
                removeTitle.setOnClickListener(listener);
            }
        });
        dialog.show();
    }

    private void toggleCategoriesView(final boolean toggle)
    {
        isChoosingCategory = toggle;

        toggleHiddenView(categoriesView, toggle);

        if(toggle)
            return;

        try
        {
            ViewSwitcher viewSwitcher = (ViewSwitcher) categoriesView.getTag();
            boolean isSearchingCategory = (boolean) viewSwitcher.getTag();
            if(isSearchingCategory)
            {
                viewSwitcher.showNext();
                viewSwitcher.setTag(false);
            }
        }
        catch (NullPointerException ignored) {}
    }

    private void toggleTagsView(final boolean toggle)
    {
        isSelectingTags = toggle;

        toggleHiddenView(tagsView, toggle);

        //TODO: change add tags button view (horizontally scrolling list of selected tags)
    }

    private void toggleNotesView(final boolean toggle)
    {
        isEditingNotes = toggle;

        toggleHiddenView(notesView, toggle);

        if(toggle)
        {
            notesInput.requestFocus();
            toggleKeyboard(notesInput, true);
        }
        else
            toggleKeyboard(null, false);

        TextView buttonLabel = (TextView) notesView.getTag();
        boolean notesInputIsEmpty = notesInput.getText().toString().equals("");
        buttonLabel.setText(notesInputIsEmpty? "Add Notes" : "Edit Notes");
    }

    private void markAsFavorite(View button)
    {
        isFavorite = !isFavorite;

        ImageView favoriteIcon = button.findViewById(R.id.new_verse_favorite_icon);
        TextView favoriteLabel = button.findViewById(R.id.new_verse_favorite_label);

        int favoriteColor = ContextCompat.getColor(getContext(), R.color.favoriteColor),
            normalColor = Color.getColor(getContext(), android.R.attr.textColorSecondary);

        int icon = isFavorite? R.drawable.favorite : R.drawable.favorite_outlined;
        int iconTint = isFavorite? favoriteColor : normalColor;
        String label = isFavorite? "Marked as Favorite!" : "Mark as Favorite";

        favoriteIcon.setImageResource(icon);
        favoriteIcon.setColorFilter(iconTint, PorterDuff.Mode.SRC_ATOP);
        favoriteLabel.setText(label);
    }

    private void toggleHiddenView(final View hiddenView, final boolean toggle)
    {
        float translateTo = 0;
        if(!toggle)
            translateTo = screenHeight;

        hiddenView
            .animate()
            .y(translateTo)
            .setDuration(600)
            .setInterpolator(new DecelerateInterpolator(3))
            .setListener(new Animator.AnimatorListener()
            {
                @Override
                public void onAnimationStart(Animator animator)
                {
                    if(toggle)
                        hiddenView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animator)
                {
                    if(!toggle)
                        hiddenView.setVisibility(View.GONE);
                }

                @Override public void onAnimationCancel(Animator animator) {}
                @Override public void onAnimationRepeat(Animator animator) {}
            })
            .start();
    }

    //When user presses Done button
    private void addVerse()
    {
        final String
            citation = this.citation.getText().toString(),
            verseText = this.verseText.getText().toString(),
            notes = notesInput.getText().toString();

        boolean citationValidated = !citation.equals("");
        TextInputLayout citationParent = (TextInputLayout) this.citation.getTag();
        if(citationValidated)
        {
            citationValidated = citationIsValid(citation);
            if(citationValidated)
                citationParent.setErrorEnabled(false);
            else
                citationParent.setError("This is not a correct verse citation");
        }
        else
            citationParent.setError("Please enter the verse citation");

        boolean verseTextValidated = !verseText.equals("");
        if(verseTextValidated)
            this.verseText.setError(null);
        else
            this.verseText.setError("Please enter the verse text");

        if(!citationValidated || !verseTextValidated)
            return;

//        d_showVerseDetails();

        new AlertDialog.Builder(getContext())
            .setTitle("Save this verse?")
            .setMessage("Are you done adding details about this verse?")
            .setPositiveButton("Yes", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    Verse verse = new Verse();
                    verse.setId(UUID.randomUUID().toString());
                    verse.setVerse(citation);
                    verse.setVerseText(verseText);
                    verse.setTags(new String[] { "tag1", "tag2", "tag3" });
                    verse.setTitle(title);
                    verse.setNotes(notes);
                    verse.setFavorited(isFavorite);

                    if(category != null)
                        verse.setCategory(category);

                    ((MainActivity) getActivity()).saveVerse(verse);
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            })
            .setNegativeButton("No", null)
            .show();
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
                        chaptersObject = bookObjects
                            .getJSONObject(selectedBook);

                        Iterator<String> keys = chaptersObject.keys();
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
        if(chaptersObject != null)
        {
            List<Integer> verses = new ArrayList<>();

            try
            {
                int length = chaptersObject.getInt(selectedChapter);

                for(int i = 1; i <= length; i++)
                    verses.add(i);
            }
            catch (JSONException e) { e.printStackTrace(); }

            this.verses = verses.size();
        }
    }

    //Getting the verse from a web api
    private void requestVerse()
    {
        String citationString = citation.getText().toString();
        if(citationString.equals(""))
            return;

//        String versesString = citationString.substring
//            (citationString.indexOf(":") + 1, citationString.length());
//        BibleUtils.getVerse(selectedBook, selectedChapter, versesString);

        verseTextProgressBar.setVisibility(View.VISIBLE);
        String url = "http://labs.bible.org/api/?type=json&formatting=plain&passage="
                + citationString.replaceAll("\\s", "%20");
        new VerseWebRequest(NewVerse.this).execute(url);
    }

    //Check if citation is valid citation
    private boolean citationIsValid(String input)
    {
        String verseRegex = "^(\\d\\s)?([a-zA-Z]+\\s*){1,3}(\\d{1,3}:\\d{1,3})(-\\d{1,3})?$";
        Pattern citationPattern = Pattern.compile(verseRegex);
        Matcher citationMatcher = citationPattern.matcher(input);
        return citationMatcher.matches();
    }

    private String getSelectedVerses(List<Integer> selectedVerses)
    {
        Collections.sort(selectedVerses);

        String selectedVersesString = "";
        ArrayList<Integer> temp = new ArrayList<>();

        for(int i = 0; i < selectedVerses.size(); i++)
        {
            int current = selectedVerses.get(i);

            temp.add(current);
            if(i + 1 < selectedVerses.size())
            {
                if(Math.abs(selectedVerses.get(i + 1) - current) == 1)
                    continue;
            }

            if(temp.size() == 1)
            {
                selectedVersesString += temp.get(0) + ", ";
                temp.clear();
            }
            else
            {
                String min = String.valueOf(Collections.min(temp));
                String max = String.valueOf(Collections.max(temp));
                selectedVersesString += min + "-" + max + ", ";
                temp.clear();
            }
        }

        selectedVersesString = selectedVersesString.replaceAll(", $", "");
        return selectedVersesString;
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

    //FOR DEBUGGING ONLY
    private void d_showVerseDetails()
    {
        final String
            citation = this.citation.getText().toString(),
            verseText = this.verseText.getText().toString(),
            notes = notesInput.getText().toString();

        boolean citationValidated = !citation.equals("");
        TextInputLayout citationParent = (TextInputLayout) this.citation.getTag();
        if(citationValidated)
        {
            citationValidated = citationIsValid(citation);
            if(citationValidated)
                citationParent.setErrorEnabled(false);
            else
                citationParent.setError("This is not a correct verse citation");
        }
        else
            citationParent.setError("Please enter the verse citation");

        boolean verseTextValidated = !verseText.equals("");
        if(verseTextValidated)
            this.verseText.setError(null);
        else
            this.verseText.setError("Please enter the verse text");

        if(!citationValidated || !verseTextValidated)
            return;

        Verse verse = new Verse();
        verse.setVerse(citation);
        verse.setVerseText(verseText);
        verse.setTags(new String[] { "tag1", "tag2", "tag3" });
        verse.setTitle(title);
        verse.setNotes(notes);
        verse.setFavorited(isFavorite);

        if(category != null)
            verse.setCategory(category);

        String message =
            "Citation: " + citation + "\n" +
            "Verse Text: " + verseText + "\n" +
            "Category: " + category.getName() + "\n" +
            "Title: " + title + "\n" +
            "Notes: " + notes + "\n" +
            "Marked as favorite: " + isFavorite;

        new AlertDialog.Builder(getContext())
            .setTitle("Verse Details")
            .setMessage(message)
            .setPositiveButton("Done", null)
            .show();
    }

    //OnTouchListener that moves/swipes view
    private class SwipeTouchListener implements View.OnTouchListener
    {
        private View viewToMove;

        SwipeTouchListener(View viewToMove)
        {
            this.viewToMove = viewToMove;
        }

        private float downY, previousY, fromY;
        private boolean closed;

        @Override
        public boolean onTouch(View view, MotionEvent event)
        {
            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    if(view instanceof EditText)
                    {
                        view.requestFocus();
                        toggleKeyboard(view, true);
                    }

                    downY = event.getRawY();
                    previousY = downY;
                    fromY = viewToMove.getY();
                    break;

                case MotionEvent.ACTION_MOVE:
                    float rawY = event.getRawY();
                    closed = rawY < previousY;
                    previousY = rawY;

                    float y = event.getRawY() - downY;
                    float newPosition = fromY + y;

                    TypedValue value = new TypedValue();
                    if(getActivity().getTheme()
                        .resolveAttribute(R.attr.actionBarSize, value, true))
                    {
                        int bound = TypedValue.complexToDimensionPixelSize
                            (value.data, getResources().getDisplayMetrics());
                        if(newPosition >= bound)
                            viewToMove.animate()
                                .y(newPosition)
                                .setDuration(0)
                                .start();
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    if(downY != event.getRawY())
                    {
                        if(viewToMove == notesView)
                            toggleNotesView(closed);
                        else if(viewToMove == categoriesView)
                            toggleCategoriesView(closed);
                        else if(viewToMove == tagsView)
                            toggleTagsView(closed);
                    }
                    break;
            }
            return true;
        }
    }
}
