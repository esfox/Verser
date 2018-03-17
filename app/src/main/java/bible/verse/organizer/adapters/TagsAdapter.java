package bible.verse.organizer.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import bible.verse.organizer.adapters.viewholders.TagViewHolder;
import bible.verse.organizer.interfaces.TagsListItemListener;
import bible.verse.organizer.objects.Tag;
import bible.verse.organizer.organizer.R;

public class TagsAdapter extends RecyclerView.Adapter<TagViewHolder>
{
    private List<Tag> tags;

    private TagsListItemListener listener;

    public TagsAdapter(List<Tag> tags) { this.tags = tags; }

    public void addTag(Tag tag)
    {
        tags.add(0, tag);
        notifyItemInserted(0);
    }

    @Override
    public TagViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new TagViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tags_item, parent, false), listener);
    }

    @Override
    public void onBindViewHolder(TagViewHolder holder, int position)
    {
        holder.bind(tags.get(position));
    }

    @Override
    public int getItemCount()
    {
        return tags.size();
    }
}
