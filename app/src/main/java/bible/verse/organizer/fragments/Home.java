package bible.verse.organizer.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bible.verse.organizer.organizer.R;

public class Home extends Fragment
{
    public Home() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View layout = inflater.inflate(R.layout.fragment_home, container, false);
        return layout;
    }
}
