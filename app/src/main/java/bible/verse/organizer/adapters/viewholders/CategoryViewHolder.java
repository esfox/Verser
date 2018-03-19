package bible.verse.organizer.adapters.viewholders;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.RecyclerView;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import bible.verse.organizer.interfaces.CategoriesListItemListener;
import bible.verse.organizer.objects.Category;
import bible.verse.organizer.organizer.R;

public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    private TextView name, verseCount;
    private ImageView icon, options;

    private Category category;

    private CategoriesListItemListener listener;

    public CategoryViewHolder(View itemView, CategoriesListItemListener listener)
    {
        super(itemView);

        this.listener = listener;

        name = itemView.findViewById(R.id.category_item_name);
        verseCount = itemView.findViewById(R.id.category_item_verse_count);
        icon = itemView.findViewById(R.id.category_item_icon);
        options = itemView.findViewById(R.id.category_item_options);

        itemView.setOnClickListener(this);
        options.setOnClickListener(this);
    }

    @SuppressLint("SetTextI18n")
    public void bind(Category category)
    {
        this.category = category;

        name.setText(category.getName());
        verseCount.setText(String.valueOf(category.getVerseCount()) + " verses");

        String packageName = itemView.getContext().getPackageName();
        Resources resources = itemView.getContext().getResources();
        int iconID = resources.getIdentifier
            (category.getIconIdentifier(), "drawable", packageName);
        icon.setImageResource(iconID);
    }

    @Override
    public void onClick(View view)
    {
        switch(view.getId())
        {
            case R.id.category_item_options:
                options();
                break;

            default:
                clickCallback();
                break;
        }
    }

    private void clickCallback()
    {
        listener.onCategoryItemClick(category);
    }

    @SuppressLint("RestrictedApi")
    private void options()
    {
        MenuBuilder.Callback callback = new MenuBuilder.Callback()
        {
            @Override
            public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item)
            {
                switch(item.getItemId())
                {
                    case R.id.category_item_options_edit:
                        Toast.makeText(itemView.getContext(),
                            "Edit " + category.getName(), Toast.LENGTH_SHORT).show();
                        return true;

                    case R.id.category_item_options_delete:
                        Toast.makeText(itemView.getContext(),
                            "Delete " + category.getName(), Toast.LENGTH_SHORT).show();
                        return true;

                    default:
                        return false;
                }
            }

            @Override public void onMenuModeChange(MenuBuilder menu) {}
        };

        MenuBuilder menuBuilder = new MenuBuilder(itemView.getContext());
        MenuInflater inflater = new MenuInflater(itemView.getContext());
        inflater.inflate(R.menu.category_item_options, menuBuilder);

        MenuPopupHelper popupHelper = new MenuPopupHelper
                (itemView.getContext(), menuBuilder, options);
        popupHelper.setForceShowIcon(true);
        popupHelper.show();
        menuBuilder.setCallback(callback);
    }
}
