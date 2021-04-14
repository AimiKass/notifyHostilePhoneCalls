package com.example.notifyhostilephonecalls.fragments;

import android.app.Activity;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.notifyhostilephonecalls.R;
import com.example.notifyhostilephonecalls.activities.MainActivity;
import com.example.notifyhostilephonecalls.adapters.SettingsArrayAdapter;
import com.example.notifyhostilephonecalls.utils.Permissions;
import com.example.notifyhostilephonecalls.utils.Settings;


/**
 * Settings fragment
 */
public class SettingsFragment extends Fragment implements FragmentArguments
{
    private static final int DEFAULT_SMS_APP = 1;
    private static final int BLOCKED_SMS = 2;
    private static final int RECEIVED_SMS = 3;
    private static final int BLOCKED_CALL = 4;
    private SettingsArrayAdapter adapter = null;
    private ListView listView = null;
    private int listPosition = 0;

    public SettingsFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        // set activity title
        Bundle arguments = getArguments();
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (arguments != null && actionBar != null)
        {
            actionBar.setTitle(arguments.getString(TITLE));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if (savedInstanceState != null)
        {
            listPosition = savedInstanceState.getInt(LIST_POSITION, 0);
        } else
        {
            Bundle arguments = getArguments();
            if (arguments != null)
            {
                listPosition = arguments.getInt(LIST_POSITION, listPosition);
            }
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
//        Permissions.notifyIfNotGranted(getContext(), Permissions.WRITE_EXTERNAL_STORAGE);

        listView = (ListView) view.findViewById(R.id.settings_list);
        loadListViewItems(listPosition);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt(LIST_POSITION, listView.getFirstVisiblePosition());
    }

    @Override
    public void onPause()
    {
        super.onPause();
        listPosition = listView.getFirstVisiblePosition();
    }

    // Is used for getting result of ringtone picker dialog & default sms app dialog
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            // default sms app dialog result
            case DEFAULT_SMS_APP:
                if (resultCode == Activity.RESULT_OK)
                {
                    Permissions.invalidateCache();
                }
                // reload list
                reloadListViewItems();
                break;
            // ringtone picker dialog results
            default:
                // get ringtone url
                Uri uri = null;
                if (resultCode == Activity.RESULT_OK && data != null)
                {
                    uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                }
                // save url as settings property value
//                setRingtoneUri(requestCode, uri);
                break;
        }
    }

    // Reloads the Settings list
    private void reloadListViewItems()
    {
        listPosition = listView.getFirstVisiblePosition();
        loadListViewItems(listPosition);
    }

    // Loads the Settings list
    private void loadListViewItems(final int listPosition)
    {
        // Create list adapter and fill it with data
        adapter = new SettingsArrayAdapter(getContext());




        // calls blocking settings
        adapter.addTitle(R.string.Calls_blocking);
        adapter.addCheckbox(R.string.All_calls, R.string.Block_all_calls, Settings.BLOCK_ALL_CALLS);
        adapter.addCheckbox(R.string.Black_list, R.string.Block_calls_from_black_list, Settings.BLOCK_CALLS_FROM_BLACK_LIST);
        adapter.addCheckbox(R.string.Hostile_calls,R.string.Autoblock_incoming_hostile_calls,Settings.AUTO_BLOCK_HOSTILE_CALLS);
//        adapter.addCheckbox(R.string.Call_log, R.string.Remove_from_call_log, Settings.REMOVE_FROM_CALL_LOG);

        // calls notifications settings
        adapter.addTitle(R.string.Calls_blocking_notification);
        adapter.addCheckbox(R.string.Notify_blocked_calls, R.string.Notify_in_status_bar_blocked_call, Settings.BLOCKED_CALL_STATUS_NOTIFICATION, new DependentRowOnClickListener());
        adapter.addCheckbox(R.string.Notify_hostile_calls,R.string.Notify_in_status_bar_hostile_call,Settings.HOSTILE_CALL_STATUS_NOTIFICATION, new DependentRowOnClickListener());
//        adapter.addCheckbox(R.string.Sound, R.string.Notify_with_sound_blocked_call, Settings.BLOCKED_CALL_SOUND_NOTIFICATION, new RingtonePickerOnClickListener(BLOCKED_CALL));

        // app interface
        adapter.addTitle(R.string.App_interface);
        adapter.addCheckbox(R.string.UI_theme_dark, 0, Settings.UI_THEME_DARK, new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                restartApp();
            }
        });





        // add adapter to the ListView and scroll list to position
        listView.setAdapter(adapter);
        listView.post(new Runnable()
        {
            @Override
            public void run()
            {
                listView.setSelection(listPosition);
            }
        });
    }

    // Saves ringtone url as settings property value
//    private void setRingtoneUri(int requestCode, @Nullable Uri uri)
//    {
//        String ringtoneProperty, soundProperty, statusProperty = null;
//        switch (requestCode)
//        {
//            case BLOCKED_CALL:
////                ringtoneProperty = Settings.BLOCKED_CALL_RINGTONE;
//                soundProperty = Settings.BLOCKED_CALL_SOUND_NOTIFICATION;
//                statusProperty = Settings.BLOCKED_CALL_STATUS_NOTIFICATION;
//                break;
////            case BLOCKED_SMS:
////                ringtoneProperty = Settings.BLOCKED_SMS_RINGTONE;
////                soundProperty = Settings.BLOCKED_SMS_SOUND_NOTIFICATION;
////                statusProperty = Settings.BLOCKED_SMS_STATUS_NOTIFICATION;
////                break;
////            case RECEIVED_SMS:
////                ringtoneProperty = Settings.RECEIVED_SMS_RINGTONE;
////                soundProperty = Settings.RECEIVED_SMS_SOUND_NOTIFICATION;
////                break;
//            default:
//                return;
//        }
//
//        if (uri != null)
//        {
//            Settings.setStringValue(getContext(), ringtoneProperty, uri.toString());
//            adapter.setRowChecked(soundProperty, true);
//            if (statusProperty != null)
//            {
//                // if we enable ringtone we must enable status bar notification
//                adapter.setRowChecked(statusProperty, true);
//            }
//        } else
//        {
//            adapter.setRowChecked(soundProperty, false);
//        }
//    }

    // Returns ringtone url from settings property
//    @Nullable
//    private Uri getRingtoneUri(int requestCode)
//    {
//        String uriString = null;
//        switch (requestCode)
//        {
//            case BLOCKED_CALL:
//                uriString = Settings.getStringValue(getContext(), Settings.BLOCKED_CALL_RINGTONE);
//                break;
//            case BLOCKED_SMS:
//                uriString = Settings.getStringValue(getContext(), Settings.BLOCKED_SMS_RINGTONE);
//                break;
//            case RECEIVED_SMS:
//                uriString = Settings.getStringValue(getContext(), Settings.RECEIVED_SMS_RINGTONE);
//                break;
//        }
//
//        return (uriString != null ? Uri.parse(uriString) : null);
//    }
//




    // Shows toast
    private void toast(@StringRes int messageId)
    {
        Toast.makeText(getContext(), messageId, Toast.LENGTH_SHORT).show();
    }

    // Restarts the current app and opens settings fragment
    private void restartApp()
    {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(MainActivity.ACTION_SETTINGS);
        intent.putExtra(LIST_POSITION, listView.getFirstVisiblePosition());
        startActivity(intent);
        getActivity().finish();
    }



//    // On row click listener for opening ringtone picker
//    private class RingtonePickerOnClickListener implements View.OnClickListener
//    {
//        int requestCode;
//
//        RingtonePickerOnClickListener(int requestCode)
//        {
//            this.requestCode = requestCode;
//        }
//
//        @Override
//        public void onClick(View view)
//        {
//            if (isAdded())
//            {
//                if (adapter.isRowChecked(view))
//                {
//                    // open ringtone picker dialog
//                    Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
//                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
////                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, getRingtoneUri(requestCode));
//                    startActivityForResult(intent, requestCode);
//                }
//            } else
//            {
//                adapter.setRowChecked(view, false);
//            }
//        }
//    }

    // On row click listener for updating dependent rows
    private class DependentRowOnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View view)
        {
            String property = adapter.getRowProperty(view);
            if (property == null)
            {
                return;
            }

            boolean checked = adapter.isRowChecked(view);
            if (!checked)
            {
                // if row was unchecked - reset dependent rows
                switch (property)
                {
//                    case Settings.BLOCKED_SMS_STATUS_NOTIFICATION:
//                        adapter.setRowChecked(Settings.BLOCKED_SMS_SOUND_NOTIFICATION, false);
//                        adapter.setRowChecked(Settings.BLOCKED_SMS_VIBRATION_NOTIFICATION, false);
//                        break;
                    case Settings.BLOCKED_CALL_STATUS_NOTIFICATION:
                        adapter.setRowChecked(Settings.BLOCKED_CALL_SOUND_NOTIFICATION, false);
//                        adapter.setRowChecked(Settings.BLOCKED_CALL_VIBRATION_NOTIFICATION, false);
                        break;
                }
            } else
            {
                switch (property)
                {
//                    case Settings.BLOCKED_SMS_SOUND_NOTIFICATION:
//                    case Settings.BLOCKED_SMS_VIBRATION_NOTIFICATION:
//                        adapter.setRowChecked(Settings.BLOCKED_SMS_STATUS_NOTIFICATION, true);
//                        break;
                    case Settings.BLOCKED_CALL_SOUND_NOTIFICATION:
//                    case Settings.BLOCKED_CALL_VIBRATION_NOTIFICATION:
                        adapter.setRowChecked(Settings.BLOCKED_CALL_STATUS_NOTIFICATION, true);
                        break;
                }
            }
        }
    }
}