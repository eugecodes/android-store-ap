package caribouapp.caribou.com.cariboucoffee;

import javax.inject.Singleton;

import caribouapp.caribou.com.cariboucoffee.api.RestModule;
import caribouapp.caribou.com.cariboucoffee.api.RestOAuthV2Module;
import caribouapp.caribou.com.cariboucoffee.common.BaseActivity;
import caribouapp.caribou.com.cariboucoffee.common.CCInformationActivity;
import caribouapp.caribou.com.cariboucoffee.common.CCInformationPresenter;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.BaseViewRetrofitErrorMapperCallback;
import caribouapp.caribou.com.cariboucoffee.databinding.BindingAdapters;
import caribouapp.caribou.com.cariboucoffee.di.DomainModule;
import caribouapp.caribou.com.cariboucoffee.domain.AuthorizeSaleUseCase;
import caribouapp.caribou.com.cariboucoffee.mvp.account.presenter.CreditCardPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.account.presenter.ProfilePresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.account.presenter.RewardsCardPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.account.presenter.TransactionHistoryPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.account.view.TransactionHistoryFragment;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.ResetPasswordPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SignInActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SignInPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.presenter.AddFundsPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.presenter.AutoReloadPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.presenter.CheckInPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.view.CheckInActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.dashboard.BaseUpdatePresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.dashboard.DashboardFragment;
import caribouapp.caribou.com.cariboucoffee.mvp.dashboard.DashboardPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.enrollment.presenter.DupeCheckPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.enrollment.presenter.PersonalInformationPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.enrollment.presenter.SetPasswordPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.enrollment.presenter.SignUpPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.enrollment.view.PersonalInfoFragment;
import caribouapp.caribou.com.cariboucoffee.mvp.enrollment.view.PersonalInformationActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.faq.presenter.FaqPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.feedback.presenter.FeedbackPopupPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.feedback.presenter.FeedbackPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.inbox.view.CustomMessageActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.inbox.view.CustomMessageListFragment;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.presenter.MultipleModifiersAndQuantitiesPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.presenter.OptionAndQuantityPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.presenter.ncr.NcrItemCustomizationPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.presenter.positouch.PositouchItemCustomizationPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.LocationDetailsActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.LocationDetailsPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.LocationsActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.LocationsFilterFragmentDialog;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.LocationsListActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.LocationsPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.presenter.MenuDetailsPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.presenter.MenuFragmentPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.presenter.MenuNutritionPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.presenter.MenuPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.presenter.MenuRewardsFragmentPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.view.MenuActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.view.MenuProductFragment;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.presenter.PaymentSelectionPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.view.AddNewCardPaymentActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.SettingsInjector;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.cart.presenter.CartPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.presenter.OrderCheckoutPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.presenter.LetsGetStartedPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.presenter.NewCardPaymentPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.presenter.OrderConfirmationPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.view.GuestUserDetailActivityDialog;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.view.PaymentTypeSelectionActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.picklocation.presenter.PickLocationPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.picklocation.view.PickLocationActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.pickup.presenter.PickupPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.recentOrders.presenter.RecentOrderPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.recentOrders.view.RecentOrderActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.termsandprivacy.presenter.TermsAndPrivacyPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.trivia.presenter.TriviaCountdownPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.trivia.presenter.TriviaFinishPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.trivia.presenter.TriviaQuestionPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.webflow.presenter.CaptivePortalWebPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.webflow.presenter.SourceWebPresenter;
import caribouapp.caribou.com.cariboucoffee.push.MyFirebaseMessagingService;
import dagger.Component;

/**
 * Created by asegurola on 11/22/16.
 */

@Singleton
@Component(modules = {AppModule.class, RestModule.class, RestOAuthV2Module.class, DomainModule.class})
public interface CaribouComponent {

    AuthorizeSaleUseCase provideAuthorizeSaleUseCase();

    void inject(DashboardPresenter presenter);

    void inject(LocationsPresenter presenter);

    void inject(MenuPresenter presenter);

    void inject(LocationDetailsPresenter presenter);

    void inject(SignInPresenter presenter);

    void inject(ResetPasswordPresenter presenter);

    void inject(SplashPresenter presenter);

    void inject(TermsAndPrivacyPresenter presenter);

    void inject(FaqPresenter presenter);

    void inject(CheckInPresenter presenter);

    void inject(CreditCardPresenter presenter);

    void inject(SourceWebPresenter sourceWebPresenter);

    void inject(AddFundsPresenter presenter);

    void inject(CCInformationPresenter presenter);

    void inject(AutoReloadPresenter presenter);

    void inject(ProfilePresenter profilePresenter);

    void inject(MenuNutritionPresenter presenter);

    void inject(BaseViewRetrofitErrorMapperCallback.InjectionHelper injectionHelper);

    void inject(BindingAdapters.InjectionHelper injectionHelper);

    void inject(DupeCheckPresenter presenter);

    void inject(SetPasswordPresenter presenter);

    void inject(SourceApplication caribouApplication);

    void inject(PersonalInformationPresenter presenter);

    void inject(PersonalInformationActivity personalInformationActivity);

    void inject(SignInActivity signInActivity);

    void inject(MenuFragmentPresenter presenter);

    void inject(FeedbackPopupPresenter presenter);

    void inject(FeedbackPresenter presenter);

    void inject(BaseActivity.InjectionHelper manager);

    void inject(PickLocationPresenter pickLocationPresenter);

    void inject(CaptivePortalWebPresenter captivePortalWebPresenter);

    void inject(PositouchItemCustomizationPresenter itemCustomizationPresenter);

    void inject(OptionAndQuantityPresenter presenter);

    void inject(MultipleModifiersAndQuantitiesPresenter presenter);

    void inject(OrderConfirmationPresenter orderConfirmationPresenter);

    void inject(RecentOrderPresenter presenter);

    void inject(OrderCheckoutPresenter presenter);

    void inject(CartPresenter presenter);

    void inject(CCInformationActivity ccInformationActivity);

    void inject(CheckInActivity checkInActivity);

    void inject(MenuActivity menuActivity);

    void inject(MenuProductFragment menuProductFragment);

    void inject(BindingAdapters.ClockWrapper clockWrapper);

    void inject(LocationsFilterFragmentDialog locationsFilterFragmentDialog);

    void inject(TransactionHistoryPresenter presenter);

    void inject(RewardsCardPresenter rewardCardPresenter);

    void inject(MenuDetailsPresenter menuDetailsPresenter);

    void inject(DashboardFragment dashboardActivity);

    void inject(CustomMessageListFragment customMessageListFragment);

    void inject(CustomMessageActivity customMessageActivity);

    void inject(SignUpPresenter signUpPresenter);

    void inject(PersonalInfoFragment personalInfoFragment);

    void inject(MenuRewardsFragmentPresenter presenter);

    void inject(TriviaCountdownPresenter presenter);

    void inject(TriviaFinishPresenter presenter);

    void inject(TriviaQuestionPresenter presenter);

    void inject(DeeplinkParser parseDeepLinkActivity);

    void inject(BaseUpdatePresenter presenter);

    void inject(NcrItemCustomizationPresenter ncrItemCustomizationPresenter);

    void inject(TransactionHistoryFragment transactionHistoryFragment);

    void inject(MyFirebaseMessagingService myFirebaseMessagingService);

    void inject(PickupPresenter pickupPresenter);

    void inject(SettingsInjector settingsServices);

    void inject(PickLocationActivity pickLocationActivity);

    void inject(LocationsActivity locationsActivity);

    void inject(LocationDetailsActivity locationDetailsActivity);

    void inject(LocationsListActivity locationsListActivity);

    void inject(RecentOrderActivity recentOrderActivity);

    void inject(LetsGetStartedPresenter presenter);

    void inject(GuestUserDetailActivityDialog guestUserDetailActivityDialog);

    void inject(NewCardPaymentPresenter presenter);

    void inject(AddNewCardPaymentActivity addNewCardPaymentActivity);

    void inject(PaymentSelectionPresenter presenter);

    void inject(PaymentTypeSelectionActivity paymentTypeSelectionActivity);

}


