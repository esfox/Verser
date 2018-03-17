package bible.verse.organizer.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

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

    public CategoriesAdapter(CategoriesListItemListener listener, List<Category> categories)
    {
        categoriesDefaultList = categories;
        this.listener = listener;

//        makeDummyItems();

        this.categories = categoriesDefaultList;
    }

    public void addCategory(Category category)
    {
        categories.add(category);
        notifyItemInserted(categories.size() - 1);
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

            Category category = new Category(name, drawable);
            category.setVerseCount(verseCount);
            categoriesDefaultList.add(category);
        }
    }
}
