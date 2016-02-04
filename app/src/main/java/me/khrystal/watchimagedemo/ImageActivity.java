package me.khrystal.watchimagedemo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import photoview.BigPicViewPage;
import photoview.ImageViewTouch;
import photoview.ImageViewTouchBase;
import photoview.utils.DensityUtil;


/**
 * Created by Yao on 2015/12/15.
 */
public class ImageActivity extends AppCompatActivity{
    private BigPicViewPage mViewPager;
    private List<String> imageUrls;
    private int mPosition;
    private Context mContext;
    private LinearLayout ll_photo_index;
    public static String IMAGE_URLS = "image_urls";
    public static String IMAGE_INDEX = "image_index";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_image);
        loadData();
        findView();
        setListener();
        processLogic();
    }

    protected void loadData() {
        imageUrls = getIntent().getStringArrayListExtra(IMAGE_URLS);
        if (imageUrls==null||imageUrls.size()==0)
            finish();
        mPosition = getIntent().getIntExtra(IMAGE_INDEX,0); //设置选中的图片位置

    }

    protected void findView() {

        mViewPager = (BigPicViewPage) findViewById(R.id.mViewPager);
        ll_photo_index = (LinearLayout) findViewById(R.id.ll_photo_index);
        int margins = DensityUtil.dip2px(this, 5);
        for (int i = 0; i < imageUrls.size(); i++) {
            ImageView pointView = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(margins, margins, margins, margins);
            pointView.setLayoutParams(params);
            pointView.setBackgroundResource(R.drawable.icon_product_detail_image_unselected);
            ll_photo_index.addView(pointView);
        }
        changePointStatus(mPosition);
    }

    protected void setListener() {
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                changePointStatus(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    public void changePointStatus(int position) {
        for (int i = 0; i < ll_photo_index.getChildCount(); i++) {
            ImageView pointView = (ImageView) ll_photo_index.getChildAt(i);
            if (i == position) {
                pointView
                        .setBackgroundResource(R.drawable.icon_product_detail_image_selected);
            } else {
                pointView
                        .setBackgroundResource(R.drawable.icon_product_detail_image_unselected);
            }
        }
    }

    protected void processLogic() {
        mViewPager.setAdapter(new SamplePagerAdapter());
        mViewPager.setCurrentItem(mPosition);
    }

    class SamplePagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return imageUrls.size();
        }

        ImageView loading;

        @Override
        public View instantiateItem(ViewGroup container, final int position) {
            View view = View.inflate(mContext, R.layout.adapter_viewpager_pic,
                    null);
            final ImageViewTouch photoView = (ImageViewTouch) view.findViewById(R.id.photoview);
            loading = (ImageView)(view.findViewById(R.id.src_loading));
            Glide.with(ImageActivity.this).load(R.drawable.src_loading).into(loading);

            photoView.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
            photoView.setDoubleTapEnabled(true);
            //ImageUtils.loadImage(photoView, imageUrls.get(position), R.color._000000);
            Glide.with(ImageActivity.this).load(imageUrls.get(position)==null?R.drawable.ic_photo_loading:imageUrls.get(position)).listener(new RequestListener<Serializable, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, Serializable model, Target<GlideDrawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, Serializable model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    loading.setVisibility(View.GONE);
                    photoView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ImageActivity.this);
                            builder.setItems(new String[]{"保存图片"}, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    photoView.setDrawingCacheEnabled(true);
                                    Bitmap imageBitmap = photoView.getDrawingCache();
                                    if (imageBitmap != null) {
                                        new SaveImageTask(photoView).execute(imageBitmap);
                                    }
                                }
                            });
                            builder.show();
                            return false;
                        }
                    });
                    return false;
                }
            }).into(photoView);
            photoView.setSingleTapListener(new ImageViewTouch.OnImageViewTouchSingleTapListener() {
                @Override
                public void onSingleTapConfirmed() {
                    finish();
                }
            });
            /*photoView.setOnTouchListener(new View.OnTouchListener() {

                private long downTime;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            downTime = System.currentTimeMillis();
                            break;
                        case MotionEvent.ACTION_UP:
                            if ((System.currentTimeMillis()-downTime)<100) {
                                finish();
                                overridePendingTransition(0, 0);
                            }
                            break;

                        default:
                            break;
                    }
                    return false;
                }
            });*/
            container.addView(view, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 如果按下的是返回键，并且没有重复
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();

            return true;
        }
        return false;
    }

    private class SaveImageTask extends AsyncTask<Bitmap, Void, String> {
        private ImageViewTouch image;
        public SaveImageTask(ImageViewTouch image){
            this.image = image;
        }

        @Override
        protected String doInBackground(Bitmap... params) {

        String result = "图片保存失败";

        try {
            String sdcard = Environment.getExternalStorageDirectory().toString();

            File file = new File(sdcard + "/Download");
            if (!file.exists()) {
                file.mkdirs();
            }

            File imageFile = new File(file.getAbsolutePath(),new Date().getTime()+".jpg");
            FileOutputStream outStream = null;
            outStream = new FileOutputStream(imageFile);
            Bitmap image = params[0];
            image.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
            result = getResources().getString(R.string.save_picture_success,  file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
            image.setDrawingCacheEnabled(false);
        }
    }
}
