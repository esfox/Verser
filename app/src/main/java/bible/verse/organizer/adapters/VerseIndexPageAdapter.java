package bible.verse.organizer.adapters;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class VerseIndexPageAdapter extends PagerAdapter
{
    private List<View> pages = new ArrayList<>();

    public void addPage(View page)
    {
        pages.add(page);
    }

    @Override
    public boolean isViewFromObject(View view, Object object)
    {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        return pages.get(position);
    }

    @Override
    public int getCount()
    {
        return pages.size();
    }
}
