package com.practice.jack_wang.sqlitepractice;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class SQLitePracticeActivity extends AppCompatActivity {

    private Button btnAdd,btnDeleteSingleRow,btnDelete,btnAddSingle;
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
        btnDeleteSingleRow.setOnClickListener(btnDeleteSingleRowlistener);
        btnDelete.setOnClickListener(btnDeletelistener);
        btnAddSingle.setOnClickListener(btnAddSinglelistener);

        MyDBHelper helper = new MyDBHelper(this, "taipeiParkInfo.db", null, 1);
        Cursor c = helper.getReadableDatabase().query(
                "parkInfo", null, null, null, null, null, null);
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                R.layout.list_row, c,
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

    private View.OnClickListener btnDeleteSingleRowlistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            final View item = LayoutInflater.from(SQLitePracticeActivity.this).inflate(R.layout.alertdialog, null);
            new AlertDialog.Builder(SQLitePracticeActivity.this)
                    .setTitle(R.string.question)
                    .setView(item)
                    .setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            SQLiteDatabase db = helper.getWritableDatabase();
                            EditText edDeleteRowNum = (EditText) item.findViewById(R.id.edDeleteRow);

                            if(edDeleteRowNum.getText().toString().matches("")){
                                Toast toast = Toast.makeText(SQLitePracticeActivity.this, "Can't be empty", Toast.LENGTH_LONG);
                                toast.show();
                            }
                            else{
                                String index = edDeleteRowNum.getText().toString();
                                db.delete("parkInfo","_id"+"="+index ,null) ;
                                finish();
                                startActivity(getIntent());
                                Toast toast = Toast.makeText(SQLitePracticeActivity.this, "delete data success", Toast.LENGTH_LONG);
                                toast.show();
                            }
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();

        }
    };
    private View.OnClickListener btnAddSinglelistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            final View item = LayoutInflater.from(SQLitePracticeActivity.this).inflate(R.layout.alertdialogadd, null);
            new AlertDialog.Builder(SQLitePracticeActivity.this)
                    .setTitle(R.string.question2)
                    .setView(item)
                    .setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            SQLiteDatabase db = helper.getWritableDatabase();
                            EditText edParkName = (EditText) item.findViewById(R.id.edParkName);
                            EditText edName = (EditText) item.findViewById(R.id.edName);
                            EditText edOpenTime = (EditText) item.findViewById(R.id.edOpenTime);
                            EditText edIntro = (EditText) item.findViewById(R.id.edIntro);

                            if(edParkName.getText().toString().matches("") || edName.getText().toString().matches("")
                                    || edOpenTime.getText().toString().matches("") || edIntro.getText().toString().matches("")){
                                Toast toast = Toast.makeText(SQLitePracticeActivity.this, "Can't be empty", Toast.LENGTH_LONG);
                                toast.show();
                            }
                            else{

                                values.put("ParkName",edParkName.getText().toString());
                                values.put("Name",edName.getText().toString());
                                values.put("OpenTime",edOpenTime.getText().toString());
                                values.put("Introduction",edIntro.getText().toString());
                                helper.getWritableDatabase().insert("parkInfo", null, values);

                                finish();
                                startActivity(getIntent());
                                Toast toast = Toast.makeText(SQLitePracticeActivity.this, "add data success", Toast.LENGTH_LONG);
                                toast.show();
                            }
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
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
        btnDeleteSingleRow = (Button)findViewById(R.id.btnDeleteSingle);
        btnDelete = (Button)findViewById(R.id.btnDelete);
        btnAddSingle = (Button)findViewById(R.id.btnAddsingle);
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

                    helper.getWritableDatabase().insert("parkInfo", null, values);

//                    long id = helper.getWritableDatabase().insert("parkInfo", null, values);
//                    Log.e("ADD", id+"!");
                }
                finish();
                startActivity(getIntent());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }
}
