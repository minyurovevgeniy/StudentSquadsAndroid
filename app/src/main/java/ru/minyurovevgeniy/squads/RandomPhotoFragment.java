package ru.minyurovevgeniy.squads;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RandomPhotoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RandomPhotoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    String currentSquadIdFile="currentSquadIdFile.txt";
    int currentSquadId=1;

    RandomPhoto randomPhoto;

    public RandomPhotoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RandomPhotoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RandomPhotoFragment newInstance(String param1, String param2) {
        RandomPhotoFragment fragment = new RandomPhotoFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_random_photo, container, false);

        StringBuilder builder = new StringBuilder();

        File directory = RandomPhotoFragment.this.getContext().getFilesDir(); //or getExternalFilesDir(null); for external storage
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

        Button download =rootView.findViewById(R.id.download_random_photo);

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                new Thread(getRandomPhoto).start();
            }
        });

        new Thread(getRandomPhoto).start();

        return rootView;
    }


    public Runnable getRandomPhoto = new Runnable()
    {
        @Override
        public void run() {
            byte[] data = null;
            String params = "id="+currentSquadId;
            try {
                URL url = new URL("https://buildingsquaduspu.xn--100-5cdnry0bhchmgqi5d.xn--p1ai/php/getRandomPhoto.php");
                try {

                    URLConnection connection = url.openConnection();
                    HttpURLConnection httpConnection = (HttpURLConnection) connection;
                    httpConnection.setConnectTimeout(10000);
                    httpConnection.setReadTimeout(10000);
                    httpConnection.setRequestMethod("POST");
                    httpConnection.setAllowUserInteraction(false);
                    // Open communications link (network traffic occurs here).
  //                  httpConnection.connect();
                    // give it 15 seconds to respond

                    connection.setDoOutput(true);
                    connection.setDoInput(true);

                    OutputStream os = connection.getOutputStream();
                    data = params.getBytes("UTF-8");
                    os.write(data);

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

                        /*
                        rawJSON = stringBuilder.toString();

                        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(GeneralFragment.this.getContext().openFileOutput(generalInfoLocal, Context.MODE_PRIVATE));
                        outputStreamWriter.flush();
                        outputStreamWriter.write(rawJSON);
                        outputStreamWriter.close();

                        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(MainActivity.this.openFileOutput(filename, Context.MODE_PRIVATE));
                        outputStreamWriter.write(rawJSON);
                        outputStreamWriter.close();
                        */
                        JSONObject myJSONObject = new JSONObject(stringBuilder.toString());
                        //JSONArray squadsJSON = myJSONObject.getJSONArray("infos");


                        randomPhoto=new RandomPhoto(myJSONObject.getString("description"),getBitmap(myJSONObject.getString("link")));
                        //int currentId=1;
                        //squads= new ArrayList<>();
                        /*
                        for(int i=0;i<squadsJSON.length();i++)
                        {
                            if (Integer.parseInt(squadsJSON.getJSONObject(i).getString("squad"))==currentSquadId)
                            {
                                generalInfoText=squadsJSON.getJSONObject(i).getString("info");
                            }
                        }
                         */

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
                        TextView description = getActivity().findViewById(R.id.photo_description);
                        description.setText(randomPhoto.description);

                        ImageView photoImage = getActivity().findViewById(R.id.photo_content);
                        photoImage.setImageBitmap(randomPhoto.photo);
                    }
                    catch (Exception e) {
                    }
                }
            });
        }
    };


    public Bitmap getBitmap(String url) {
        try
        {
            InputStream is = (InputStream) new URL(url).getContent();
            Bitmap d = BitmapFactory.decodeStream(is);
            is.close();
            return d;
        } catch (Exception e)
        {
            e.getStackTrace();
            return null;
        }
    }
}