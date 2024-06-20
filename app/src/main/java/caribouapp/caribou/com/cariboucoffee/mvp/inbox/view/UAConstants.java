package caribouapp.caribou.com.cariboucoffee.mvp.inbox.view;

import com.urbanairship.messagecenter.MessageCenter;

public final class UAConstants {

    private UAConstants() {
    }

    public static final String ACTION_VIEW_RICH_PUSH_INBOX = MessageCenter.VIEW_MESSAGE_CENTER_INTENT_ACTION;
    public static final String ACTION_VIEW_RICH_PUSH_MESSAGE = MessageCenter.VIEW_MESSAGE_INTENT_ACTION;

    public static final String MESSAGE_ID_KEY = "messageId";

}
