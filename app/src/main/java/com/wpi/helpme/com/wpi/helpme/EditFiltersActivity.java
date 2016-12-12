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
import android.widget.TextView;

import com.wpi.helpme.HelpMeApplication;
import com.wpi.helpme.R;

import java.util.ArrayList;

public class EditFiltersActivity extends AppCompatActivity {
    private static final String TAG = "EditFiltersActivity";
    private ListView filterListView;
    private ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_filters);

        filterListView = (ListView) this.findViewById(R.id.filter_list_view);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>(HelpMeApplication.getInstance().getUserProfile().getFilters()));
        filterListView.setAdapter(arrayAdapter);

        filterListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int itemIndex, long l) {
                Log.d(TAG, "Item: " + adapterView.getItemAtPosition(itemIndex).toString());

                LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                View dialogView = inflater.inflate(R.layout.edit_filter_dialog, null);

                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(EditFiltersActivity.this);
                dialogBuilder.setView(dialogView);
                dialogBuilder.setTitle("Modify Filter");

                final EditText textView = (EditText) dialogView
                        .findViewById(R.id.edit_filter_dialog_text);

                dialogBuilder.setCancelable(true)
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Log.d(TAG, "Clicked save with text: " + textView.getText());
                                updateFilterText(itemIndex, textView.getText().toString());
                            }
                        })
                        .setNegativeButton("Remove", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Log.d(TAG, "Clicked remove on dialog.");
                                removeFilter(itemIndex);
                            }
                        });

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.edit_filters_settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.edit_filters_settings_add_filter:
                Log.d(TAG, "Adding new item...");
                HelpMeApplication.getInstance().getUserProfile().getFilters().add("New Filter Item");
                updateFiltersView();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void removeFilter(int filterIndex) {
        HelpMeApplication.getInstance().getUserProfile().getFilters().remove(filterIndex);
        updateFiltersView();
    }

    private void updateFilterText(int filterIndex, String newFilter) {
        HelpMeApplication.getInstance().getUserProfile().getFilters().set(filterIndex, newFilter);
        updateFiltersView();
    }

    private void updateFiltersView() {
        arrayAdapter.clear();
        arrayAdapter.addAll(HelpMeApplication.getInstance().getUserProfile().getFilters());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        HelpMeApplication.getInstance().syncProfileToDatabase();
    }
}
