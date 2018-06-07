package com.example.paranoidandroid.conapp;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static  MainActivity inst;
    ListView listView;
    ArrayList<String> smsListView = new ArrayList<String>();
    ArrayAdapter arrayAdapter;

    public static MainActivity instance(){return inst;}

    @Override
    protected void onStart() {
        super.onStart();
        inst = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.smsList);
        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,smsListView);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    String[] smsMessages = smsListView.get(position).split("\n");
                    String address = smsMessages[0];
                    String smsMessage = "";
                    for (int i = 1;i<smsMessages.length;++i){
                        smsMessage += smsMessages[i];
                    }
                    String smsMessageStr = address + "\n";
                    smsMessageStr += smsMessage;
                    Toast.makeText(MainActivity.this,smsMessageStr, Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        if(ContextCompat.checkSelfPermission(getBaseContext(),"android.permission.READ_SMS") == PackageManager.PERMISSION_GRANTED){
            refreshSmsInbox();
        }else{
            final int REQUEST_CODE_ASK_PERMISSION = 123;
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{"android.permission.READ_SMS"},REQUEST_CODE_ASK_PERMISSION);
        }

    }

    private void refreshSmsInbox() {
        ContentResolver contentResolver = getContentResolver();
        Cursor smsInboxCurser = contentResolver.query(Uri.parse("content://sms/inbox"),null,null,null);
        int indexBody = smsInboxCurser.getColumnIndex("Body");
        int indexAddress = smsInboxCurser.getColumnIndex("address");
        if(indexBody < 0 || !smsInboxCurser.moveToFirst())return;
        arrayAdapter.clear();
        do{
            String str = "SMS From:" + smsInboxCurser.getString(indexAddress)+"\n"+smsInboxCurser.getString(indexBody)+"\n";
            arrayAdapter.add(str);
        }while (smsInboxCurser.moveToNext());


    }
    public void updateList(final String smsMessage){
        arrayAdapter.insert(smsMessage,0);
        arrayAdapter.notifyDataSetChanged();
    }
}
