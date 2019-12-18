package com.example.dictionarydemo.View;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dictionarydemo.Adapter.AdapterSearch;
import com.example.dictionarydemo.Interface.IOnClickWord;
import com.example.dictionarydemo.Model.Dictionary;
import com.example.dictionarydemo.R;
import com.example.dictionarydemo.SQLHelper;
import com.example.dictionarydemo.databinding.ActivityMainBinding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    List<Dictionary> dictionary;
    SQLHelper sqlHelper;
    AdapterSearch adapter;
    int size;
    boolean changeTranslator= true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding= DataBindingUtil.setContentView(this, R.layout.activity_main);

        /*dictionary = new ArrayList<>();
        sqlHelper= new SQLHelper(getBaseContext());
        dictionary = sqlHelper.getAllDictionary();*/
        readData2();
        size= dictionary.size();

        binding.etSearch.setInputType(InputType.TYPE_NULL);
        editText();
        randomWords();
        setChangeTranslator();
        binding.btCancel.setVisibility(View.INVISIBLE);
        binding.etSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.etSearch.setInputType(InputType.TYPE_CLASS_TEXT);
                binding.etSearch.requestFocus();
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.showSoftInput(binding.etSearch, InputMethodManager.SHOW_FORCED);
                buildRcv();
                hideViewHome();
                binding.rcvSearch.setVisibility(View.INVISIBLE);
                binding.btCancel.setVisibility(View.VISIBLE);
            }
        });
        binding.btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showViewHome();
                binding.btCancel.setVisibility(View.INVISIBLE);
                binding.rcvSearch.setVisibility(View.INVISIBLE);
                binding.etSearch.setText("");

               // binding.etSearch.setInputType(InputType.TYPE_NULL);

            }
        });

        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().equals(""))
                    binding.rcvSearch.setVisibility(View.INVISIBLE);
                else {
                    hideViewHome();
                    binding.rcvSearch.setVisibility(View.VISIBLE);
                    filter(s.toString());
                }
            }
        });
    }
///////////////////////////////////// gợi ý từ ............ ----->xong
    private void filter(String text){
        List<Dictionary> filterList= new ArrayList<>();
        if(changeTranslator==true) {
            for (Dictionary item : dictionary) {
                if (item.getEnglish().toLowerCase().indexOf(text.toLowerCase()) == 0) {
                    filterList.add(item);
                }
            }
        }else {
            for (Dictionary item : dictionary) {
                if (item.getVietnamese().toLowerCase().indexOf(text.toLowerCase()) == 0) {
                    filterList.add(item);
                }
            }

        }
        if(filterList.size()==0)
            filterList.add(new Dictionary("trống", "trống"));
        adapter.filterList(filterList);
    }

    //////////////////////////////////////////
    private void editText(){
        binding.etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    binding.etSearch.onEditorAction(EditorInfo.IME_ACTION_DONE);
                    Toast.makeText(getBaseContext(),binding.etSearch.getText().toString(),Toast.LENGTH_LONG).show();
                    return true;
                }
                return false;
            }
        });
    }
    private void randomWords(){
        final Random rd= new Random();
        final int[] index = {rd.nextInt(size)};
        binding.tvEnglish.setText(dictionary.get(index[0]).getEnglish());
        binding.tvVietnamese.setText(dictionary.get(index[0]).getVietnamese());
        binding.btRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index[0] = rd.nextInt(size);
                binding.tvEnglish.setText(dictionary.get(index[0]).getEnglish());
                binding.tvVietnamese.setText(dictionary.get(index[0]).getVietnamese());
            }
        });
    }
    void hideViewHome(){
        binding.layoutRandomWord.setVisibility(View.INVISIBLE);
        binding.layoutHistory.setVisibility(View.INVISIBLE);
    }
    void showViewHome(){
        binding.layoutRandomWord.setVisibility(View.VISIBLE);
        binding.layoutHistory.setVisibility(View.VISIBLE);
    }
    void buildRcv(){
        binding.rcvSearch.setVisibility(View.VISIBLE);
        adapter= new AdapterSearch(dictionary, getBaseContext(), changeTranslator);
        RecyclerView.LayoutManager layoutManager= new LinearLayoutManager(getBaseContext(), RecyclerView.VERTICAL, false);
        binding.rcvSearch.setHasFixedSize(true);
        binding.rcvSearch.setLayoutManager(layoutManager);
        binding.rcvSearch.setAdapter(adapter);
        adapter.setOnClickWord(new IOnClickWord() {
            @Override
            public void onClick(Dictionary in4Word) {
                if (changeTranslator==true) {
                    Toast.makeText(getBaseContext(), in4Word.getVietnamese(), Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(getBaseContext(), in4Word.getEnglish(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    void setChangeTranslator(){
        binding.changeTranslator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    changeTranslator= false;
         binding.TranslatorToEngLish.setBackgroundResource(R.drawable.bo_tron_xam);
         binding.changeTranslator.setBackgroundResource(R.drawable.background_random_words);
            }
        });
        binding.TranslatorToEngLish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTranslator = true;
                binding.changeTranslator.setBackgroundResource(R.drawable.bo_tron_xam);
                binding.TranslatorToEngLish.setBackgroundResource(R.drawable.background_random_words);
            }
        });
    }

    public void readData2()
    {
        dictionary= new ArrayList<>();
        String data;
        InputStream in= getResources().openRawResource(R.raw.file);
        InputStreamReader inreader=new InputStreamReader(in);
        BufferedReader bufreader=new BufferedReader(inreader);
        int indexToCut;
        if(in!=null)
        {
            try
            {
                while((data=bufreader.readLine())!=null)
                {
                    indexToCut= data.indexOf(":");
                    if(indexToCut!=-1){
                        String vietnamese= data.substring(0, indexToCut);
                        String english= data.substring(indexToCut+2);
                        //stringList.add(data);
                        dictionary.add(new Dictionary(vietnamese, english));
                    }
                }
                in.close();
                //Toast.makeText(getBaseContext(), String.valueOf(dictionary.size()), Toast.LENGTH_LONG).show();
                //tv.setText(stringList.get(50000));
            }
            catch(IOException ex){
                Log.e("ERROR", ex.getMessage());
            }
        }

    }
}
