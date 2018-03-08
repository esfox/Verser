package bible.verse.organizer.adapters.viewholders;

import android.annotation.SuppressLint;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import bible.verse.organizer.interfaces.TagsListItemListener;
import bible.verse.organizer.objects.Tag;
import bible.verse.organizer.organizer.R;

public class TagViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    private TextView name, verseCount;
    private ImageView color, options;

    private Tag tag;

//    private TagsListItemListener listener;

    public TagViewHolder(View itemView, TagsListItemListener listener)
    {
        super(itemView);

//        this.listener = listener;

        name = itemView.findViewById(R.id.tags_item_tag);
        verseCount = itemView.findViewById(R.id.tags_item_count);
        options = itemView.findViewById(R.id.tags_item_options);
        color = itemView.findViewById(R.id.tags_item_color_indicator);

        itemView.setOnClickListener(this);
        options.setOnClickListener(this);
    }

    @SuppressLint("SetTextI18n")
    public void bind(Tag tag)
    {
        this.tag = tag;

        name.setText(tag.getName());
        color.setColorFilter(tag.getColor());
        verseCount.setText(String.valueOf(tag.getVerseCount()) + " verses");
    }

    @Override
    public void onClick(View view)
    {
        switch(view.getId())
        {
            case R.id.tags_item_options:
                options();
                break;

            default:
                clickCallback();
                break;
        }
    }

    private void clickCallback()
    {
//        listener.onTagItemClick(tag);
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
                    case R.id.tag_item_options_edit:
                        Toast.makeText(itemView.getContext(),
                                "Edit " + tag.getName(), Toast.LENGTH_SHORT).show();
                        return true;

                    case R.id.tag_item_options_delete:
                        Toast.makeText(itemView.getContext(),
                                "Delete " + tag.getName(), Toast.LENGTH_SHORT).show();
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
