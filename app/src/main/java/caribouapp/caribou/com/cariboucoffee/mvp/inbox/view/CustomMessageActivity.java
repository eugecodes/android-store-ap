package caribouapp.caribou.com.cariboucoffee.mvp.inbox.view;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.urbanairship.Autopilot;
import com.urbanairship.UAirship;
import com.urbanairship.messagecenter.Inbox;
import com.urbanairship.messagecenter.InboxListener;
import com.urbanairship.messagecenter.Message;
import com.urbanairship.messagecenter.MessageFragment;

import javax.inject.Inject;

import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.SourceApplication;
import caribouapp.caribou.com.cariboucoffee.analytics.AppScreen;
import caribouapp.caribou.com.cariboucoffee.common.BaseActivity;
import caribouapp.caribou.com.cariboucoffee.databinding.ActivityCustomMessageBinding;
import caribouapp.caribou.com.cariboucoffee.util.Log;
import caribouapp.caribou.com.cariboucoffee.util.LogErrorException;

public class CustomMessageActivity extends BaseActivity<ActivityCustomMessageBinding> {

    private static final String MESSAGE_FRAGMENT_TAG = MessageFragment.class.getName();

    @Inject
    Inbox richPushInbox;

    private String messageId;
    private InboxListener updateMessageListener = () -> updateTitle();

    protected void onCreate(Bundle savedInstanceState) {
        SourceApplication.get(this).getComponent().inject(this);

        super.onCreate(savedInstanceState);

        setSupportActionBar(getBinding().tb);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Autopilot.automaticTakeOff(getApplication());
        if (!UAirship.isTakingOff() && !UAirship.isFlying()) {
            Log.e("MessageActivity", new LogErrorException("Unable to create activity, takeOff not called."));
            finish();
        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if (savedInstanceState == null) {
                messageId = parseMessageId(getIntent());
            } else {
                messageId = savedInstanceState.getString(UAConstants.MESSAGE_ID_KEY);
            }

            if (messageId == null) {
                finish();
            } else {
                loadMessage();
            }
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_custom_message;
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(UAConstants.MESSAGE_ID_KEY, messageId);
    }

    private void loadMessage() {
        if (messageId != null) {
            MessageFragment previousMessageFragment = (MessageFragment) getSupportFragmentManager().findFragmentByTag(MESSAGE_FRAGMENT_TAG);

            if (previousMessageFragment == null || !messageId.equals(previousMessageFragment.getMessageId())) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                if (previousMessageFragment != null) {
                    transaction.remove(previousMessageFragment);
                }

                transaction.add(R.id.container, MessageFragment.newInstance(messageId), MESSAGE_FRAGMENT_TAG).commitNow();
            }

            updateTitle();
        }
    }

    protected void onStart() {
        super.onStart();
        richPushInbox.addListener(updateMessageListener);
    }

    protected void onStop() {
        super.onStop();
        richPushInbox.removeListener(updateMessageListener);
    }

    @Override
    protected AppScreen getScreenName() {
        return AppScreen.INBOX;
    }

    private void updateTitle() {
        if (messageId == null) {
            return;
        }

        Message message = richPushInbox.getMessage(messageId);

        if (message == null) {
            //TODO review if it works with just one call
            getBinding().setTitle(null);
            setTitle(null);
        } else {
            getBinding().setTitle(message.getTitle());
            setTitle(message.getTitle());
        }
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String newMessageId = parseMessageId(intent);
        if (newMessageId != null) {
            messageId = newMessageId;
            loadMessage();
        }
    }

    @Nullable
    private static String parseMessageId(Intent intent) {
        if (intent != null && intent.getData() != null && intent.getAction() != null) {
            String messageId = null;
            if (UAConstants.ACTION_VIEW_RICH_PUSH_MESSAGE.equals(intent.getAction())) {
                messageId = intent.getData().getSchemeSpecificPart();
            }

            return messageId;
        } else {
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.custom_message_menu, menu);
        MenuItem deleteItem = menu.findItem(R.id.action_delete);
        if (deleteItem != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                deleteItem.setContentDescription(getString(R.string.delete_cd));
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            Message message = richPushInbox.getMessage(messageId);
            if (message != null) {
                message.delete();
            }
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
