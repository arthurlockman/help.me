package com.wpi.helpme.com.wpi.helpme;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.wpi.helpme.HelpMeApplication;
import com.wpi.helpme.R;

import java.util.ArrayList;

/**
 * This activity handles displaying and letting the user edit their filters for help request that
 * may be sent out around them.
 */
public class EditFiltersActivity extends AppCompatActivity {
    private static final String TAG = "EditFiltersActivity";
    private ListView filterListView;
    private ArrayAdapter arrayAdapter;

    /**
     * @see {@link AppCompatActivity#onCreate(Bundle))
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_filters);

        // Make list view
        filterListView = (ListView) this.findViewById(R.id.filter_list_view);

        // Fill list with filter strings
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                new ArrayList<>(HelpMeApplication.getInstance().getUserProfile().getFilters()));
        filterListView.setAdapter(arrayAdapter);

        // Listener for clicking on any of the filter items
        filterListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * @see {@link android.widget.AdapterView.OnItemClickListener#onItemClick(AdapterView, View, int, long)}
             */
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int itemIndex,
                                    long l) {
                Log.d(TAG, "Item selected: " + adapterView.getItemAtPosition(itemIndex).toString());

                // Inflate the dialog
                LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                View dialogView = inflater.inflate(R.layout.edit_filter_dialog, null);

                // Build dialog using this activity as the basis
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(
                        EditFiltersActivity.this);
                dialogBuilder.setView(dialogView);
                dialogBuilder.setTitle(getString(R.string.edit_filter_edit_dialog_title));

                // Get editable text field of dialog
                final EditText textView = (EditText) dialogView
                        .findViewById(R.id.edit_filter_dialog_text);

                // Populate text field with selected filter text
                textView.setText(adapterView.getItemAtPosition(itemIndex).toString());
                textView.selectAll();

                // Add listeners for accepting or rejecting changes
                dialogBuilder.setCancelable(true)
                        .setPositiveButton(getString(R.string.edit_filter_save_filter_text),
                                new DialogInterface.OnClickListener() {
                                    /**
                                     * @see {@link android.content.DialogInterface.OnClickListener#onClick(DialogInterface, int)}
                                     */
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        // Save new filter name
                                        Log.d(TAG, "Clicked save with text: " + textView.getText());
                                        updateFilterText(itemIndex, textView.getText().toString());
                                    }
                                })
                        .setNegativeButton(getString(R.string.edit_filter_remove_filter_text),
                                new DialogInterface.OnClickListener() {
                                    /**
                                     * @see {@link android.content.DialogInterface.OnClickListener#onClick(DialogInterface, int)}
                                     */
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        // Remove filter from list
                                        Log.d(TAG,
                                                "Removed filter: " + textView.getText().toString());
                                        removeFilter(itemIndex);
                                    }
                                });

                // Start runnable to display dialog on UI thread
                Runnable showDialog = new Runnable() {
                    @Override
                    public void run() {
                        if (!isFinishing()) {
                            AlertDialog dialog = dialogBuilder.create();
                            dialog.show();
                        }
                    }
                };

                runOnUiThread(showDialog);
            }
        });
    }

    /**
     * Updates the filter that was selected with the new text that was entered.
     *
     * @param filterIndex
     *         The item index that was clicked on to be modified.
     * @param newFilter
     *         The new filter name that was entered.
     */
    private void updateFilterText(int filterIndex, String newFilter) {
        HelpMeApplication.getInstance().getUserProfile().getFilters().set(filterIndex, newFilter);
        updateFiltersView();
    }

    /**
     * Removes the filter at the specified index.
     *
     * @param filterIndex
     *         The item index that was clicked on to be removed.
     */
    private void removeFilter(int filterIndex) {
        HelpMeApplication.getInstance().getUserProfile().getFilters().remove(filterIndex);
        updateFiltersView();
    }

    /**
     * Updates the list view by retrieving the updated profile filters.
     */
    private void updateFiltersView() {
        // Clear items and add new set of filters
        arrayAdapter.clear();
        arrayAdapter.addAll(HelpMeApplication.getInstance().getUserProfile().getFilters());
    }

    /**
     * @see {@link AppCompatActivity#onDestroy()}
     */
    @Override
    public void onDestroy() {
        super.onDestroy();

        // Sync profile when closing this activity so users do not have to manually save
        HelpMeApplication.getInstance().syncProfileToDatabase();
    }

    /**
     * @see AppCompatActivity#onCreateOptionsMenu(Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.edit_filters_settings_menu, menu);
        return true;
    }

    /**
     * @see {@link AppCompatActivity#onOptionsItemSelected(MenuItem)}
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_filters_settings_add_filter:
                // Add new filter item
                Log.d(TAG, "Adding new item...");
                HelpMeApplication.getInstance().getUserProfile().getFilters()
                        .add(getString(R.string.edit_filter_new_filter_default_text));
                updateFiltersView();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
