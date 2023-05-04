package com.example.leddemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.handheld.uhfr.UHFRManager;
import com.uhf.api.cls.Reader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.pda.serialport.Tools;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout layout1;
    private LinearLayout layout2;
    private TabHost tabHost;
    private ListView listView,listView1;
    private EditText editText;
    private Button btnInventory;
    private Button btnStop;
    private Button btnFind;
    private Button btnEnd;
    private DBHelper dbHelper1;
    private SQLiteDatabase sql;
    private int tab = 1;
    private List<listInfo> listdata = new ArrayList<listInfo>();
    private List<String> filterepc = new ArrayList<String>();
    private LedAdapter ledAdapter;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    String epc = msg.getData().getString("epc");
                    if (filterepc.contains(epc)) {
                        return;
                    } else {
                        find(epc);
                        filterepc.add(epc);
                    }
                    if (ledAdapter==null) {
                        ledAdapter = new LedAdapter(listdata, MainActivity.this);
                    } else {
                        ledAdapter.notifyDataSetChanged();
                    }
                    if (tab==1) {
                        listView.setAdapter(ledAdapter);
                    } else {
                        listView1.setAdapter(ledAdapter);
                    }
                    break;

            }
        }
    };

    private void clear() {
        filterepc.clear();
        listdata.clear();
        if (ledAdapter==null) {
            ledAdapter = new LedAdapter(listdata,MainActivity.this);
        }
        ledAdapter.notifyDataSetChanged();
    }

    public void find(String barcode) {
        Cursor cursor = sql.query("user", new String[]{"id","typeA","typeB","unit"}, "epc = ?", new String[]{barcode}, null, null, null);
        while(cursor.moveToNext()) {
            @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex("id"));
            @SuppressLint("Range") String typeA = cursor.getString(cursor.getColumnIndex("typeA"));
            @SuppressLint("Range") String typeB = cursor.getString(cursor.getColumnIndex("typeB"));
            @SuppressLint("Range") String unit = cursor.getString(cursor.getColumnIndex("unit"));
            if (id==null||typeA==null||typeB==null||unit==null) {
                continue;
            }
            listInfo info = new listInfo(id,typeA,typeB,unit);
            listdata.add(info);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSharedPreferences = this.getSharedPreferences("UHF", MODE_PRIVATE);
        initview();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if(!Environment.isExternalStorageManager()){
                startActivity(new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION));
            }
        }
        dbHelper1 = new DBHelper(MainActivity.this, "test_db", null, 1);
        sql = dbHelper1.getWritableDatabase();
        UtilSound.initSoundPool(this);
    }
    private void initview() {
        listView = (ListView) findViewById(R.id.listview);
        listView1 = (ListView) findViewById(R.id.listview1);
        btnInventory = (Button) findViewById(R.id.button_inventory);
        btnFind = (Button) findViewById(R.id.button_find);
        btnStop = (Button) findViewById(R.id.button_stop);
        editText = (EditText) findViewById(R.id.edit_query);
        btnEnd = (Button) findViewById(R.id.button_end);
        btnEnd.setOnClickListener(this);
        btnInventory.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        btnFind.setOnClickListener(this);
        //初始化容器
        layout1 = (LinearLayout) findViewById(R.id.tab1);
        layout2 = (LinearLayout) findViewById(R.id.tab2);
        tabHost = (TabHost) findViewById(R.id.tabhost);
        tabHost.setup();
        //创建标签，设置标题/图标/页面布局
        tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("盘存",null).setContent(R.id.tab1));
        tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("寻线",null).setContent(R.id.tab2));
        //标签切换事件处理，setOnTabChangedListener
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener(){
            @Override
            // tabId是newTabSpec参数设置的tab页名，并不是layout里面的标识符id
            public void onTabChanged(String tabId) {
                if (tabId.equals("tab1")) {   //第一个标签
                    tab = 1;
                }
                if (tabId.equals("tab2")) {   //第二个标签
                    tab = 2;
                }
                Log.e("TAG", "onTabChanged: tab" + tab );
            }

        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true; // true：允许创建的菜单显示出来，false：创建的菜单将无法显示。
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_file:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                try {
                    startActivityForResult(Intent.createChooser(intent, "选择文件"), 0);
                } catch (ActivityNotFoundException ex) {
                    showToast("亲，木有文件管理器啊-_-!!");
                }
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 0) {
            return;
        }
        if (requestCode != 0) {
           Toast.makeText(MainActivity.this,"Do not found file",Toast.LENGTH_SHORT).show();
            return;
        }
        Uri uri = data.getData();
        String filename = FileUtil.getFilePathByUri(this, uri);
        Log.e("TAG", "onActivityResult: " + filename );
        inputSheet(filename);
    }


    @Override
    protected void onStart() {
        super.onStart();
        initModule();
        Log.e("TAG", "onStart: " );
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (invenrotyThread!=null) {
            handler.removeCallbacks(invenrotyThread);
        }
        if (mUhfrManager!=null) {
            mUhfrManager.close();
            mUhfrManager = null;
        }
        Log.e("TAG", "onStop: " );
    }




    private SharedUtil sharedUtil ;
    public static UHFRManager mUhfrManager;
    private SharedPreferences mSharedPreferences;
    public boolean isConnectUHF  = false;
    /**
     * 初始化uhf模块
     */
    private void initModule() {
        mUhfrManager = UHFRManager.getInstance();// Init Uhf module
        if(mUhfrManager!=null){
            //5106和6106 /6107和6108 支持33db
            sharedUtil = new SharedUtil(this);
            Reader.READER_ERR err = mUhfrManager.setPower(sharedUtil.getPower(), sharedUtil.getPower());//set uhf module power

            if(err== Reader.READER_ERR.MT_OK_ERR){
                isConnectUHF = true ;
                Reader.READER_ERR err1 = mUhfrManager.setRegion(Reader.Region_Conf.valueOf(sharedUtil.getWorkFreq()));

                Toast.makeText(getApplicationContext(),"FreRegion:"+Reader.Region_Conf.valueOf(sharedUtil.getWorkFreq())+
                        "\n"+"Read Power:"+sharedUtil.getPower()+
                        "\n"+"Write Power:"+sharedUtil.getPower(),Toast.LENGTH_LONG).show();
            }else {
                //5101 30db
                Reader.READER_ERR err1 = mUhfrManager.setPower(30, 30);//set uhf module power
                if(err1== Reader.READER_ERR.MT_OK_ERR) {
                    mUhfrManager.setRegion(Reader.Region_Conf.valueOf(mSharedPreferences.getInt("freRegion", 1)));
                    Toast.makeText(getApplicationContext(), "FreRegion:" + Reader.Region_Conf.valueOf(mSharedPreferences.getInt("freRegion", 1)) +
                            "\n" + "Read Power:" + 30 +
                            "\n" + "Write Power:" + 30, Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(this,"模块初始化失败", Toast.LENGTH_SHORT).show();
                }
            }

        }else {
            Toast.makeText(this,"模块初始化失败", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isTab1 = false;
    private Runnable invenrotyThread = new Runnable() {
        @Override
        public void run() {
            List<Reader.TAGINFO> listTag = null;
            //6C
            if (tab!=1) {
                listTag = mUhfrManager.tagEpcTidInventoryByTimer((short) 50) ;
                Log.e("TAG", "run: Tab1" );
            } else {
                listTag = mUhfrManager.tagEpcOtherInventoryByTimer((short) 50, 0, 4, 4, Tools.HexString2Bytes("000000000"));
                Log.e("TAG", "run: Tab2" );
            }
            //
            if (listTag != null && !listTag.isEmpty()) {
                for (int i=0;i<listTag.size();i++) {
                    byte[] data = listTag.get(i).EpcId;
                    String epc = Tools.Bytes2HexString(listTag.get(i).EpcId,listTag.get(i).Epclen);
                    Log.e("TAG" + tab, "invenrotyThread  " + epc );
                    if (tab==2) {
                        if (epc.contains(filterdata)) {
                            byte[] bytes = mUhfrManager.getTagDataByFilter(0, 4, 1, Tools.HexString2Bytes("000000000"), (short) 50, data, 1, 2, true);
                            Log.e("TAG", "light: " + epc );
                        } else {
                            handler.postDelayed(invenrotyThread, 0) ;
                            return;
                        }
                        mUhfrManager.setCancleInventoryFilter();
                    }
                    Bundle b = new Bundle();
                    Message msg = new Message();
                    msg.what = 1;
                    b.putString("epc",epc);
                    msg.setData(b);
                    handler.sendMessage(msg);
                    UtilSound.play(1,0);
                }
            }
            handler.postDelayed(invenrotyThread, 0) ;
        }
    };

    String filterdata = "";
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_inventory:
                clear();
                if(mUhfrManager == null){
                    showToast("模块连接失败");
                    return ;
                }
                handler.postDelayed(invenrotyThread, 0);
                Log.e("TAG", "onClick: " + "start" );
                break;
            case R.id.button_stop:
            case R.id.button_end:
                mUhfrManager.setCancleInventoryFilter();
                if (invenrotyThread!=null) {
                    handler.removeCallbacks(invenrotyThread);
                }
                break;
            case R.id.button_find:
                filterdata = editText.getText().toString();
                clear();
                if (invenrotyThread!=null) {
                    handler.postDelayed(invenrotyThread,0);
                }
                break;
        }
    }

    //导入表格
    private void inputSheet(String fileName) {
        try {
                InputStream is = new FileInputStream(fileName);
                Workbook book = Workbook.getWorkbook(is);
                Sheet sheet = book.getSheet(0);
                String id = "";
                String epc = "";
                String typeA = "";
                String typeB = "";
                String unit = "";
                int pac;
                for (int j = 1; j < (sheet.getRows() ); ++j) {
                    Cursor cursor = sql.query("user", new String[]{"epc"}, "epc = ?", new String[]{sheet.getCell(4, j).getContents()}, null, null, null);
                    if (cursor.getCount() == 0) {
                        id = sheet.getCell(0,j).getContents();
                        epc = sheet.getCell(4, j).getContents();
                        typeA = sheet.getCell(1, j).getContents();
                        typeB = sheet.getCell(2,j).getContents();
                        unit = sheet.getCell(3, j).getContents();
                        dbHelper1.inserts(id, epc, typeA, typeB,unit);
                    }
                    cursor.close();
                }
                book.close();
            showToast("导入数据成功");
                return;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        }
    }

    //show tips
    private Toast toast;

    private void showToast(String info) {
        if (toast == null) toast = Toast.makeText(MainActivity.this, info, Toast.LENGTH_SHORT);
        else toast.setText(info);
        toast.show();
    }

}