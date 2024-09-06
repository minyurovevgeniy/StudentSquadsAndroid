package ru.minyurovevgeniy.squads;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.io.FileOutputStream;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {

    public ArrayList<Squad> squads;
    SquadsAdapter anAdapter;
    PhotoHolder holder;

    String rawJSON="";

    String filename="squads.txt";
    int id=0;
    String currentSquadIdFile="currentSquadIdFile.txt";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        squads = new ArrayList<>();

        ListView list = (ListView) findViewById(R.id.squads_list);

        anAdapter = new SquadsAdapter();
        list.setAdapter(anAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this,SquadInfo.class);
                //intent.putExtra("id",squads.get(i).id);


                File directory = getFilesDir(); //or getExternalFilesDir(null); for external storage
                File yourFile = new File(directory, currentSquadIdFile);


                    try(FileOutputStream fos=new FileOutputStream(yourFile))
                    {
                        fos.flush();
                        // перевод строки в байты
                        byte[] buffer = Integer.toString(squads.get(i).id).getBytes();

                        fos.write(buffer, 0, buffer.length);
                        //System.out.println("The file has been written");
                        Log.d("","The file has been written");
                    }
                    catch(IOException ex){

                        Log.d("",ex.getMessage());
                    }

                /*
                try
                {
                BufferedWriter writer = new BufferedWriter(new FileWriter(currentSquadIdFile));
                writer.write("");
                writer.flush();

                writer.write(Integer.toString(squads.get(i).id));
                }
                catch (Exception e) {
                    e.printStackTrace(); // Handle the error here
                }
                */



                /*
                try
                {

                    File myObj = new File(currentSquadIdFile);

                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(MainActivity.this.openFileOutput(currentSquadIdFile, Context.MODE_PRIVATE));
                    outputStreamWriter.flush();
                    outputStreamWriter.write(squads.get(i).id);
                    outputStreamWriter.close();
                }
                catch (Exception e) {
                    e.printStackTrace(); // Handle the error here
                }

                 */


                intent.putExtra("name",squads.get(i).name);
                startActivity(intent);
            }
        });


        Button downloadSquads = findViewById(R.id.load_squads);

        downloadSquads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                squads.clear();

                new Thread(getSquadsList).start();
            }
        });

        new Thread(getSquadsList).start();
    }

    public class SquadsAdapter extends ArrayAdapter<Squad>
    {
        public SquadsAdapter()
        {
            super(MainActivity.this,R.layout.squad_item,squads);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            int maxPosition = squads.size()-1;

            if (position>maxPosition)
            {
                position=maxPosition;
            }

            View row=convertView;
            holder=null;
            if (row==null)
            {
                LayoutInflater inflater = getLayoutInflater();
                row=inflater.inflate(R.layout.squad_item,parent,false);
                holder=new PhotoHolder(row);
                row.setTag(holder);
            }
            else
            {
                holder=(PhotoHolder)row.getTag();
            }

            holder.PopulateFrom(squads.get(position));
            return row;
        }

    }

    public class PhotoHolder
    {
        private TextView name;

        public PhotoHolder(View row)
        {
            name=(TextView)row.findViewById(R.id.squad_name);
        }

        void PopulateFrom(Squad r)
        {
            name.setText(r.name);
        }
    }

    public Runnable getSquadsList = new Runnable()
    {
        @Override
        public void run() {
            byte[] data = null;
            //String params = "test="+testToken;
            try {
                URL url = new URL("https://buildingsquaduspu.xn--100-5cdnry0bhchmgqi5d.xn--p1ai/php/getSquads.php");
                try {

                    URLConnection connection = url.openConnection();
                    HttpURLConnection httpConnection = (HttpURLConnection) connection;
                    httpConnection.setConnectTimeout(10000);
                    httpConnection.setReadTimeout(10000);
                    httpConnection.setRequestMethod("GET");
                    httpConnection.setAllowUserInteraction(false);
                    // Open communications link (network traffic occurs here).
                    //httpConnection.connect();
                    // give it 15 seconds to respond

                    connection.setDoOutput(true);
                    connection.setDoInput(true);

                    //OutputStream os = connection.getOutputStream();
                    //data = params.getBytes("UTF-8");
                    //os.write(data);

                    //if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK)
                    //{
                    try
                    {
                        // read the output from the server

                        BufferedReader reader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
                        StringBuffer stringBuilder = new StringBuffer();

                        String line = null;
                        while ((line = reader.readLine()) != null)
                        {
                            stringBuilder.append(line);
                        }

                        rawJSON = stringBuilder.toString();
                        /*
                        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(MainActivity.this.openFileOutput(filename, Context.MODE_PRIVATE));
                        outputStreamWriter.write(rawJSON);
                        outputStreamWriter.close();
                        */
                        JSONObject myJSONObject = new JSONObject(stringBuilder.toString());
                        JSONArray squadsJSON = myJSONObject.getJSONArray("squads");


                        squads= new ArrayList<>();
                        for(int i=0;i<squadsJSON.length();i++)
                        {
                            squads.add(
                                    new Squad
                                            (
                                                    Integer.parseInt(squadsJSON.getJSONObject(i).getString("squad_id")),
                                                    squadsJSON.getJSONObject(i).getString("squad_name")
                                            )
                            );
                        }

                    }
                    catch (Exception e) {
                    }
                } catch (IOException r)
                {
                    r.getMessage();
                }
            } catch (MalformedURLException e)
            {
                e.getMessage();
            }
            finally {}

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try
                    {
                        anAdapter.clear();
                        int length = squads.size();
                        for (int i = 0; i < length; i++)
                        {
                            anAdapter.add(squads.get(i));
                        }
                    }
                    catch (Exception e) {
                    }
                }
            });
        }
    };
}