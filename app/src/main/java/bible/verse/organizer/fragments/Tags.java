package bible.verse.organizer.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import bible.verse.organizer.adapters.TagsAdapter;
import bible.verse.organizer.objects.Tag;
import bible.verse.organizer.organizer.R;

public class Tags extends Fragment
{
    public Tags() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_tags, container, false);

        RecyclerView tagsList = view.findViewById(R.id.tags_list);
        tagsList.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        tagsList.setLayoutManager(gridLayoutManager);

        TagsAdapter tagsAdapter = new TagsAdapter(makeDummyData());
        tagsList.setAdapter(tagsAdapter);

        FloatingActionButton add = view.findViewById(R.id.tags_add);
        return view;
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
