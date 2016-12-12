package com.wpi.helpme.com.wpi.helpme;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.wpi.helpme.HelpMeApplication;
import com.wpi.helpme.LoginActivity;
import com.wpi.helpme.R;

import java.util.ArrayList;

public class EditFiltersActivity extends AppCompatActivity {
    private static final String TAG = "EditFiltersActivity";
    private ListView filterListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_filters);

        filterListView = (ListView) this.findViewById(R.id.filter_list_view);
        ArrayList<String> filters = new ArrayList<>();
        filters.addAll(HelpMeApplication.getInstance().getUserProfile().getFilters());
        final ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                filters);
        filterListView.setAdapter(adapter);

        filterListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int itemIndex, long l) {
                Log.d(TAG, "Item: " + adapterView.getItemAtPosition(itemIndex).toString());

                LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                View dialogView = inflater.inflate(R.layout.edit_filter_dialog, null);

                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(EditFiltersActivity.this);
                dialogBuilder.setView(dialogView);

                final EditText textView = (EditText) dialogView
                        .findViewById(R.id.edit_filter_dialog_text);

                dialogBuilder.setCancelable(true)
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Log.d(TAG, "Clicked save with text: " + textView.getText());
                                HelpMeApplication.getInstance().getUserProfile().getFilters().set(itemIndex, textView.getText().toString());
                                adapter.clear();
                                adapter.addAll(HelpMeApplication.getInstance().getUserProfile().getFilters());
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Log.d(TAG, "Clicked cancel with text: " + textView.getText());
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
    public void onDestroy() {
        super.onDestroy();
        HelpMeApplication.getInstance().syncProfileToDatabase();
    }
}
