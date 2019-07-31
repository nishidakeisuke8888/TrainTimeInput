package com.example.keisuke.traintimeinput;

//import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
//import android.support.v4.app.DialogFragment;
//import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends FragmentActivity implements
        TimePickerDialog.OnTimeSetListener{

    private TextView mTextView;
    private TextView TextView2;

    TextView tv;

    int num=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.ride);
        TextView2 = (TextView) findViewById(R.id.walkstarttime);


        // Enables Always-on
        //setAmbientEnabled();

        findViewById(R.id.timeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num=1;
                showTimePickerDialog(v);
            }
        });

        findViewById(R.id.timeButton2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num=2;
                showTimePickerDialog(v);
            }
        });

        findViewById(R.id.time2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rideTime = ((TextView) findViewById(R.id.ride)).getText().toString();
                walkTime = ((TextView) findViewById(R.id.walkstarttime)).getText().toString();
                TemporarilySavedLog();  //端末内一時保存
                saveAllLog();           //ログデータの出力

                Toast.makeText(getApplicationContext(),"入力を保存しました",Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        String str;
        if(num == 1){
            tv = mTextView;
        } else if(num == 2){
            tv = TextView2;
        }

        if(minute < 10){
            str= String.format(Locale.US, "%d:0"+"%d", hourOfDay, minute);
        } else {
            str= String.format(Locale.US, "%d:%d", hourOfDay, minute);
        }

        tv.setText(str);

        /*
        String str = String.format(Locale.US, "%d:%d", hourOfDay, minute);
        mTextView.setText( str );
        */
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePick();
        newFragment.show(getSupportFragmentManager(), "timePicker");

    }



    ///////////////////////////////////////ログの保存

    static DtoItem memorize;
    static List<DtoItem> listItemAll = new ArrayList<DtoItem>();
    static List<DtoItem> listItemNow = new ArrayList<DtoItem>();
    //public static Calendar calendar = Calendar.getInstance();
    static int id = 0;
    public static String rideTime;
    public static String walkTime;


    //ログの一時保存（入力テキストに変更があった場合に処理）
    public static void TemporarilySavedLog(){
        id++;
        StringBuilder historyBuilder = new StringBuilder(8224);
        historyBuilder.setLength(0);
        memorize =new DtoItem();
        memorize.WearId= id;
        memorize.ridetime = rideTime;
        memorize.walktime= walkTime;

        /*
        memorize.startatation = startStation;
        memorize.arrivestation = arrivalStation;
        memorize.ReservationStarttime = startTime;
        memorize.ReservationArrivetime = arrivalTime;
        */
        listItemNow.add(memorize);
        listItemAll.addAll(listItemNow);
        listItemNow.clear();
    }

    //ログデータを一括で出力
    public static void saveAllLog(){
        String filePath = /*Environment.getExternalStorageDirectory() + */"data/data/com.example.keisuke.watchcountdowntablemap/files/Logdata.dat";
        File file = new File(filePath);
        file.getParentFile().mkdir();
        FileOutputStream fos;

        StringBuilder databaseToFile = new StringBuilder(131072);

        for (DtoItem memorize : listItemAll) {
            databaseToFile.append(memorize.WearId);
            databaseToFile.append(",");
            databaseToFile.append(memorize.ridetime);
            databaseToFile.append(",");
            databaseToFile.append(memorize.walktime);
            databaseToFile.append(",");
            databaseToFile.append("\n");
        }

        try {
            // OpenWnnLog.datファイルへの追加書き込み準備
            fos = new FileOutputStream(file, true);
            OutputStreamWriter osw = new OutputStreamWriter(fos, "Shift_JIS");
            BufferedWriter writer = new BufferedWriter(osw);

            writer.write(databaseToFile.toString());

            writer.flush();
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        listItemAll.clear();
    }
}
