package bible.verse.organizer.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import bible.verse.organizer.adapters.CategoriesAdapter;
import bible.verse.organizer.interfaces.CategoriesListItemListener;
import bible.verse.organizer.objects.Category;
import bible.verse.organizer.organizer.R;

public class Categories extends Fragment implements
    CategoriesListItemListener,
    View.OnClickListener
{
    private CategoriesAdapter adapter;
    private RecyclerView categoriesList;

    public Categories() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
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

        //Setup Categories list
        categoriesList = layout.findViewById(R.id.categories_list);
        categoriesList.setHasFixedSize(true);
        categoriesList.setLayoutManager(new LinearLayoutManager(getContext()));

        //Set adapter to Categories list
        adapter = new CategoriesAdapter(this);
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
    public void onClick(View view)
    {
        switch(view.getId())
        {
            case R.id.categories_add:
                addNewCategory();
                break;

            case R.id.new_category_icon:
                Toast.makeText(getContext(), "Change Icon", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void addNewCategory()
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View newCategoryDialog = inflater.inflate(R.layout.dialog_new_category, null);

        newCategoryDialog.findViewById(R.id.new_category_icon)
            .setOnClickListener(this);

        final TextInputLayout newCategoryName = newCategoryDialog.findViewById(R.id.new_category_name);

        final AlertDialog dialog = new AlertDialog.Builder(getContext())
            .setTitle("Add New Category")
            .setView(newCategoryDialog)
            .setPositiveButton("Done", null)
            .setNegativeButton("Cancel", null)
            .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener()
        {
            @Override
            public void onShow(DialogInterface dialogInterface)
            {
                dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                    .setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            String categoryName = newCategoryName
                                .getEditText().getText().toString();

                            if(categoryName.equals(""))
                                newCategoryName.setError("Please enter a name for the category.");
                            else
                            {
                                adapter.addCategory(new Category
                                    (categoryName, R.drawable.temp_category_icon));
                                categoriesList.smoothScrollToPosition(0);
                                dialog.dismiss();
                            }
                        }
                    });
            }
        });
        dialog.show();

    }
}
