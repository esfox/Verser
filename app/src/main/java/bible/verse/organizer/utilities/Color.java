package bible.verse.organizer.utilities;

import android.content.Context;
import android.util.TypedValue;

public class Color
{
    public static int getColor(Context context, int attributeID)
    {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attributeID, typedValue, true);
        return typedValue.data;
    }
}
