package gttrade.guantang.com.tradeerp.TE12;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import gttrade.guantang.com.tradeerp.R;
import gttrade.guantang.com.tradeerp.sortlistview.CharacterParser;
import gttrade.guantang.com.tradeerp.sortlistview.PinyinComparator;
import gttrade.guantang.com.tradeerp.sortlistview.SideBar;
import gttrade.guantang.com.tradeerp.sortlistview.SortModel;

import static android.R.attr.country;

public class TE1201Activity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    @BindView(R.id.back)
    ImageButton back;
    @BindView(R.id.editText)
    EditText editText;
    @BindView(R.id.searchDelBtn)
    ImageView searchDelBtn;
    @BindView(R.id.hplist2)
    ListView hplist2;
    @BindView(R.id.dialog)
    TextView dialog;
    @BindView(R.id.sidrbar)
    SideBar sidrbar;
    private List<String> countryList;

    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    private List<SortModel> SourceDateList;

    private CountryAdapter countryAdapter;

    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;

    private int where = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_te1201);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        countryList = (List<String>) intent.getSerializableExtra("countrys");

        hplist2.setOnItemClickListener(this);
        countryAdapter = new CountryAdapter(this);
        hplist2.setAdapter(countryAdapter);
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        new initAsyncTask().execute();
    }


    public void init() {
        // 实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s.toString())){
                    searchDelBtn.setVisibility(View.GONE);
                }else{
                    searchDelBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                new searchAsyncTask().execute(s.toString());

            }
        });

        sidrbar.setTextView(dialog);
        // 设置右侧触摸监听
        sidrbar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                // 该字母首次出现的位置
                int position = countryAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    hplist2.setSelection(position);
                }

            }
        });
        hplist2.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO 自动生成的方法存根
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    where = hplist2.getFirstVisiblePosition();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                // TODO 自动生成的方法存根

            }
        });
    }

    /**
     * 为ListView填充数据,并为每个元素配上字母
     *
     * @param
     * @return
     */
    public List<SortModel> filledData() {
        List<SortModel> mSortList = new ArrayList<SortModel>();
        int length = countryList.size();
        for (int i = 0; i < length; i++) {
            SortModel sortModel = new SortModel();
            sortModel.setCountry(countryList.get(i));
            // 汉字转换成拼音
            String pinyin = characterParser.getSelling(countryList.get(i));
            String sortString = "";
            if (!TextUtils.isEmpty(pinyin)) {
                sortString = pinyin.substring(0, 1).toUpperCase();
            } else {
                sortModel.setSortLetters("#");
            }
            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
            } else {
                sortModel.setSortLetters("#");
            }
            mSortList.add(sortModel);
        }

        return mSortList;
    }


    @OnClick({ R.id.searchDelBtn,R.id.back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.searchDelBtn:
                editText.setText("");
                break;
            case R.id.back:
                Intent intent = getIntent();
                intent.putExtra("country","");
                setResult(1,intent);
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SortModel sortModel = (SortModel) parent.getAdapter().getItem(position);
        Intent intent = getIntent();
        intent.putExtra("country",sortModel.getCountry());
        setResult(1,intent);
        finish();
    }


    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     */
    class searchAsyncTask extends AsyncTask<String,Void,List<SortModel>>{

        @Override
        protected List<SortModel> doInBackground(String... params) {
            List<SortModel> filterDateList = new ArrayList<SortModel>();
//            SourceDateList = filledData();
            if (TextUtils.isEmpty(params[0])) {
                filterDateList = SourceDateList;
            } else {
                for (SortModel sortModel : SourceDateList) {
                    String country = sortModel.getCountry();
                    if (country.indexOf(params[0].toString()) != -1
                            || characterParser.getSelling(country).startsWith(
                            params[0].toString())) {
                        filterDateList.add(sortModel);
                    }
                }
//                SourceDateList.clear();
//                SourceDateList = filterDateList;
            }
            // 根据a-z进行排序
            Collections.sort(filterDateList, pinyinComparator);
            return filterDateList;
        }

        @Override
        protected void onPostExecute(List<SortModel> data) {
            super.onPostExecute(data);

            countryAdapter.setData(data);
            hplist2.setSelection(where);
        }
    }

    class initAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            SourceDateList = filledData();
            // 根据a-z进行排序
            Collections.sort(SourceDateList, pinyinComparator);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            countryAdapter.setData(SourceDateList);
            hplist2.setSelection(where);

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            Intent intent = getIntent();
            intent.putExtra("country","");
            setResult(1,intent);
            finish();
        }
        return true;
    }
}
