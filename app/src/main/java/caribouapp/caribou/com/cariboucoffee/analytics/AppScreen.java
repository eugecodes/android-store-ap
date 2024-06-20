package caribouapp.caribou.com.cariboucoffee.analytics;

public enum AppScreen {
    HOME_SCREEN("Home Screen", "home_screen"),
    CHECK_IN("Check-In/Pay", "check_in"),
    MENU("Menu", "menu"),
    MENU_OA("Menu OA", "menu_oa"),
    LOCATIONS("Locations", "locations"),
    LOCATIONS_OA("Locations OA", "locations_oa"),
    CART("Cart", "cart"),
    CHECKOUT("Checkout", "checkout"),
    ADD_FUNDS_OA("Add Funds OA", "add_funds_oa"),
    ADD_FUNDS("Add Funds", "add_funds"),
    ADD_PAYMENT_OA("Add Payment OA", "add_payment_oa"),
    ADD_PAYMENT("Add Payment", "add_payment"),
    PICKUP_TYPE("Pickup Type", "pickup_type"),
    PICKUP_TYPE_CHECKOUT("Pickup Type Checkout", "pickup_type_checkout"),
    CHOOSE_LOCATION("Choose Location", "choose_location"),
    MY_ACCOUNT("My Account", "my_account"),
    EDIT_PROFILE("Edit Profile", "edit_profile"),
    CHANGE_PASSWORD("Change Password", "change_password"),
    EDIT_CARD("Edit Card", "edit_card"),
    FAQS("FAQs", "faqs"),
    TERMS("Terms", "terms"),
    LOG_IN("Log in", "log_in"),
    SIGN_UP_1("Sign up 1", "sign_up_1"),
    SIGN_UP_2("Sign up 2", "sign_up_2"),
    FORGOT_PASSWORD("Forgot Password", "forgot_password"),
    EGIFT("EGift", "egift"),
    INBOX("Inbox", "inbox"),
    SHARE_A_PERK("Share-A-Perk", "share_a_perk"),
    TRIVIA_WAIT("Trivia Wait", "trivia_wait"),
    TRIVIA_QUESTION("Trivia Question", "trivia_question"),
    TRIVIA_RESULT("Trivia Result", "trivia_result"),
    STORE_DETAIL("Store Details", "store_detail"),
    STORE_DETAIL_OA("Store Details OA", "store_detail_oa"),
    ORDER_CONFIRMATION("Order Confirmation", "order_confirmation"),

    SET_PASSWORD("Set Password", "set_password"),
    PERSONAL_INFO("Personal Info", "personal_info"),
    PRODUCT_MENU("Product Menu", "product_menu"),
    ITEM_CUSTOMIZATION("Item Customization", "item_customization"),
    LOCATION_DETAILS("Locations Details", "location_details"),
    GUEST_USER_DETAILS("Guest User Details", "guest_user_details"),
    ADD_NEW_CARD_PAYMENT("Add New Card Payment", "add_new_card_payment"),
    PAYMENT_TYPE_SELECTION("Payment Type Selection", "payment_type_selection");

    private String mScreenValue;
    private String mScreenAnalyticsValue;

    AppScreen(String screenValue, String screenAnalyticsValue) {
        mScreenValue = screenValue;
        mScreenAnalyticsValue = screenAnalyticsValue;
    }

    public String getScreenValue() {
        return mScreenValue;
    }

    public String getScreenAnalyticsValue() {
        return mScreenAnalyticsValue;
    }
}
