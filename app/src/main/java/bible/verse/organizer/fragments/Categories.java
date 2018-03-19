package bible.verse.organizer.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import bible.verse.organizer.MainActivity;
import bible.verse.organizer.adapters.CategoriesAdapter;
import bible.verse.organizer.interfaces.CategoriesListItemListener;
import bible.verse.organizer.objects.Category;
import bible.verse.organizer.organizer.R;

//TODO: Saving to Database
public class Categories extends Fragment implements
    CategoriesListItemListener,
    View.OnClickListener
{
    private CategoriesAdapter adapter;
    private RecyclerView categoriesList;

    public Categories() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        View layout = inflater.inflate(R.layout.fragment_categories, container, false);

        //Setup AppBar
        Toolbar appBar = layout.findViewById(R.id.categories_toolbar);
        appBar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        //Initial text
        TextView initialText = layout.findViewById(R.id.categories_initial_text);

        //Setup Categories list
        categoriesList = layout.findViewById(R.id.categories_list);
        categoriesList.setHasFixedSize(true);
        categoriesList.setLayoutManager(new LinearLayoutManager(getContext()));
        categoriesList.setTag(initialText);

        //Get categories from database
        List<Category> categories = ((MainActivity) getActivity()).getCategories();

        //Hide initial text (no saved categories yet) when there are no categories
        if(categories.isEmpty())
            initialText.setVisibility(View.VISIBLE);

        //Set adapter to Categories list
        adapter = new CategoriesAdapter(this, categories);
        categoriesList.setAdapter(adapter);

        layout.findViewById(R.id.categories_add).setOnClickListener(this);

        return layout;
    }

    @Override
    public void onCategoryItemClick(Category category)
    {
        Snackbar.make(getView(), "Go to " + category.getName(), Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onCategoryAdd(Category category)
    {
        categoriesList.smoothScrollToPosition(0);
        ((MainActivity) getActivity()).saveCategory(category);

        TextView initialText = (TextView) categoriesList.getTag();
        if(initialText.getVisibility() == View.VISIBLE)
            initialText.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view)
    {
        switch(view.getId())
        {
            case R.id.categories_add:
                adapter.addCategory(getContext());
                break;

            case R.id.new_category_icon:
                Toast.makeText(getContext(), "Change Icon", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
