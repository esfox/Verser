package bible.verse.organizer.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.GridView;

public class PopupGridView extends GridView
{
    public PopupGridView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    public PopupGridView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PopupGridView(Context context)
    {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        float height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 288,
            getContext().getResources().getDisplayMetrics());
        int maxHeight = MeasureSpec.makeMeasureSpec((int) height, MeasureSpec.AT_MOST);

        super.onMeasure(widthMeasureSpec, maxHeight);
    }
}
