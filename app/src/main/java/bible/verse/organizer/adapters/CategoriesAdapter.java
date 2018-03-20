package bible.verse.organizer.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import bible.verse.organizer.adapters.viewholders.CategoryViewHolder;
import bible.verse.organizer.interfaces.CategoriesListItemListener;
import bible.verse.organizer.objects.Category;
import bible.verse.organizer.organizer.R;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoryViewHolder> implements
    Filterable
{
    private List<Category>
        categoriesDefaultList,
        categories;

    private CategoriesListItemListener listener;

    public CategoriesAdapter(List<Category> categories, CategoriesListItemListener listener)
    {
        categoriesDefaultList = categories;
        this.listener = listener;

//        makeDummyItems();

        this.categories = categoriesDefaultList;
    }

    public void addCategory(final Context context)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        View newCategoryDialog = inflater.inflate(R.layout.dialog_new_category, null);

        newCategoryDialog.findViewById(R.id.new_category_icon)
            .setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Toast.makeText(context, "Change Icon", Toast.LENGTH_SHORT).show();
                }
            });

        final TextInputLayout newCategoryName = newCategoryDialog
            .findViewById(R.id.new_category_name);

        final AlertDialog dialog = new AlertDialog.Builder(context)
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
                                dialog.dismiss();

                                String iconIdentifier = context.getResources()
                                    .getResourceEntryName(R.drawable.temp_category_icon);
                                Category category = new Category(categoryName, iconIdentifier);
                                categories.add(category);

                                int lastIndex = categories.size() - 1;
                                notifyItemInserted(lastIndex);
                                listener.onAddCategory(category, lastIndex);
                            }
                        }
                    });
            }
        });
        dialog.show();
    }

    public void updateCategories(List<Category> categories)
    {
        categoriesDefaultList = categories;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new CategoryViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.category_item, parent, false), listener);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position)
    {
        holder.bind(categories.get(position));
    }

    @Override
    public int getItemCount()
    {
        return categories.size();
    }

    @Override
    public Filter getFilter()
    {
        return new Filter()
        {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence)
            {
                String query = charSequence.toString();
                if (query.isEmpty())
                    categories = categoriesDefaultList;
                else
                {
                    List<Category> filterList = new ArrayList<>();
                    for (Category category : categoriesDefaultList)
                    {
                        if (category.getName().toLowerCase().contains(query))
                            filterList.add(category);
                    }

                    categories = filterList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = categories;
                return filterResults;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults)
            {
                categories = (ArrayList<Category>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    private void makeDummyItems()
    {
        for (int i = 0; i < 20; i++)
        {
            String name = "Category " + String.valueOf(i + 1);
            int verseCount = (i + 1) * 10;
            int drawable = R.drawable.temp_category_icon;

            Category category = new Category(name, "temp_category_icon");
            category.setVerseCount(verseCount);
            categoriesDefaultList.add(category);
        }
    }
}
