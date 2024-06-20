package caribouapp.caribou.com.cariboucoffee.util.nutritionnallergens;

import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCardItemModel;

public interface NutritionAllergensAware {

    void onNoNutritionAllergensAvailable();

    void onNoAllergensAvailable();

    void onNoNutritionAvailable();

    static void notifyAvailability(MenuCardItemModel model, NutritionAllergensAware nutritionAllergensAware) {
        if (model.getNutritionalItemModels().isEmpty() && model.getAllergens().isEmpty()) {
            nutritionAllergensAware.onNoNutritionAllergensAvailable();
        } else if (model.getNutritionalItemModels().isEmpty()) {
            nutritionAllergensAware.onNoNutritionAvailable();
        } else if (model.getAllergens().isEmpty()) {
            nutritionAllergensAware.onNoAllergensAvailable();
        }
    }
}
