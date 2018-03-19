package bible.verse.organizer.utilities;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;

import bible.verse.organizer.organizer.R;

public class Color
{
    public static int getColor(Context context, int attributeID)
    {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attributeID, typedValue, true);
        return typedValue.data;
    }

    public static int getAdaptedTextColor(Context context, int color)
    {
        double luminance = 1 -
            (0.299 * android.graphics.Color.red(color)
                + 0.587 * android.graphics.Color.green(color)
                + 0.114 * android.graphics.Color.blue(color))
                / 255;

        return luminance < 0.42?
            ContextCompat.getColor(context, R.color.textColorPrimary) :
            ContextCompat.getColor(context, R.color.textColorLight);
    }
}
