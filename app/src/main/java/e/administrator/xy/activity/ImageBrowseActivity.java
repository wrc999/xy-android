package e.administrator.xy.activity;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import e.administrator.xy.R;
import e.administrator.xy.util.ImageBrowseAdapter;

public class ImageBrowseActivity extends AppCompatActivity {

    private ViewPager imageBrowseViewPager;
    private TextView imageBrowse_position;
    private int index ;
    private List<String> imageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_browse);
        initView();
        initDate();
    }

    private void initView() {
        imageBrowseViewPager = findViewById(R.id.imageBrowseViewPager);
        imageBrowse_position = findViewById(R.id.imageBrowse_position);
    }

    private void initDate() {
        if(getIntent().getExtras() != null){
            index = getIntent().getIntExtra("index", 0);
            imageList = getIntent().getStringArrayListExtra("imageList");
            ImageBrowseAdapter adapter = new ImageBrowseAdapter(ImageBrowseActivity.this,imageList);
            imageBrowseViewPager.setAdapter(adapter);
            imageBrowse_position.setText(index+1+"/"+imageList.size());
            imageBrowseViewPager.setCurrentItem(index);
            imageBrowseViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    imageBrowse_position.setText(position+1+"/"+imageList.size());
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
        }
    }

}
