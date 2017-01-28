package com.example.xyzreader.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;

/**
 * Created by iamsparsh on 28/1/17.
 */

public class ArticleDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private ViewPager viewPager;
    private long mStartId,mSelectedItemId;
    private MyPagerAdapter pagerAdapter;
    private Cursor cursorData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        try{
            init();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void init() {

        findViewById();
        initialize();
        getIntentData();
        initializeLoader();
    }

    private void initialize() {

        pagerAdapter = new MyPagerAdapter(getFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

                if (cursorData != null) {
                    cursorData.moveToPosition(position);
                }
                mSelectedItemId = cursorData.getLong(ArticleLoader.Query._ID);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initializeLoader() {

        getLoaderManager().initLoader(0,null,this);
    }

    private void getIntentData() {

        if(getIntent() != null && getIntent().getData() != null){
            if (getIntent() != null && getIntent().getData() != null) {
                mStartId = ItemsContract.Items.getItemId(getIntent().getData());
                mSelectedItemId = mStartId;
            }
        }
    }

    private void findViewById() {

        viewPager= (ViewPager) findViewById(R.id.pager);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return ArticleLoader.newAllArticlesInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        cursorData = data;
        pagerAdapter.notifyDataSetChanged();

        // Select the start ID
        if (mStartId > 0) {
            cursorData.moveToFirst();
            // TODO: optimize
            while (!cursorData.isAfterLast()) {
                if (cursorData.getLong(ArticleLoader.Query._ID) == mStartId) {
                    final int position = cursorData.getPosition();
                    viewPager.setCurrentItem(position, false);
                    break;
                }
                cursorData.moveToNext();
            }
            mStartId = 0;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        cursorData = null;
        pagerAdapter.notifyDataSetChanged();
    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
        }

        @Override
        public Fragment getItem(int position) {
            cursorData.moveToPosition(position);
            return ArticleDetailFragment.newInstance(cursorData.getLong(ArticleLoader.Query._ID));
        }

        @Override
        public int getCount() {
            return (cursorData != null) ? cursorData.getCount() : 0;
        }
    }
}
