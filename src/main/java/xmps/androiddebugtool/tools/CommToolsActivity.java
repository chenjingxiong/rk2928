package xmps.androiddebugtool.tools;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import xmps.androiddebugtool.factorytest.R;
import xmps.androiddebugtool.tools.colorboard.ColorBoardActivity;

/**
 * Created by enjack on 2015/8/16.
 */
public class CommToolsActivity extends ActionBarActivity {
    private ArrayAdapter<String> adapter;
    private ListView itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comm_tools);
        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        initAdapter();
        itemList = (ListView)findViewById(R.id.comm_tools_list);
        itemList.setAdapter(adapter);
        itemList.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                switch(position){
                    case 0:
                        Intent intent = new Intent(CommToolsActivity.this, ColorBoardActivity.class);
                        startActivity(intent);
                        break;
                }
            }});
    }

    private void initAdapter(){
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1);
        adapter.add("调色板");	//0
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        else if(id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
