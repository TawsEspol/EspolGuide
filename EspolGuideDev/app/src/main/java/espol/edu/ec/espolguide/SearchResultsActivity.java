package espol.edu.ec.espolguide;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Locale;

import espol.edu.ec.espolguide.controllers.adapters.RouteAdapter;
import espol.edu.ec.espolguide.controllers.adapters.SearchViewAdapter;
import espol.edu.ec.espolguide.utils.IntentHelper;

public class SearchResultsActivity extends AppCompatActivity {
    private String from;
    private String initialText;
    private RouteAdapter adapter;
    private ViewHolder viewHolder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        Bundle b = new Bundle();
        Intent intent = getIntent();
        this.viewHolder = new ViewHolder();
        b = intent.getExtras();
        if(b.containsKey("from")){
            this.from = b.getString("from");
        }
        if(b.containsKey("text")){
            this.initialText = b.getString("text");
        }

        this.viewHolder.editSearch.setText(initialText);
        this.viewHolder.editSearch.setSelection(this.viewHolder.editSearch.getText().length());
        ArrayList<String> namesItems = (ArrayList<String>) IntentHelper.getObjectForKey("namesItems");
        System.out.println("Numero de bloques: " + namesItems.size());
        this.adapter = new RouteAdapter(namesItems);
        this.adapter.setInflater(this);
        this.adapter.setBar(this.viewHolder.editSearch);
        this.viewHolder.searchPoiLv.setAdapter(adapter);

        this.viewHolder.editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewHolder.searchPoiLv.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = viewHolder.editSearch.getText().toString().toLowerCase(Locale.getDefault());
                adapter.filter(text);
            }
        });
    }

    public class ViewHolder {
        public ListView searchPoiLv;
        public EditText editSearch;


        public ViewHolder() {
            findViews();
        }

        private void findViews() {

            editSearch = (EditText) findViewById(R.id.search_destiny);
            searchPoiLv = (ListView) findViewById(R.id.listview);
        }
    }
}
