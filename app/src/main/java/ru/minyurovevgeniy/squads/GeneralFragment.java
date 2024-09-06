package ru.minyurovevgeniy.squads;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GeneralFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GeneralFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ArrayList<String> squadGeneralInfo;
    SquadsAdapter anAdapter;
    PhotoHolder holder;

    String generalInfoLocal="generalInfoLocal.txt";

    String filename="squads.txt";
    int currentSquadId=1;
    String currentSquadIdFile="currentSquadIdFile.txt";

    String rawJSON="";
    String generalInfoText="";

    public GeneralFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GeneralFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GeneralFragment newInstance(String param1, String param2) {
        GeneralFragment fragment = new GeneralFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_general, container, false);

        squadGeneralInfo = new ArrayList<>();
        ListView list = (ListView) rootView.findViewById(R.id.general_list);

        anAdapter = new SquadsAdapter();
        list.setAdapter(anAdapter);

        StringBuilder builder = new StringBuilder();

        File directory = GeneralFragment.this.getContext().getFilesDir(); //or getExternalFilesDir(null); for external storage
        File yourFile = new File(directory, currentSquadIdFile);

        try(FileInputStream fin=new FileInputStream(yourFile))
        {
            int i;
            while((i=fin.read())!=-1){

                builder.append((char)i);
            }
        }
        catch(IOException ex){

            System.out.println(ex.getMessage());
        }

        currentSquadId = Integer.parseInt(builder.toString());

        Button download =rootView.findViewById(R.id.download_general_info);

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

            }
        });

        new Thread(getSquadGeneralInfo).start();

        return rootView;
    }

    public Runnable getSquadGeneralInfo = new Runnable()
    {
        @Override
        public void run() {
            byte[] data = null;
            //String params = "test="+testToken;
            try {
                URL url = new URL("https://buildingsquaduspu.xn--100-5cdnry0bhchmgqi5d.xn--p1ai/php/generalInfo.json");
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

                        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(GeneralFragment.this.getContext().openFileOutput(generalInfoLocal, Context.MODE_PRIVATE));
                        outputStreamWriter.flush();
                        outputStreamWriter.write(rawJSON);
                        outputStreamWriter.close();
                        /*
                        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(MainActivity.this.openFileOutput(filename, Context.MODE_PRIVATE));
                        outputStreamWriter.write(rawJSON);
                        outputStreamWriter.close();
                        */
                        JSONObject myJSONObject = new JSONObject(stringBuilder.toString());
                        JSONArray squadsJSON = myJSONObject.getJSONArray("infos");

                         //int currentId=1;
                        //squads= new ArrayList<>();
                        for(int i=0;i<squadsJSON.length();i++)
                        {
                            if (Integer.parseInt(squadsJSON.getJSONObject(i).getString("squad"))==currentSquadId)
                            {
                                generalInfoText=squadsJSON.getJSONObject(i).getString("info");
                            }
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

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try
                    {
                        //TextView generalInfo = getActivity().findViewById(R.id.general_info);
                        //generalInfo.setText(generalInfoText);
                        anAdapter.clear();
                        anAdapter.add(generalInfoText);
                    }
                    catch (Exception e) {
                    }
                }
            });
        }
    };


    public class SquadsAdapter extends ArrayAdapter<String>
    {
        public SquadsAdapter()
        {
            super(GeneralFragment.this.getContext(),R.layout.general_info_item,squadGeneralInfo);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            int maxPosition = squadGeneralInfo.size()-1;

            if (position>maxPosition)
            {
                position=maxPosition;
            }

            View row=convertView;
            holder=null;
            if (row==null)
            {
                LayoutInflater inflater = getLayoutInflater();
                row=inflater.inflate(R.layout.general_info_item,parent,false);
                holder=new PhotoHolder(row);
                row.setTag(holder);
            }
            else
            {
                holder=(PhotoHolder)row.getTag();
            }

            holder.PopulateFrom(squadGeneralInfo.get(position));
            return row;
        }

    }

    public class PhotoHolder
    {
        private TextView name;

        public PhotoHolder(View row)
        {
            name=(TextView)row.findViewById(R.id.general_list_item);
        }

        void PopulateFrom(String r)
        {
            name.setText(r);
        }
    }
}