package bible.verse.organizer.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import bible.verse.organizer.adapters.viewholders.TagViewHolder;
import bible.verse.organizer.interfaces.TagsListItemListener;
import bible.verse.organizer.objects.Tag;
import bible.verse.organizer.organizer.R;
import bible.verse.organizer.utilities.Color;

public class TagsAdapter extends RecyclerView.Adapter<TagViewHolder>
{
    private List<Tag> tags;

    private TagsListItemListener listener;

    public TagsAdapter(List<Tag> tags, TagsListItemListener listener)
    {
        this.tags = tags;
        this.listener = listener;

        Collections.reverse(tags);
    }

    public void addTag(final Context context)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        View newTagDialog = inflater.inflate(R.layout.dialog_new_tag, null);

        newTagDialog.findViewById(R.id.new_tag_icon)
            .setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Toast.makeText(context, "Set Color", Toast.LENGTH_SHORT).show();
                }
            });

        final TextInputLayout newTagName = newTagDialog
            .findViewById(R.id.new_tag_name);

        final AlertDialog dialog = new AlertDialog.Builder(context)
            .setTitle("Add New Tag")
            .setView(newTagDialog)
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
                            String tagName = newTagName
                                .getEditText().getText().toString();

                            if(tagName.equals(""))
                                newTagName.setError("Please enter a name for the tag.");
                            else
                            {
                                dialog.dismiss();

                                Tag tag = new Tag(tagName,
                                    Color.getColor(context, R.attr.colorPrimary));
                                tags.add(0, tag);

                                notifyItemInserted(0);
                                listener.onAddTag(tag);
                            }
                        }
                    });
            }
        });
        dialog.show();
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
