package asm.uabierta.activities;

import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;

import com.squareup.picasso.Picasso;

import asm.uabierta.R;
import asm.uabierta.utils.Constants;
import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouch.OnImageViewTouchDoubleTapListener;
import it.sephiroth.android.library.imagezoom.ImageViewTouch.OnImageViewTouchSingleTapListener;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase.DisplayType;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase.OnDrawableChangeListener;

public class ImageViewActivity extends AppCompatActivity {

    private static final String LOG_TAG = "IMAGEVIEW";
    ImageViewTouch mImage;
    String imageSrc;
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.image_view);
        //overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        title = getIntent().getStringExtra(Constants.title);
        setTitle(title);

        if(getActionBar()!=null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();

        imageSrc = getIntent().getStringExtra(Constants.image);
        mImage = (ImageViewTouch) findViewById(R.id.image);
        mImage.setDisplayType(DisplayType.FIT_TO_SCREEN);

        Picasso.with(getApplicationContext())
                .load(imageSrc)
                .into(mImage);

        mImage.setSingleTapListener(new OnImageViewTouchSingleTapListener() {
            @Override
            public void onSingleTapConfirmed() {
            }
        }
        );

        mImage.setDoubleTapListener(new OnImageViewTouchDoubleTapListener() {
                                        @Override
                                        public void onDoubleTap() {
                                            //Log.d(LOG_TAG, "onDoubleTap");
                                        }
                                    }
        );

        mImage.setOnDrawableChangedListener(new OnDrawableChangeListener() {
                                                @Override
                                                public void onDrawableChanged(Drawable drawable) {
                                                    //Log.i(LOG_TAG, "onBitmapChanged: " + drawable);
                                                }
                                            }
        );
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_image_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.adjust:
                mImage.setDisplayType(DisplayType.NONE);
                mImage.setDisplayType(DisplayType.FIT_TO_SCREEN);
                break;

            case android.R.id.home:
                finish();
                break;

            default:
                break;
        }
        return true;
    }
}