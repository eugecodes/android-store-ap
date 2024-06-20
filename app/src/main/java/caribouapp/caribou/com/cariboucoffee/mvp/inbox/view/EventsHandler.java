package caribouapp.caribou.com.cariboucoffee.mvp.inbox.view;

import caribouapp.caribou.com.cariboucoffee.mvp.inbox.model.InboxMessage;

public interface EventsHandler {

    void onSelectionClicked(InboxMessage inboxMessage);
}
