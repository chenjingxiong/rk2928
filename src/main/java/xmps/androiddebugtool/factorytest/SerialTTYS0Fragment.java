package xmps.androiddebugtool.factorytest;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import xmps.androiddebugtool.factorytest.chain.BaseTestItemFragment;
import xmps.androiddebugtool.factorytest.chain.ItemDescription;

/**
 * Created by enjack on 2015/8/14.
 */
public class SerialTTYS0Fragment extends BaseTestItemFragment {

    private String tag = "<SerialTTYS0Fragment>";
    private View view = null;
    private Spinner spinner = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        view = inflater.inflate(R.layout.fm_ttys, container, false);
        TextView tv = (TextView)view.findViewById(R.id.title);
        tv.setText("ttyS0");

        findElements();
        return view;
    }

    private void findElements(){
        spinner = (Spinner)view.findViewById(R.id.ttys_sp_baundrate);
        List<String> list = new ArrayList<>();
        list.add("115200");
        list.add("9600");
        list.add("4800");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 0:
                        Log.i(tag, "115200 selected.");
                        break;
                    case 1:
                        Log.i(tag, "9600 selected.");
                        break;
                    case 2:
                        Log.i(tag, "4800 selected.");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public ItemDescription getItemDescription() {
        ItemDescription item = new ItemDescription();
        item.board = "通用";
        item.title = "ttyS0";
        item.desc = "ttyS0收发测试";
        return item;
    }
}
