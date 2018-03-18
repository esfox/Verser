package bible.verse.organizer.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import bible.verse.organizer.adapters.TagsAdapter;
import bible.verse.organizer.interfaces.TagsListItemListener;
import bible.verse.organizer.objects.Tag;
import bible.verse.organizer.organizer.R;

public class Tags extends Fragment implements
        TagsListItemListener,
        View.OnClickListener
{
    public Tags() {}

    private TagsAdapter adapter;
    private RecyclerView tagsList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_tags, container, false);

        tagsList = view.findViewById(R.id.tags_list);
        tagsList.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        tagsList.setLayoutManager(gridLayoutManager);

        adapter = new TagsAdapter(makeDummyData());
        tagsList.setAdapter(adapter);

        FloatingActionButton add = view.findViewById(R.id.tags_add);
        add.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view)
    {
        switch(view.getId())
        {
            case R.id.tags_add:
                addNewTag();
                break;

            case R.id.new_tag_icon:
                Toast.makeText(getContext(), "Change Icon", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onTagItemClick(Tag tag)
    {
        Snackbar.make(getView(), "Go to " + tag.getName(), Snackbar.LENGTH_SHORT).show();
    }

    private void addNewTag()
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View newTagDialog = inflater.inflate(R.layout.dialog_new_tag, null);

        newTagDialog.findViewById(R.id.new_tag_icon)
                .setOnClickListener(this);

        final TextInputLayout newTagName = newTagDialog
                .findViewById(R.id.new_tag_name);

        final AlertDialog dialog = new AlertDialog.Builder(getContext())
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
                                    adapter.addTag(new Tag ());
                                    tagsList.smoothScrollToPosition(0);
                                    dialog.dismiss();
                                }
                            }
                        });
            }
        });
        dialog.show();
    }

    private List<Tag> makeDummyData ()
    {
        List<Tag> tags = new ArrayList<>();

        for (int i = 0; i < 20; i++)
        {
            Tag tag = new Tag();

            tag.setName("Tagatag");
            tag.setVerseCount(10);
            tag.setColor(R.color.colorAccent);

            tags.add(tag);
        }

        return tags;
    }
}
