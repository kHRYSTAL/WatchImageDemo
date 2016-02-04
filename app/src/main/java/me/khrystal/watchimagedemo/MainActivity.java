package me.khrystal.watchimagedemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
    }

    private void initData() {
        images = new ArrayList<>();
        images.add("http://img1.gtimg.com/sports/pics/hv1/102/210/2012/130883952.jpg");
        images.add("http://img1.gtimg.com/sports/pics/hv1/126/212/2012/130884486.jpg");
        images.add("http://mat1.gtimg.com/sports/soccerdata/images//player/19054.jpg");
    }


    public void watch(View view) {
        Intent intent = new Intent(MainActivity.this,ImageActivity.class);
        intent.putStringArrayListExtra(ImageActivity.IMAGE_URLS,images);
//TODO 添加索引位置
        intent.putExtra(ImageActivity.IMAGE_INDEX,2);
        startActivity(intent);
    }
}
