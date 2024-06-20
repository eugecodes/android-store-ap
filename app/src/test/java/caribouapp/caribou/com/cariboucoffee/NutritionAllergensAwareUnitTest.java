package caribouapp.caribou.com.cariboucoffee;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import caribouapp.caribou.com.cariboucoffee.api.model.content.CmsNutritionalDataEntry;
import caribouapp.caribou.com.cariboucoffee.api.model.content.CmsServingNutritionalData;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.MenuNutritionContract;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCardItemModel;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.NutritionalProductModel;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.SizeEnum;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.presenter.MenuNutritionPresenter;
import caribouapp.caribou.com.cariboucoffee.util.nutritionnallergens.NutritionAllergensAware;

public class NutritionAllergensAwareUnitTest {

    private NutritionAllergensAware mNutritionAllergensAware;
    private MenuCardItemModel mMenuCardItemModel;
    private MenuNutritionContract.View mMenuNutritionContractView;
    private MenuNutritionPresenter mNutritionPresenter;

    @Before
    public void before() {
        mNutritionAllergensAware = mock(NutritionAllergensAware.class);
        mMenuNutritionContractView = mock(MenuNutritionContract.View.class);
        mMenuCardItemModel = new MenuCardItemModel(mock(SettingsServices.class));
    }

    @Test
    public void testNoNutritionAllergensAvailableHappenWhenNullNutritionModel() {
        NutritionAllergensAware.notifyAvailability(mMenuCardItemModel, mNutritionAllergensAware);
        verify(mNutritionAllergensAware).onNoNutritionAllergensAvailable();
        verifyNoMoreInteractions(mNutritionAllergensAware);
    }

    @Test
    public void testNoAllergensAvailableHappenWhenNullNutritionModel() {
        CmsServingNutritionalData cmsServingNutritionalData = new CmsServingNutritionalData();
        CmsNutritionalDataEntry cmsNutritionalDataEntry = new CmsNutritionalDataEntry();
        cmsNutritionalDataEntry.setName("calories");
        cmsNutritionalDataEntry.setValue("400");
        cmsServingNutritionalData.getNutrition().add(cmsNutritionalDataEntry);
        cmsServingNutritionalData.setSize("small");
        NutritionalProductModel nutritionalProductModel = new NutritionalProductModel(Arrays.asList(cmsServingNutritionalData));

        mMenuCardItemModel.setNutritionalProductModel(nutritionalProductModel);

        NutritionAllergensAware.notifyAvailability(mMenuCardItemModel, mNutritionAllergensAware);
        verify(mNutritionAllergensAware).onNoAllergensAvailable();
        verifyNoMoreInteractions(mNutritionAllergensAware);
    }

    @Test
    public void testNoNutritionAvailableHappenWhenNullNutritionModel() {
        mMenuCardItemModel.setAllergens(Arrays.asList("Allergen"));

        NutritionAllergensAware.notifyAvailability(mMenuCardItemModel, mNutritionAllergensAware);
        verify(mNutritionAllergensAware).onNoNutritionAvailable();
        verifyNoMoreInteractions(mNutritionAllergensAware);
    }

    @Test
    public void testNutritionBeverageWithTwoSizes() {
        CmsServingNutritionalData cmsServingNutritionalData = new CmsServingNutritionalData();
        CmsServingNutritionalData cmsServingNutritionalDataSecondEntry = new CmsServingNutritionalData();
        CmsNutritionalDataEntry cmsNutritionalDataEntry = new CmsNutritionalDataEntry();
        cmsNutritionalDataEntry.setName("calories");
        cmsNutritionalDataEntry.setValue("400");
        cmsServingNutritionalData.getNutrition().add(cmsNutritionalDataEntry);
        cmsServingNutritionalDataSecondEntry.getNutrition().add(cmsNutritionalDataEntry);
        cmsServingNutritionalData.setSize("small");
        cmsServingNutritionalDataSecondEntry.setSize("Medium");
        List<CmsServingNutritionalData> cmsServingNutritionalDataList = new ArrayList<>();
        cmsServingNutritionalDataList.add(cmsServingNutritionalData);
        cmsServingNutritionalDataList.add(cmsServingNutritionalDataSecondEntry);
        NutritionalProductModel nutritionalProductModel = new NutritionalProductModel(cmsServingNutritionalDataList);
        mMenuCardItemModel.setNutritionalProductModel(nutritionalProductModel);
        mNutritionPresenter = new MenuNutritionPresenter(mMenuNutritionContractView);
        mNutritionPresenter.setModel(mMenuCardItemModel);
        mNutritionPresenter.loadData(SizeEnum.MEDIUM);
        ArgumentCaptor<Set> argument = ArgumentCaptor.forClass(Set.class);
        verify(mMenuNutritionContractView, times(1)).setPossibleSizes(argument.capture());
        Assert.assertEquals(argument.getAllValues().size(), 1);
    }
}
