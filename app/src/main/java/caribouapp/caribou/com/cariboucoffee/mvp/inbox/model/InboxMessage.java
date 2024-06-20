package caribouapp.caribou.com.cariboucoffee.mvp.inbox.model;

import com.urbanairship.messagecenter.Message;

import org.joda.time.LocalDate;

public class InboxMessage {

    private Message mRichPushMessage;

    public InboxMessage(Message richPushMessage) {
        mRichPushMessage = richPushMessage;
    }

    public String getTitle() {
        return mRichPushMessage.getTitle();
    }

    public LocalDate getDate() {
        return LocalDate.fromDateFields(mRichPushMessage.getSentDate());
    }

    public boolean isUnread() {
        return !mRichPushMessage.isRead();
    }
}
