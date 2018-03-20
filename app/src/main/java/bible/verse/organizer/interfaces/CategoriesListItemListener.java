package bible.verse.organizer.interfaces;

import bible.verse.organizer.objects.Category;

public interface CategoriesListItemListener
{
    void onCategoryItemClick(Category category);
    void onAddCategory(Category category, int lastIndex);
}
