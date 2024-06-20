package caribouapp.caribou.com.cariboucoffee.mvp.inbox.view;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.urbanairship.messagecenter.DefaultMultiChoiceModeListener;
import com.urbanairship.messagecenter.Inbox;
import com.urbanairship.messagecenter.Message;
import com.urbanairship.messagecenter.MessageListFragment;
import com.urbanairship.messagecenter.MessageViewAdapter;

import javax.inject.Inject;

import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.SourceApplication;
import caribouapp.caribou.com.cariboucoffee.databinding.LayoutCustomInboxItemBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.inbox.model.InboxMessage;

public class CustomMessageListFragment extends MessageListFragment {

    @Inject
    Inbox richPushInbox;

    @Override
    public void onAttach(Context context) {
        SourceApplication.get(context).getComponent().inject(this);
        super.onAttach(context);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AbsListView absListView = getAbsListView();

        absListView.setMultiChoiceModeListener(new DefaultMultiChoiceModeListener(this));
        absListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        absListView.setSaveEnabled(false);
    }

    @NonNull
    @Override
    protected MessageViewAdapter createMessageViewAdapter(Context context) {
        return new MessageViewAdapter(getContext(), R.layout.layout_custom_inbox_item) {
            @Override
            protected void bindView(View view, Message message, int position) {
                LayoutCustomInboxItemBinding layoutCustomInboxItemBinding = DataBindingUtil.bind(view);
                layoutCustomInboxItemBinding.setMessage(new InboxMessage(message));
                layoutCustomInboxItemBinding.setEventsHandler((selectedMessage) ->
                    getAbsListView().setItemChecked(position, !getAbsListView().isItemChecked(position))
                );

                layoutCustomInboxItemBinding.selectionCheckbox
                        .setChecked(getAbsListView().isItemChecked(position));
            }
        };
    }
}
