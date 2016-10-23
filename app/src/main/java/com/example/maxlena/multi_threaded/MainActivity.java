package com.example.maxlena.multi_threaded;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import android.view.View.OnClickListener;
import static android.R.id.message;

public class MainActivity extends AppCompatActivity {

    Context context = this;
    private String fileName = "number.txt";
    private ArrayAdapter<String> adapter;
    private List<String> list;
    private ListView listView;
    private ProgressBar progressBar;
    private Handler handler = new Handler();
    private int status = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.ListView);
        adapter =  new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        progressBar = (ProgressBar) findViewById(R.id.Bar);
        listView.setAdapter(adapter);

    }


    //synchronized allows the variable to
    //be shared among all of the threads
    private synchronized void setProgressBar(int progress) {
        this.status = Math.min(100, progress);
    }

    //synchronized allows the variable to
    //be shared among all of the threads
    private synchronized int getProgressBar() {
        return this.status;
    }
    public void clickedCreate(View v) {
        progressBar.setVisibility(View.VISIBLE);
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                setProgressBar(0);
                String message = "";
                FileOutputStream outputStream;
                File file = new File(context.getFilesDir(), fileName);
                for ( int i = 1; i <= 10; i++) {
                    message += i + "\n";

                    //increment the progress bar
                    setProgressBar(getProgressBar() + 10);

                    //communicate with the main thread
                    handler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(getProgressBar());
                        }
                    });

                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {

                    outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                    outputStream.write(message.getBytes());
                    outputStream.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

       thread.start();
    }

    public void clickedLoad(View v) {
        progressBar.setVisibility(View.VISIBLE);
        list = new ArrayList<String>();
        final File file = new File(context.getFilesDir(), fileName);
        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    setProgressBar(0);
                    Scanner scanner = new Scanner(file);

                    while (scanner.hasNextLine()) {
                        final String num = scanner.nextLine();

                        list.add(num);

                        //increase progress bar
                        setProgressBar(getProgressBar() + 10);

                        //display the new progress bar
                        handler.post(new Runnable() {
                            public void run() {
                                progressBar.setProgress(getProgressBar());
                            }
                        });
                        Thread.sleep(250);
                    }

                    scanner.close();

                    handler.post(new Runnable() {
                        public void run() {
                            adapter.clear();
                            adapter.addAll(list);
                        }
                    });

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        thread.start();
    }

    public void clickedClear(View v){
        setProgressBar(0);
        progressBar.setVisibility(View.INVISIBLE);

        adapter.clear();
        list.clear();
    }

}