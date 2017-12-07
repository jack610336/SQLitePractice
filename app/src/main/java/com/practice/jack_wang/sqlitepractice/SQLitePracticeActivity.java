package com.practice.jack_wang.sqlitepractice;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class SQLitePracticeActivity extends AppCompatActivity {

    private Button btnAdd,btnQuery,btnDelete;
    private TextView txtIndex,txtParkName,txtName,txtOpenTime,txtIntroduction;
    private ContentValues values = new ContentValues();
    private MyDBHelper helper ;
    private ListView list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sqlite_practice);
        findViews();
        helper = MyDBHelper.getInstance(this);
        btnAdd.setOnClickListener(btnAddlistener);
        btnQuery.setOnClickListener(btnQuerylistener);
        btnDelete.setOnClickListener(btnDeletelistener);

        MyDBHelper helper = new MyDBHelper(this, "taipeiParkInfo.db", null, 1);
        Cursor c = helper.getReadableDatabase().query(
                "parkInfo", null, null, null, null, null, null);
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                R.layout.list_row,
                c,
                new String[] {"_id","ParkName","Name","OpenTime","Introduction"},
                new int[] {R.id.txtIndex, R.id.txtParkName,R.id.txtName,R.id.txtOpenTime,R.id.txtIntroduction},
                0);
        list.setAdapter(adapter);
    }

    private View.OnClickListener btnAddlistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            new TransTask().execute("http://data.taipei/opendata/datalist/datasetMeta/download?id=ea732fb5-4bec-4be7-93f2-8ab91e74a6c6&rid=bf073841-c734-49bf-a97f-3757a6013812");


        }
    };

    private View.OnClickListener btnQuerylistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            finish();
            startActivity(getIntent());
        }
    };

    private View.OnClickListener btnDeletelistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SQLiteDatabase db = helper.getWritableDatabase();
            db.delete("parkInfo", null, null);
            finish();
            startActivity(getIntent());

        }
    };
    private void findViews(){

        btnAdd = (Button)findViewById(R.id.btnAdd);
        btnQuery = (Button)findViewById(R.id.btnQuery);
        btnDelete = (Button)findViewById(R.id.btnDelete);
        txtParkName = (TextView)findViewById(R.id.txtParkName);
        txtName = (TextView)findViewById(R.id.txtName);
        txtOpenTime = (TextView)findViewById(R.id.txtOpenTime);
        txtIndex = (TextView)findViewById(R.id.txtIndex);
        txtIntroduction = (TextView)findViewById(R.id.txtIntroduction);
        list = (ListView) findViewById(R.id.list);

    }

    class TransTask extends AsyncTask<String, Void ,String> {


        @Override
        protected String doInBackground(String... params) {

            StringBuilder sb = new StringBuilder();
            try{
                URL url = new URL(params[0]);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(url.openStream()));
                String line = in.readLine();
                while(line!=null){
                    Log.d("HTTP",line);
                    sb.append(line);
                    line = in.readLine();
                }
            }catch (MalformedURLException e ){
                e.printStackTrace();
            }
            catch (IOException e){
                e.printStackTrace();
            }
            return sb.toString();
        }
        @Override
        protected void onPostExecute(String s){
            super.onPostExecute(s);
//            Log.d("JSON",s);
            parseJSON(s);

        }
        private void parseJSON(String s){

            try {

                JSONArray jsonArray = new JSONArray(s);
                for(int i =0;i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String ParkName = jsonObject.getString("ParkName");
                    String Name = jsonObject.getString("Name");
                    String OpenTime = jsonObject.getString("OpenTime");
                    String Introduction = jsonObject.getString("Introduction");

                    values.put("ParkName",ParkName);
                    values.put("Name",Name);
                    values.put("OpenTime",OpenTime);
                    values.put("Introduction",Introduction);

//                    long id = helper.getWritableDatabase().insert("parkInfo", null, values);
//                    Log.d("ADD", id+"");
                }
                finish();
                startActivity(getIntent());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }
}
