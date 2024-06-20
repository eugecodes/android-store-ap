package caribouapp.caribou.com.cariboucoffee;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import com.google.gson.reflect.TypeToken;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Map;

import caribouapp.caribou.com.cariboucoffee.api.CmsApi;
import caribouapp.caribou.com.cariboucoffee.api.CmsOrderApi;
import caribouapp.caribou.com.cariboucoffee.api.model.content.CmsMenuCategory;
import caribouapp.caribou.com.cariboucoffee.common.ResultCallback;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCardItemModel;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCategory;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.NutritionalItemModel;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.NutritionalRowData;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.SizeEnum;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.service.MenuData;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.service.MenuDataService;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.service.NcrMenuDataServiceImpl;
import caribouapp.caribou.com.cariboucoffee.util.MockitoUtil;
import retrofit2.Call;

/**
 * Created by jmsmuy on 7/4/18.
 */
@RunWith(MockitoJUnitRunner.class)
public class NutritionTest {

    enum NutritionalEntry {

        ServingSize("Serving Size (g)"),
        Calories("Calories"),
        FatCalories("Fat Calories"),
        TotalFat("Total Fat (g)"),
        SaturatedFat("Saturated Fat (g)"),
        TransFat("Trans Fat (g)"),
        Cholesterol("Cholesterol (mg)"),
        Protein("Protein (g)"),
        TotalCarbohydrates("Total Carbohydrates (g)"),
        Sodium("Sodium (mg)"),
        Sugars("Sugars (g)"),
        Fiber("Fiber (g)"),
        Caffeine("Caffeine (mg)");

        private String mLabel;

        NutritionalEntry(String label) {
            mLabel = label;
        }

        public String getLabel() {
            return mLabel;
        }

        public static NutritionalEntry fromLabel(String label) {
            for (NutritionalEntry nutritionalEntry : values()) {
                if (nutritionalEntry.getLabel().equalsIgnoreCase(label)) {
                    return nutritionalEntry;
                }
            }
            return null;
        }
    }


    private static final String PRODUCT_NAME_AMERICANO = "Americano";
    private static final String PRODUCT_NAME_COD = "Coffee of the Day";
    private static final String PRODUCT_NAME_LATTE = "Latte";
    private static final String PRODUCT_NAME_ICED_TURTLE_MOCHA = "Iced Turtle Mocha";
    private static final String PRODUCT_NAME_CARAMEL_COOLER = "Caramel Cooler";
    private static final String PRODUCT_NAME_SMOOTHIE = "Kids Strawberry Banana Smoothie";
    private static final String PRODUCT_NAME_PLAN_BAGEL = "Plain Bagel";
    private static final String PRODUCT_NAME_ASIAGO_BAGEL = "Asiago Bagel";
    private static final String PRODUCT_NAME_EVERYTHING_BAGEL = "Everything Bagel";
    private MenuDataService mMenuDataService;
    @Mock
    private CmsApi mCmsApi;
    @Mock
    private CmsOrderApi cmsOrderApi;
    @Mock
    private Call<List<CmsMenuCategory>> mGetFilterableMenuCall;
    @Mock
    private SettingsServices mSettingsServices;

    @Before
    public void setup() {
        when(mCmsApi.getFilterableMenu()).thenReturn(mGetFilterableMenuCall);

        mMenuDataService = new NcrMenuDataServiceImpl(mCmsApi, cmsOrderApi, mSettingsServices);
        MockitoUtil.mockEnqueue(mGetFilterableMenuCall, "/test_menuv3.json", new TypeToken<List<CmsMenuCategory>>() {
        }.getType());
    }

    @Test
    //TODO We should separate test here, one to test NutritionCSVProcessor and another test then loading the data to the model as we do in MenuDataServiceImpl.
    public void testMenu() {
        mMenuDataService.getMenuDataFiltered(new ResultCallback<MenuData>() {
            @Override
            public void onSuccess(MenuData data) {
                // Americano
                assertEquals(testDataNutrition(data, PRODUCT_NAME_AMERICANO, SizeEnum.SMALL, "344", "0", "0",
                        "0", "0", "0", "0", "1", "0",
                        "10", "0", "0", "180"), 3);
                assertEquals(testDataNutrition(data, PRODUCT_NAME_AMERICANO, SizeEnum.MEDIUM, "457", "0", "0",
                        "0", "0", "0", "0", "2", "0",
                        "15", "0", "0", "270"), 3);
                assertEquals(testDataNutrition(data, PRODUCT_NAME_AMERICANO, SizeEnum.LARGE, "570", "5", "5",
                        "0", "0", "0", "0", "2", "0",
                        "20", "0", "0", "360"), 3);

                // Coffee of the Day
                assertEquals(testDataNutrition(data, PRODUCT_NAME_COD, SizeEnum.SMALL, "340", "5", "0",
                        "0", "0", "0", "0", "0", "0",
                        "5", "0", "0", "230"), 3);
                assertEquals(testDataNutrition(data, PRODUCT_NAME_COD, SizeEnum.MEDIUM, "454", "5", "0",
                        "0", "0", "0", "0", "1", "0",
                        "10", "0", "0", "305"), 3);
                assertEquals(testDataNutrition(data, PRODUCT_NAME_COD, SizeEnum.LARGE, "567", "5", "0",
                        "0", "0", "0", "0", "1", "0",
                        "10", "0", "0", "385"), 3);

                // Latte
                assertEquals(testDataNutrition(data, PRODUCT_NAME_LATTE, SizeEnum.SMALL, "323", "90", "5",
                        "0.5", "0", "0", "5", "9", "12",
                        "140", "12", "0", "180"), 3);
                assertEquals(testDataNutrition(data, PRODUCT_NAME_LATTE, SizeEnum.MEDIUM, "445", "130", "5",
                        "1", "0", "0", "5", "14", "18",
                        "200", "18", "0", "180"), 3);
                assertEquals(testDataNutrition(data, PRODUCT_NAME_LATTE, SizeEnum.LARGE, "545", "150", "10",
                        "1", "0.5", "0", "10", "16", "21",
                        "240", "21", "0", "270"), 3);

                // Iced Turtle Mocha
                assertEquals(testDataNutrition(data, PRODUCT_NAME_ICED_TURTLE_MOCHA, SizeEnum.SMALL, "380", "380", "60",
                        "7", "4", "0", "20", "6", "77",
                        "240", "63", "3", "180"), 3);
                assertEquals(testDataNutrition(data, PRODUCT_NAME_ICED_TURTLE_MOCHA, SizeEnum.MEDIUM, "472", "480", "80",
                        "9", "5", "0", "20", "7", "97",
                        "290", "80", "4", "270"), 3);
                assertEquals(testDataNutrition(data, PRODUCT_NAME_ICED_TURTLE_MOCHA, SizeEnum.LARGE, "565", "580", "100",
                        "11", "6", "0", "25", "8", "118",
                        "340", "97", "5", "360"), 3);

                // Caramel Cooler
                assertEquals(testDataNutrition(data, PRODUCT_NAME_CARAMEL_COOLER, SizeEnum.SMALL, "469", "430", "80",
                        "9", "7", "0", "15", "3", "86",
                        "300", "72", "0", "100"), 3);
                assertEquals(testDataNutrition(data, PRODUCT_NAME_CARAMEL_COOLER, SizeEnum.MEDIUM, "594", "540", "100",
                        "12", "9", "0", "20", "4", "107",
                        "370", "90", "0", "125"), 3);
                assertEquals(testDataNutrition(data, PRODUCT_NAME_CARAMEL_COOLER, SizeEnum.LARGE, "719", "650", "120",
                        "14", "10", "0", "25", "5", "129",
                        "440", "108", "0", "150"), 3);

                // Kids Strawberry Banana Smoothie
                assertEquals(testDataNutrition(data, PRODUCT_NAME_SMOOTHIE, SizeEnum.ONE_SIZE, "373", "230", "0",
                        "0", "0", "0", "0", "0", "53",
                        "25", "50", "0", "0"), 1);

                // Plain Bagel
                assertEquals(testDataNutrition(data, PRODUCT_NAME_PLAN_BAGEL, null, "105", "270", "10",
                        "1", "0", "0", "0", "10", "57",
                        "540", "6", "4", "NA"), 1);

                // Asiago Bagel
                assertEquals(testDataNutrition(data, PRODUCT_NAME_ASIAGO_BAGEL, null, "105", "290", "30",
                        "3.5", "2", "0", "10", "12", "52",
                        "590", "5", "3", "NA"), 1);

                // Everything Bagel
                assertEquals(testDataNutrition(data, PRODUCT_NAME_EVERYTHING_BAGEL, null, "105", "280", "15",
                        "1.5", "0", "0", "0", "10", "57",
                        "530", "6", "4", "NA"), 1);


            }

            @Override
            public void onFail(int errorCode, String errorMessage) {

            }

            @Override
            public void onError(Throwable error) {

            }
        });

        MockitoUtil.mockEnqueue(mGetFilterableMenuCall, "/test_menuv3.json", new TypeToken<List<CmsMenuCategory>>() {
        }.getType());
    }

    /**
     * Checks if the passes nutritional value is correct
     *
     * @param particularNutritionalValue
     * @param servSize
     * @param fatCals
     * @param totFat
     * @param satFat
     * @param transFat
     * @param cholesterol
     * @param protein
     * @param totCarbs
     * @param sodium
     * @param sugars
     * @param fiber
     * @param caffeine
     */
    private void checkNutritionalValue(NutritionalRowData particularNutritionalValue,
                                       String servSize, String fatCals, String totFat, String satFat,
                                       String transFat, String cholesterol, String protein, String totCarbs,
                                       String sodium, String sugars, String fiber, String caffeine) {
        String value = particularNutritionalValue.getNutritionalValue();

        NutritionalEntry nutritionalEntry = NutritionalEntry.fromLabel(particularNutritionalValue.getNutritionalName());
        switch (nutritionalEntry) {
            case ServingSize:
                assertEquals(value, servSize);
                break;
            case FatCalories:
                assertEquals(value, fatCals);
                break;
            case TotalFat:
                assertEquals(value, totFat);
                break;
            case SaturatedFat:
                assertEquals(value, satFat);
                break;
            case TransFat:
                assertEquals(value, transFat);
                break;
            case Cholesterol:
                assertEquals(value, cholesterol);
                break;
            case Protein:
                assertEquals(value, protein);
                break;
            case TotalCarbohydrates:
                assertEquals(value, totCarbs);
                break;
            case Sodium:
                assertEquals(value, sodium);
                break;
            case Sugars:
                assertEquals(value, sugars);
                break;
            case Fiber:
                assertEquals(value, fiber);
                break;
            case Caffeine:
                assertEquals(value, caffeine);
                break;
        }
    }

    /**
     * Tests if all the nutritional parameters are present in the menu data recovered
     * Returns the number of sizes available for product with id "id"
     *
     * @param data
     * @param id
     * @param sizeEnum
     * @param servSize
     * @param calories
     * @param fatCals
     * @param totFat
     * @param satFat
     * @param transFat
     * @param cholesterol
     * @param protein
     * @param totCarbs
     * @param sodium
     * @param sugars
     * @param fiber
     * @param caffeine
     * @return
     */
    private int testDataNutrition(MenuData data, String id, SizeEnum sizeEnum, String servSize, String calories,
                                  String fatCals, String totFat, String satFat, String transFat, String cholesterol,
                                  String protein, String totCarbs, String sodium, String sugars, String fiber, String caffeine) {

        MenuCardItemModel menuCardItemModel = findProductByNameInCategories(data.getCategories(), id);
        Map<SizeEnum, NutritionalItemModel> nutritionalItemModelList = menuCardItemModel.getNutritionalItemModels();
        assertNotNull(nutritionalItemModelList);
        assertFalse(nutritionalItemModelList.isEmpty());

        NutritionalItemModel nutritionalItemModel;
        if (sizeEnum == null) {
            nutritionalItemModel = menuCardItemModel.getDefaultNutritionalItemModel();
        } else {
            nutritionalItemModel = nutritionalItemModelList.get(sizeEnum);
        }

        assertEquals(calories, nutritionalItemModel.getCalories());

        for (NutritionalRowData particularNutritionalValue : nutritionalItemModel.getNutritionalValues()) {
            checkNutritionalValue(particularNutritionalValue, servSize, fatCals, totFat, satFat, transFat, cholesterol, protein, totCarbs, sodium, sugars, fiber, caffeine);
        }

        return nutritionalItemModelList.size();
    }

    private MenuCardItemModel findProductByNameInCategories(List<MenuCategory> menuCategories, String productName) {
        for (MenuCategory menuCategory : menuCategories) {
            for (MenuCardItemModel menuCardItemModel : menuCategory.getProducts()) {
                if (menuCardItemModel.getName().equalsIgnoreCase(productName)) {
                    return menuCardItemModel;
                }
            }

            MenuCardItemModel menuCardItemModel = findProductByNameInCategories(menuCategory.getSubCategories(), productName);
            if (menuCardItemModel != null) {
                return menuCardItemModel;
            }
        }
        return null;
    }

}
