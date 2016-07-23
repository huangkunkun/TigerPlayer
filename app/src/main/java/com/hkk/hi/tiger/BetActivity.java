package com.hkk.hi.tiger;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BetActivity extends Activity {

    private int mMoney = 0;
    private String nName = "";
    private ListView mBetLv;
    private String[] mScoreArr = {"1 : 1", "1 : 2", "1 : 3", "1 : 4", ""};//中奖赔率
    private String[] mNameArr = {"猕猴桃", "苹果", "西瓜", "草莓"};//可以中奖的水果名
    private int[] mImgArr = {R.mipmap.kiwifruit, R.mipmap.apple, R.mipmap.watermelon, R.mipmap.strawberry};//可以中奖的水果图片id
    private Button mBtn100, mBtn200, mBtn500, mBtn1000, mConfirmBtn;
    private EditText mEdtMoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bet_layout);

        initView();//初始化控件

        mBtn100.setOnClickListener(mClickListener);
        mBtn200.setOnClickListener(mClickListener);
        mBtn500.setOnClickListener(mClickListener);
        mBtn1000.setOnClickListener(mClickListener);
        mConfirmBtn.setOnClickListener(mClickListener);

        SimpleAdapter sAdapter = new SimpleAdapter(BetActivity.this, getData(), R.layout.item,
                new String[]{"score", "name", "img"}, new int[]{R.id.score_tv, R.id.text_tv, R.id.image_iv});
        mBetLv.setAdapter(sAdapter);
        mBetLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                nName = mNameArr[position];
            }
        });
    }

    //初始化控件
    private void initView() {
        mBtn100 = (Button) findViewById(R.id.btn1);
        mBtn200 = (Button) findViewById(R.id.btn2);
        mBtn500 = (Button) findViewById(R.id.btn3);
        mBtn1000 = (Button) findViewById(R.id.btn4);
        mConfirmBtn = (Button) findViewById(R.id.btn5);
        mEdtMoney = (EditText) findViewById(R.id.money_et);

        mBetLv = (ListView) findViewById(R.id.lv1);
    }

    //准备数据源
    private List<Map<String, Object>> getData() {

        List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();

        for (int i = 0; i < 4; i++) {
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("score", mScoreArr[i]);
            data.put("name", mNameArr[i]);
            data.put("img", mImgArr[i]);
            dataList.add(data);
        }
        return dataList;
    }

    private int money = 0;//下注金额
    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == mBtn1000) {
                money = 1000;
                mBtn1000.setBackgroundColor(Color.YELLOW);
                mBtn500.setBackgroundColor(Color.TRANSPARENT);
                mBtn200.setBackgroundColor(Color.TRANSPARENT);
                mBtn100.setBackgroundColor(Color.TRANSPARENT);
                mEdtMoney.setText("");
            } else if (v == mBtn500) {
                money = 500;
                mBtn500.setBackgroundColor(Color.YELLOW);
                mBtn1000.setBackgroundColor(Color.TRANSPARENT);
                mBtn200.setBackgroundColor(Color.TRANSPARENT);
                mBtn100.setBackgroundColor(Color.TRANSPARENT);
                mEdtMoney.setText("");
            } else if (v == mBtn200) {
                money = 200;
                mBtn200.setBackgroundColor(Color.YELLOW);
                mBtn500.setBackgroundColor(Color.TRANSPARENT);
                mBtn1000.setBackgroundColor(Color.TRANSPARENT);
                mBtn100.setBackgroundColor(Color.TRANSPARENT);
                mEdtMoney.setText("");
            } else if (v == mBtn100) {
                money = 100;
                mBtn100.setBackgroundColor(Color.YELLOW);
                mBtn500.setBackgroundColor(Color.TRANSPARENT);
                mBtn200.setBackgroundColor(Color.TRANSPARENT);
                mBtn1000.setBackgroundColor(Color.TRANSPARENT);
                mEdtMoney.setText("");
            } else if (v == mConfirmBtn) {

                mMoney = money;

                String edtMoney = mEdtMoney.getText().toString();//获取输入的金额
                judgeEdit(edtMoney);//判断是否输入金额，若是，则取消之前选中的金额

                Intent t = getIntent();
                int totalMoney = t.getIntExtra("TotalMoney", 0);
                /**
                 * 判断金币数量，如果金币不足会弹出提醒
                 */
                if (totalMoney < mMoney) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(BetActivity.this);
                    dialog.setTitle("请注意");
                    dialog.setMessage("您的押注金额超过剩余总金额，请重新押注");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    dialog.show();
                }
                else if (!TextUtils.isEmpty(nName) && mMoney > 0 ) {
                    Intent intent = new Intent();
                    intent.putExtra("name", nName);
                    intent.putExtra("money", mMoney);
                    BetActivity.this.setResult(500, intent);
                    BetActivity.this.finish();
                } else {
                    Toast.makeText(BetActivity.this, "未押注或押注金额有误", Toast.LENGTH_SHORT).show();
                }
            }

        }

    };

    private  void judgeEdit(String m){
        if (!TextUtils.isEmpty(m)) {
            int inputMoney = Integer.parseInt(m);
            if (inputMoney > 0) {
                mMoney = inputMoney;
            }
        }
    }
}
