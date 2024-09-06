package ru.minyurovevgeniy.squads;

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
 * Use the {@link AnnouncementsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AnnouncementsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    int currentSquadId=1;
    String currentSquadIdFile="currentSquadIdFile.txt";

    ArrayList<Announcement> announcementInfo;
    AnnouncementAdapter anAdapter;
    AnnouncementHolder holder;

    String rawJSON="";

    public AnnouncementsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AnnouncementsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AnnouncementsFragment newInstance(String param1, String param2) {
        AnnouncementsFragment fragment = new AnnouncementsFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_announcements, container, false);

        announcementInfo = new ArrayList<>();
        ListView list = (ListView) rootView.findViewById(R.id.announcements_list);

        anAdapter = new AnnouncementAdapter();
        list.setAdapter(anAdapter);

        StringBuilder builder = new StringBuilder();

        File directory = AnnouncementsFragment.this.getContext().getFilesDir(); //or getExternalFilesDir(null); for external storage
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

        Button download =rootView.findViewById(R.id.download_announcements);

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                new Thread(getAnnouncements).start();
            }
        });

        new Thread(getAnnouncements).start();

        return rootView;
    }


    public class AnnouncementAdapter extends ArrayAdapter<Announcement>
    {
        public AnnouncementAdapter()
        {
            super(AnnouncementsFragment.this.getContext(),R.layout.announcement_item,announcementInfo);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            int maxPosition = announcementInfo.size()-1;

            if (position>maxPosition)
            {
                position=maxPosition;
            }

            View row=convertView;
            holder=null;
            if (row==null)
            {
                LayoutInflater inflater = getLayoutInflater();
                row=inflater.inflate(R.layout.announcement_item,parent,false);
                holder=new AnnouncementHolder(row);
                row.setTag(holder);
            }
            else
            {
                holder=(AnnouncementHolder)row.getTag();
            }

            holder.PopulateFrom(announcementInfo.get(position));
            return row;
        }

    }

    public class AnnouncementHolder
    {
        private TextView time;
        private TextView text;

        public AnnouncementHolder(View row)
        {
            time=(TextView)row.findViewById(R.id.announcement_time);
            text=(TextView)row.findViewById(R.id.announcement_text);
        }

        void PopulateFrom(Announcement r)
        {
            time.setText(r.time);
            text.setText(r.text);
        }
    }


    public Runnable getAnnouncements = new Runnable()
    {
        @Override
        public void run() {
            byte[] data = null;
            //String params = "test="+testToken;
            try {
                URL url = new URL("https://buildingsquaduspu.xn--100-5cdnry0bhchmgqi5d.xn--p1ai/php/announcements.json");
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
                        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(AnnouncementsFragment.this.getContext().openFileOutput(generalInfoLocal, Context.MODE_PRIVATE));
                        outputStreamWriter.flush();
                        outputStreamWriter.write(rawJSON);
                        outputStreamWriter.close();
                        */
                        JSONObject myJSONObject = new JSONObject(stringBuilder.toString());
                        JSONArray squadsJSON = myJSONObject.getJSONArray("announcements");

                        announcementInfo= new ArrayList<>();
                        for(int i=0;i<squadsJSON.length();i++)
                        {
                            if (Integer.parseInt(squadsJSON.getJSONObject(i).getString("squad"))==currentSquadId) {
                                announcementInfo.add(
                                        new Announcement
                                                (
                                                        squadsJSON.getJSONObject(i).getString("time"),
                                                        squadsJSON.getJSONObject(i).getString("text")
                                                )
                                );
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
                        int length = announcementInfo.size();
                        for (int i = 0; i < length; i++)
                        {
                            anAdapter.add(announcementInfo.get(i));
                        }
                    }
                    catch (Exception e) {
                    }
                }
            });
        }
    };
}