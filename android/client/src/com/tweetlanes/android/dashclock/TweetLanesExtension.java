package com.tweetlanes.android.dashclock;

import android.content.Context;
import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;
import com.tweetlanes.android.App;
import com.tweetlanes.android.R;
import com.tweetlanes.android.model.AccountDescriptor;
import com.tweetlanes.android.view.BootActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class TweetLanesExtension extends DashClockExtension {

    public static final String PREF_NAME = "pref_name";
    final String SHARED_PREFERENCES_KEY_ACCOUNT_INDICES = "account_indices_key_v2";
    final String SHARED_PREFERENCES_KEY_NOTIFICATION_COUNT = "notification_count_";
    final String SHARED_PREFERENCES_KEY_NOTIFICATION_SUMMARY = "notification_summary_";

	@Override
	protected void onUpdateData(int arg0) {
        // Get preference value.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        int count = 0;
        String body = "";
        for(AccountDescriptor account : getAccounts(this)) {
            count += sp.getInt(SHARED_PREFERENCES_KEY_NOTIFICATION_COUNT + account.getAccountKey(), 0);
            body += (sp.getString(SHARED_PREFERENCES_KEY_NOTIFICATION_SUMMARY + account.getAccountKey(), "") + "\n");
        }
        body = body.replaceAll("\\s+#", "");

        // Publish the extension data update.
        publishUpdate(new ExtensionData().visible(count > 0).icon(R.drawable.ic_launcher).status(String.valueOf(count))
                .expandedTitle(count + " new mentions").expandedBody(body).clickIntent(new Intent(this,
                        BootActivity.class)));
        }

    private ArrayList<AccountDescriptor> getAccounts(Context context) {
        ArrayList<AccountDescriptor> accounts = new ArrayList<AccountDescriptor>();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String accountIndices = preferences.getString(SHARED_PREFERENCES_KEY_ACCOUNT_INDICES, null);

        if (accountIndices != null) {
            try {
                JSONArray jsonArray = new JSONArray(accountIndices);
                for (int i = 0; i < jsonArray.length(); i++) {
                    Long id = jsonArray.getLong(i);

                    String key = App.getAccountDescriptorKey(id);
                    String jsonAsString = preferences.getString(key, null);
                    if (jsonAsString != null) {
                        AccountDescriptor account = new AccountDescriptor(context, jsonAsString);
                        accounts.add(account);
                    }
                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return accounts;
    }
}