package xmps.androiddebugtool.tools.colorboard;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.enjack.diyviews.CircleColorButtonView;
import com.enjack.diyviews.CircleToggle;

import xmps.androiddebugtool.factorytest.R;

/**
 * Created by enjack on 2015/8/16.
 */
public class ColorBoardActivity extends ActionBarActivity {

    private RelativeLayout layout = null;
    private CircleToggle full = null;
    private EditText etA = null;
    private EditText etR = null;
    private EditText etG = null;
    private EditText etB = null;
    private CircleColorButtonView ccbA_inc = null;
    private CircleColorButtonView ccbR_inc = null;
    private CircleColorButtonView ccbG_inc = null;
    private CircleColorButtonView ccbB_inc = null;
    private CircleColorButtonView ccbA_dec = null;
    private CircleColorButtonView ccbR_dec = null;
    private CircleColorButtonView ccbG_dec = null;
    private CircleColorButtonView ccbB_dec = null;

    private EditTextDataChanged aEditTextDataChangedListener = new EditTextDataChanged(0);
    private EditTextDataChanged rEditTextDataChangedListener = new EditTextDataChanged(1);
    private EditTextDataChanged gEditTextDataChangedListener = new EditTextDataChanged(2);
    private EditTextDataChanged bEditTextDataChangedListener = new EditTextDataChanged(3);

    private int mStep = 3;
    private int mAValue = 0;
    private int mRValue = 0;
    private int mGValue = 0;
    private int mBValue = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_board);
        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        findElements();
    }

    @Override
    public void onDestroy(){
        etA.removeTextChangedListener(aEditTextDataChangedListener);
        etR.removeTextChangedListener(rEditTextDataChangedListener);
        etG.removeTextChangedListener(gEditTextDataChangedListener);
        etB.removeTextChangedListener(bEditTextDataChangedListener);
        super.onDestroy();
    }

    private void findElements(){
        BtnListener listener = new BtnListener();
        layout = (RelativeLayout)findViewById(R.id.colorboard_bk);
        full = (CircleToggle)findViewById(R.id.colorboard_btn_full_screen);
        //full.setOnClickListener(listener);
        full.setWatcher(new CircleToggle.ToggleStateWatcher() {
            @Override
            public void onSelectChanged(boolean sel) {
                ActionBar bar = getSupportActionBar();
                if(sel && bar.isShowing())
                    bar.hide();
                else if(!sel && !bar.isShowing())
                    bar.show();
            }
        });
        etA = (EditText)findViewById(R.id.colorboard_a_et);
        etR = (EditText)findViewById(R.id.colorboard_r_et);
        etG = (EditText)findViewById(R.id.colorboard_g_et);
        etB = (EditText)findViewById(R.id.colorboard_b_et);
        etA.addTextChangedListener(aEditTextDataChangedListener);
        etR.addTextChangedListener(rEditTextDataChangedListener);
        etG.addTextChangedListener(gEditTextDataChangedListener);
        etB.addTextChangedListener(bEditTextDataChangedListener);
        ccbA_inc = (CircleColorButtonView)findViewById(R.id.colorboard_btn_a_inc);
        ccbR_inc = (CircleColorButtonView)findViewById(R.id.colorboard_btn_r_inc);
        ccbG_inc = (CircleColorButtonView)findViewById(R.id.colorboard_btn_g_inc);
        ccbB_inc = (CircleColorButtonView)findViewById(R.id.colorboard_btn_b_inc);
        ccbA_dec = (CircleColorButtonView)findViewById(R.id.colorboard_btn_a_dec);
        ccbR_dec = (CircleColorButtonView)findViewById(R.id.colorboard_btn_r_dec);
        ccbG_dec = (CircleColorButtonView)findViewById(R.id.colorboard_btn_g_dec);
        ccbB_dec = (CircleColorButtonView)findViewById(R.id.colorboard_btn_b_dec);
        ccbA_inc.setOnClickListener(listener);
        ccbR_inc.setOnClickListener(listener);
        ccbG_inc.setOnClickListener(listener);
        ccbB_inc.setOnClickListener(listener);
        ccbA_dec.setOnClickListener(listener);
        ccbR_dec.setOnClickListener(listener);
        ccbG_dec.setOnClickListener(listener);
        ccbB_dec.setOnClickListener(listener);
        mAValue = Integer.parseInt(etA.getText().toString());
        mBValue = Integer.parseInt(etB.getText().toString());
        mRValue = Integer.parseInt(etR.getText().toString());
        mGValue = Integer.parseInt(etG.getText().toString());
    }

    private class EditTextDataChanged implements TextWatcher{

        private int id = -1;

        /**
         * @param id
         * which EditText
         * */
        public EditTextDataChanged(int id){
            this.id = id;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            EditText view = null;
            switch(id){
                case 0:
                    view = etA;
                    break;
                case 1:
                    view = etR;
                    break;
                case 2:
                    view = etG;
                    break;
                case 3:
                    view = etB;
                    break;
            }

            String text = view.getText().toString();
            if(null==text || text.length()==0)
                return;
            int value = Integer.parseInt(text);
            switch(id){
                case 0:
                    mAValue = value;
                    break;
                case 1:
                    mRValue = value;
                    break;
                case 2:
                    mGValue = value;
                    break;
                case 3:
                    mBValue = value;
                    break;
            }

            if(mAValue>255 || mAValue<0 ||
                    mRValue>255 || mRValue<0 ||
                    mGValue>255 || mGValue<0 ||
                    mBValue>255 || mBValue<0)
                return;

            layout.setBackgroundColor(Color.argb(mAValue,
                    mRValue,
                    mGValue,
                    mBValue));
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    private class BtnListener implements View.OnClickListener{
        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.colorboard_btn_a_inc:
                    clicked(0, true);
                    break;
                case R.id.colorboard_btn_r_inc:
                    clicked(1, true);
                    break;
                case R.id.colorboard_btn_g_inc:
                    clicked(2, true);
                    break;
                case R.id.colorboard_btn_b_inc:
                    clicked(3, true);
                    break;
                case R.id.colorboard_btn_a_dec:
                    clicked(0, false);
                    break;
                case R.id.colorboard_btn_r_dec:
                    clicked(1, false);
                    break;
                case R.id.colorboard_btn_g_dec:
                    clicked(2, false);
                    break;
                case R.id.colorboard_btn_b_dec:
                    clicked(3, false);
                    break;
            }
        }
    }

    /**
     * ...
     * @param id
     *  which value
     *  @param plus
     *  inc or dec
     * */
    private void clicked(int id, boolean plus){
        switch(id){
            case 0:{//alpha
                if(plus)
                    mAValue+=mStep;
                else
                    mAValue-=mStep;
                break;
            }
            case 1:{//r
                if(plus)
                    mRValue+=mStep;
                else
                    mRValue-=mStep;
                break;
            }
            case 2:{//g
                if(plus)
                    mGValue+=mStep;
                else
                    mGValue-=mStep;
                break;
            }
            case 3:{//b
                if(plus)
                    mBValue+=mStep;
                else
                    mBValue-=mStep;
                break;
            }
        }

        mAValue = mAValue>255?255:mAValue;
        mRValue = mRValue>255?255:mRValue;
        mGValue = mGValue>255?255:mGValue;
        mBValue = mBValue>255?255:mBValue;
        mAValue = mAValue<0?0:mAValue;
        mRValue = mRValue<0?0:mRValue;
        mGValue = mGValue<0?0:mGValue;
        mBValue = mBValue<0?0:mBValue;

        etA.setText(String.valueOf(mAValue));
        etB.setText(String.valueOf(mBValue));
        etG.setText(String.valueOf(mGValue));
        etR.setText(String.valueOf(mRValue));
        layout.setBackgroundColor(Color.argb(mAValue,
                mRValue,
                mGValue,
                mBValue));
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
