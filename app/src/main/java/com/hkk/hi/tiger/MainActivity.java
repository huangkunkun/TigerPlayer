package com.hkk.hi.tiger;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;


public class MainActivity extends Activity {


    private ImageView mP1Iv, mP2Iv, mP3Iv, mP4Iv, mP5Iv, mP6Iv, mP7Iv, mP8Iv, mP9Iv, mP10Iv, mP11Iv, mP12Iv;
    private ImageView[] mImgArr = new ImageView[12];//使用数据存储这12张图片
    private Button mBetBtn, mStartBtn;

    private TextView moneyTv;
    private String mBetName = "";

    //下注的金币和剩余总金币
    private int mBetMoney = 0;
    private int mBetTotalMoney = 10000;

    //当前选中图片的id
    private int currentId = 0;

    //使用数据存储数据名
    private String[] mNameArr = {"苹果1", "香蕉1", "梨子", "西瓜", "猕猴桃1", "香蕉2", "苹果2", "芒果", "猕猴桃2", "草莓", "猕猴桃3", "橘子"};


    private MyHandler mHandler = new MyHandler();

    //创建定时器
    private AnimThread animThread = new AnimThread();
    private TimeThread timeThread = new TimeThread();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();//初始化控件

        checkMoney();//获取存储的金币

        mBetBtn.setOnClickListener(mClickListener);
        mStartBtn.setOnClickListener(mClickListener);

        //将图片添加到ImageView[]中
        mImgArr[0] = mP1Iv;
        mImgArr[1] = mP2Iv;
        mImgArr[2] = mP3Iv;
        mImgArr[3] = mP4Iv;
        mImgArr[4] = mP5Iv;
        mImgArr[5] = mP6Iv;
        mImgArr[6] = mP7Iv;
        mImgArr[7] = mP8Iv;
        mImgArr[8] = mP9Iv;
        mImgArr[9] = mP10Iv;
        mImgArr[10] = mP11Iv;
        mImgArr[11] = mP12Iv;
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mP1Iv = (ImageView) findViewById(R.id.p1_iv);
        mP2Iv = (ImageView) findViewById(R.id.p2_iv);
        mP3Iv = (ImageView) findViewById(R.id.p3_iv);
        mP4Iv = (ImageView) findViewById(R.id.p4_iv);
        mP5Iv = (ImageView) findViewById(R.id.p5_iv);
        mP6Iv = (ImageView) findViewById(R.id.p6_iv);
        mP7Iv = (ImageView) findViewById(R.id.p7_iv);
        mP8Iv = (ImageView) findViewById(R.id.p8_iv);
        mP9Iv = (ImageView) findViewById(R.id.p9_iv);
        mP10Iv = (ImageView) findViewById(R.id.p10_iv);
        mP11Iv = (ImageView) findViewById(R.id.p11_iv);
        mP12Iv = (ImageView) findViewById(R.id.p12_iv);

        mBetBtn = (Button) findViewById(R.id.bet_btn);
        mStartBtn = (Button) findViewById(R.id.start_btn);
        moneyTv = (TextView) findViewById(R.id.money_tv);
    }

    /**
     * 获取存储的金币，如果获取到，则将获取的金币作为总金币数，否则弹出提示第一次游戏、欢迎光临
     */
    private void checkMoney() {
        try {
            SharedPreferences pref = getSharedPreferences("money", MODE_PRIVATE);
            int mTotalMoney = pref.getInt("money", 10000);
            mBetTotalMoney = mTotalMoney;
            moneyTv.setText(mBetTotalMoney + "");
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "第一次游戏，欢迎你的光临", Toast.LENGTH_SHORT);
        }
    }

    /*
     *转盘转动开始，在此期间TimeThread保持sleep，AnimThread则一直计算序号
     */
    private class TimeThread extends Thread {
        @Override
        public void run() {
            Random random = new Random();
            int x = random.nextInt(3);
            try {
                Thread.sleep((x + 3) * 1000);//转动持续的时间
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //随机时间到后，AnimThread停止计算序号
            animThread.stopAnim();

            //发送结果给MyHandler进行处理
            Message msg = Message.obtain();
            msg.what = 2;
            mHandler.sendMessage(msg);
        }
    }


    /*
    * 在转动期间（即TimeThread保持sleep期间），一直计算序号
     */
    private class AnimThread extends Thread {

        private boolean isStopped = false;

        //停止计算序号
        public void stopAnim() {
            isStopped = true;
            animThread.interrupt();
        }

        @Override
        public void run() {
            while (!isStopped) {
                //计算当前选中图片的序号
                currentId++;
                if (currentId > 11) {
                    currentId = 0;
                }
                try {
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //发送结果给MyHandler进行处理
                Message msg = Message.obtain();
                msg.arg2 = currentId;
                msg.what = 1;
                mHandler.sendMessage(msg);

            }
        }
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (v == mBetBtn) {
                Intent intent = new Intent(MainActivity.this, BetActivity.class);
                intent.putExtra("TotalMoney", mBetTotalMoney);
                MainActivity.this.startActivityForResult(intent, 1000);
            } else if (v == mStartBtn) {

                if (TextUtils.isEmpty(mBetName) || mBetMoney <= 0) {
                    Toast.makeText(MainActivity.this, "请您先下注", Toast.LENGTH_SHORT).show();
                } else {
                     timeThread = new TimeThread();
                    timeThread.start();//转动持续时间
                     animThread = new AnimThread();
                    animThread.start();//计算当前选中图片的序号
                }
            }
        }
    };

    /**
     * 处理转盘转动过程中的界面变化
     */
    private class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            //序号计算器发送过来的消息
            if (msg.what == 1) {
                //清空所有图片背景
                for (int i = 0; i < mNameArr.length; i++) {
                    ImageView iv = mImgArr[i];
                    iv.setBackgroundColor(Color.TRANSPARENT);
                }
                //将选中图片背景设置成黄色
                int index = msg.arg2;
                ImageView selectIv = mImgArr[index];
                selectIv.setBackgroundColor(Color.YELLOW);
            }
            //随机计时器发过来的消息
            else if (msg.what == 2) {
                //压中
                if (getResult(currentId)) {
                    if (mBetName.equals("猕猴桃")) {
                        mBetTotalMoney = mBetTotalMoney + mBetMoney * 1;
                        Toast.makeText(MainActivity.this, "恭喜您中彩金 " + mBetMoney * 1, Toast.LENGTH_SHORT).show();
                    } else if (mBetName.equals("苹果")) {
                        mBetTotalMoney = mBetTotalMoney + mBetMoney * 2;
                        Toast.makeText(MainActivity.this, "恭喜您中彩金 " + mBetMoney * 2, Toast.LENGTH_SHORT).show();
                    } else if (mBetName.equals("西瓜")) {
                        mBetTotalMoney = mBetTotalMoney + mBetMoney * 3;
                        Toast.makeText(MainActivity.this, "恭喜您中彩金 " + mBetMoney * 3, Toast.LENGTH_SHORT).show();
                    } else if (mBetName.equals("草莓")) {
                        mBetTotalMoney = mBetTotalMoney + mBetMoney * 4;
                        Toast.makeText(MainActivity.this, "恭喜您中彩金 " + mBetMoney * 4, Toast.LENGTH_SHORT).show();
                    }
                    moneyTv.setText(mBetTotalMoney + "");
                }
                //押错了
                else {
                    Toast.makeText(MainActivity.this, "没中，再来一次?", Toast.LENGTH_SHORT).show();
                }
                //计算完成之后，需要将押注人名和押注金额清空
                mBetName = "";
                mBetMoney = 0;
                timeThread.interrupt();
            }
        }
    }

    /**
     * 接收由BetActivity回传过来的数据（下注金币数额和下注的对象）
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == 500) {
            mBetName = data.getStringExtra("name");
            mBetMoney = data.getIntExtra("money", 0);
            mBetTotalMoney = mBetTotalMoney - mBetMoney;
            if (mBetTotalMoney >= 0) {
                moneyTv.setText(mBetTotalMoney + "");
            } else {
                moneyTv.setText(0 + "");
            }
        }
    }


    /**
     * 比较下注的水果名和当前选择器选中的水果名是否相同，返回一个boolean型数值
     *
     * @param index
     * @return
     */
    private boolean getResult(int index) {
        String selectName = mNameArr[index];
        return selectName.equals(mBetName);
    }

    //重写onPause方法，游戏被kill掉时，保存金币数量
    @Override
    protected void onPause() {
        SharedPreferences.Editor editor = getSharedPreferences("money", MODE_PRIVATE).edit();
        editor.putInt("money", mBetTotalMoney);
        editor.commit();
        super.onPause();
    }


}
