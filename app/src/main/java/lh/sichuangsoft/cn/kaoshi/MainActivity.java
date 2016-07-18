package lh.sichuangsoft.cn.kaoshi;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public List<Question> questionList;  //查询结果ORM列表
    public MyOpenHelper oh;              //数据库助手
    public SQLiteDatabase db;           //数据库
    public boolean isList = false;        //开关，判断当前是显示查询结果的列表，还是查询界面
    public int selectId = 0;              //判断之前选择是什么题型
//    public DBManager dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //dbHelper = new DBManager(this);
        // dbHelper.openDatabase();
        // dbHelper.closeDatabase();
        initSQL();  //检测到表中数据为空，就将试题数据导入
        initSearch();  //初始化搜索,绑定事件
    }

    /**
     * 检测到表中数据为空，就将试题数据导入
     *
     * @param
     * @return
     * @throws
     * @author liheng
     * @time 2016-7-17
     **/
    protected Void initSQL() {
        String Result = ""; //SQL语句
        try {
            SQLiteDatabase db = new MyOpenHelper(this).getWritableDatabase();

            //查询单选题题量
            int catCount = 0;
            Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM panduan", null);
            if (cursor.moveToFirst()) {
                catCount = cursor.getInt(0);
            }
            cursor.close();
            if (catCount >0) return null;  //如果表中已有数据，就退出

            //否则就导入数据
            try {
                //从RAW文件夹中读dudx.sql文件,写入到Result变量
                InputStreamReader inputReader = new InputStreamReader(getResources().openRawResource(R.raw.dudx));
                BufferedReader bufReader = new BufferedReader(inputReader);
                String line = "";
                Result = "";
                while ((line = bufReader.readLine()) != null) Result += line;
                bufReader.close();
                inputReader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            //解析SQL，并逐句执行
            String[] s = Result.split(";");
            for (int i = 0; i < s.length; i++) {
                if (!TextUtils.isEmpty(s[i])) db.execSQL(s[i]);
            }
            db.close();
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }


    /**
     * 初始化搜索界面，绑定事件
     *
     * @param
     * @return
     * @throws
     * @author liheng
     * @time 2016-7-17
     **/
    protected void initSearch() {
        questionList = new ArrayList<Question>();
        oh = new MyOpenHelper(this);
        db = oh.getWritableDatabase();

        //重置单选按钮组，选择到上次选择的按钮
        if (selectId != 0) {
            RadioButton rd = (RadioButton) findViewById(selectId);
            rd.setChecked(true);
        }

        //绑定按钮事件
        Button btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {

            //关闭输入法
            public void closeBoard(Context mcontext) {
                InputMethodManager imm = (InputMethodManager) mcontext
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                // imm.hideSoftInputFromWindow(myEditText.getWindowToken(), 0);
                if (imm.isActive())  //一直是true
                    imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
                            InputMethodManager.HIDE_NOT_ALWAYS);
            }

            @Override
            public void onClick(View v) {

                closeBoard(MainActivity.this);//关闭输入法

                //取得搜索关键字
                EditText et = (EditText) findViewById(R.id.editText);
                String skey = et.getText().toString();

                //Toast tst = Toast.makeText(MainActivity.this, skey, Toast.LENGTH_SHORT);
                //tst.show();

                //取得单选按钮TAG，用以决定用哪个表，TAG与表名一一对应的
                RadioGroup rg = (RadioGroup) findViewById(R.id.radioGroup);
                selectId = rg.getCheckedRadioButtonId();
                RadioButton rdbt = (RadioButton) findViewById(selectId);
                String table = rdbt.getTag().toString();

                //构建搜索条件，
                String[] columns = new String[]{"_id", "q", "a"};
                String selection = "a like ?";
                String[] selectionArgs = new String[]{"%" + skey + "%"};
                String orderBy = "_id";

                //搜索并导入LIST  questionList
                Cursor cursor = db.query(table, columns, selection, selectionArgs, null, null, orderBy);
                while (cursor.moveToNext()) {
                    String _id = cursor.getString(cursor.getColumnIndex("_id"));
                    String q = cursor.getString(cursor.getColumnIndex("q"));
                    String a = cursor.getString(cursor.getColumnIndex("a"));
                    Question p = new Question(_id, q, a);
                    questionList.add(p);
                }
                cursor.close();
                db.close();
                //切换显示布局
                setContentView(R.layout.resoult);
                isList = true;

                //把数据显示到屏幕
                LinearLayout ll = (LinearLayout) findViewById(R.id.ll);
                for (Question p : questionList) {
                    //1.集合中每有一条数据，就new一个TextView
                    TextView tv = new TextView(MainActivity.this);
                    tv.setText("★" + p.toString());
                    tv.setPadding(3, 20, 3, 0);
                    tv.setBackgroundColor(Color.WHITE);
                    // tv.setTextSize(14);
                    ll.addView(tv);
                }

            }
        });

    }

    /*
    *按返回键时，显示搜索界面，并重初始化
    *
    *
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            if (isList) {
                setContentView(R.layout.activity_main);
                isList = false;
                initSearch();
                return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }


}
