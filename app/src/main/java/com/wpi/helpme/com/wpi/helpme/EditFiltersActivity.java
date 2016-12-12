package com.wpi.helpme.com.wpi.helpme;

import android.database.DataSetObserver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.wpi.helpme.HelpMeApplication;
import com.wpi.helpme.R;

import java.util.ArrayList;
import java.util.List;

public class EditFiltersActivity extends AppCompatActivity {
    private ListView filterListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_filters);

        filterListView = (ListView) this.findViewById(R.id.filter_list_view);

        ArrayList<String> filters = new ArrayList<>();
        filters.addAll(HelpMeApplication.getInstance().getUserProfile().getFilters());
        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filters);
        filterListView.setAdapter(adapter);
    }
}
