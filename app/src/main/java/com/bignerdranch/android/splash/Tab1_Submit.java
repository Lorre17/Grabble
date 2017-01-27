package com.bignerdranch.android.splash;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.bignerdranch.android.splash.Maps_Activity.alphabet;
import static com.bignerdranch.android.splash.Maps_Activity.alphabet_scores;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Lorena on 23/01/2017.
 */

@SuppressLint("ValidFragment")
public class Tab1_Submit extends Fragment {
    public List<String> letters = Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z");
    static ArrayList<String> dictionary = new ArrayList<>();
    private Context mContext;

    Button submit_button;
    EditText submit_field;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab1_submit, container, false);

        GridView gridview = (GridView) rootView.findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(this.getActivity()));
        mContext = getApplicationContext();

        submit_button = (Button) rootView.findViewById(R.id.submit_button);
        submit_field = (EditText) rootView.findViewById(R.id.textSubmit);

        // The InputStream opens the resourceId and sends it to the buffer
        InputStream is = this.getResources().openRawResource(R.raw.grabble);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String readLine = null;

        try {
            // While the BufferedReader readLine is not null
            while ((readLine = br.readLine()) != null) {
                dictionary.add(readLine);
            }

            // Close the InputStream and BufferedReader
            is.close();
            br.close();

        } catch (IOException e) {
                e.printStackTrace();
        }

        submit_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("deb", "Inside submitWord");

                if(submit_field.getText().toString().length()!=0)
                {
                    Log.d("deb", "Inside if statement");
                    String word = submit_field.getText().toString();
                    Integer invalid = 0;
                    for (int i = 0; i < word.length(); i++) {
                        int count = 0;
                        for (int j = 0; j < word.length(); j++)
                        {
                             if( word.charAt(i) == word.charAt(j)){
                                 count++;
                             }
                        }


                        Log.d("TESTOG", String.valueOf(i));
                        Log.d("Testing", String.valueOf(Character.toUpperCase(word.charAt(i))));
                        Log.d("TESTOG", String.valueOf(alphabet.get(String.valueOf(Character.toUpperCase(word.charAt(i))))));

                        if(count <= alphabet.get(String.valueOf(Character.toUpperCase(word.charAt(i))))){
                            invalid = 0;
                        }else {
                            invalid = 1;
                        }
                    }

                    if(invalid == 0 && dictionary.contains(word)) {
                        Log.d("deb", "Word found");
                        Toast.makeText(mContext, R.string.word_in_dictionary, Toast.LENGTH_SHORT).show();
                        Maps_Activity.score = 0;

                        for (char c : word.toCharArray()) {
                            alphabet.put(String.valueOf(Character.toUpperCase(c)), alphabet.get(String.valueOf(Character.toUpperCase(c))) - 1);
                            Maps_Activity.score += alphabet_scores.get(String.valueOf(Character.toUpperCase(c)));
                        }
                        Maps_Activity.totalScore += Maps_Activity.score;
                    } else if (invalid == 1 && dictionary.contains(word)) {
                            Toast.makeText(mContext, R.string.not_enough_letters, Toast.LENGTH_SHORT).show();
                    } else if (invalid == 0 && !dictionary.contains(word)) {
                        Toast.makeText(mContext, R.string.word_not_in_dictionary, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, R.string.no_letters_no_dict, Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(mContext,"Please enter a word!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }

//    public void submitWord () {
//        Log.d("deb", "Inside submitWord");
//        if(submit_field.getText().toString().length()!=0)
//        {
//            Log.d("deb", "Inside if statement");
//            String word = submit_field.getText().toString();
//            if( dictionary.contains(word)){
//                Log.d("deb", "Word found");
//                Toast.makeText(mContext, R.string.word_in_dictionary, Toast.LENGTH_SHORT).show();
//            }else {
//                Log.d("deb", "Word not found");
//                Toast.makeText(mContext, R.string.word_not_in_dictionary, Toast.LENGTH_SHORT).show();
//            }
//        }else {
//            Toast.makeText(mContext,"NOT WORKING ", Toast.LENGTH_SHORT).show();
//        }
//    }

    public class ImageAdapter extends BaseAdapter {
        private Context mContext;

        public ImageAdapter(Context c) {
            mContext = c;

        }

        public int getCount() {
            return 26;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
//            ImageView imageView;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item,parent,false);


                TextView alpTV = (TextView) convertView.findViewById(R.id.letter);
                alpTV.setText(letters.get(position));

                TextView count = (TextView)convertView.findViewById(R.id.letterCount);

                count.setText(""+ alphabet.get(letters.get(position)));
                LinearLayout bgLayout = (LinearLayout) convertView.findViewById(R.id.letterBackground);
                if(alphabet.get(letters.get(position))==0) {
                    bgLayout.setBackgroundColor(getResources().getColor(R.color.grid_gray));
                }else{
                    bgLayout.setBackgroundColor(getResources().getColor(R.color.grid_blue));
                }

                // if it's not recycled, initialize some attributes
//                imageView = new ImageView(mContext);
//                imageView.setLayoutParams(new GridView.LayoutParams(105, 105));
//                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                imageView.setPadding(2, 2, 2, 2);
            } else {
//                imageView = (ImageView) convertView;
            }

//            imageView.setImageResource(mThumbIds[position]);
//            return imageView;
            return convertView;
        }

        // references to our images
//        private Integer[] mThumbIds = {
//                R.drawable.rounded_square_1, R.drawable.rounded_square_1, R.drawable.rounded_square_1, R.drawable.rounded_square_1, R.drawable.rounded_square_1,
//                R.drawable.rounded_square_1, R.drawable.rounded_square_1, R.drawable.rounded_square_1, R.drawable.rounded_square_1, R.drawable.rounded_square_1,
//                R.drawable.rounded_square_1, R.drawable.rounded_square_1, R.drawable.rounded_square_1, R.drawable.rounded_square_1, R.drawable.rounded_square_1,
//                R.drawable.rounded_square_1, R.drawable.rounded_square_1, R.drawable.rounded_square_1, R.drawable.rounded_square_1, R.drawable.rounded_square_1,
//                R.drawable.rounded_square_1, R.drawable.rounded_square_1, R.drawable.rounded_square_1, R.drawable.rounded_square_1, R.drawable.rounded_square_1,
//                R.drawable.rounded_square_1,

//        };
    }
}
