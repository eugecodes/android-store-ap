package caribouapp.caribou.com.cariboucoffee.mvp.inbox.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.common.BaseActivity;
import caribouapp.caribou.com.cariboucoffee.databinding.ActivityCustomMessageListBinding;

public class CustomMessageListActivity extends BaseActivity<ActivityCustomMessageListBinding> {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_custom_message_list;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(getBinding().tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        CustomMessageListFragment messageListFragment = new CustomMessageListFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.inbox_container, messageListFragment)
                .commit();


        processIntent(getIntent());
    }

    private void processIntent(Intent intent) {
        String messageId = getMessageId(intent);
        if (messageId != null) {
            Intent messageIntent = new Intent(this, CustomMessageActivity.class);
            messageIntent.setAction(UAConstants.ACTION_VIEW_RICH_PUSH_MESSAGE);
            messageIntent.setData(intent.getData());
            startActivity(messageIntent);
        }
    }

    private String getMessageId(Intent intent) {
        if (intent == null || intent.getData() == null || intent.getAction() == null) {
            return null;
        }

        String action = intent.getAction();
        switch (action) {
            case UAConstants.ACTION_VIEW_RICH_PUSH_INBOX:
            case UAConstants.ACTION_VIEW_RICH_PUSH_MESSAGE:
                return intent.getData().getSchemeSpecificPart();
            default:
                return null;
        }
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        processIntent(intent);
    }
}
