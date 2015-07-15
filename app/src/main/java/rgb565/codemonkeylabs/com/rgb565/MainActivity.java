package rgb565.codemonkeylabs.com.rgb565;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import rgb565.codemonkeylabs.com.rgb565.picasso.BitmapTransform;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView mainImgDefault = (ImageView) findViewById(R.id.mainImgDefault);
        ImageView mainImgMod = (ImageView) findViewById(R.id.mainImgMod);

        String url = "https://steveconway.files.wordpress.com/2013/01/hpim2085.jpg";

        //fetchImageWithAsyncTask(url, mainImgDefault, Bitmap.Config.ARGB_8888);
        //fetchImageWithAsyncTask(url, mainImgMod, Bitmap.Config.RGB_565);

        fetchImageWithPicasso(url, mainImgDefault, Bitmap.Config.ARGB_8888);
        fetchImageWithPicasso(url, mainImgMod, Bitmap.Config.RGB_565);

    }


    private void fetchImageWithPicasso(String urlString,
                                       ImageView target,
                                       Bitmap.Config config) {


        int targetWidth = getScreenSize(MainActivity.this).x;

        double aspectRatio = 888d / 1187d;
        int targetHeight = (int) (targetWidth * aspectRatio);

        Picasso.with(MainActivity.this)
                .load(urlString)
                .transform(new BitmapTransform(targetWidth, targetHeight, urlString, config))
                .resize(targetWidth, targetHeight)
                .centerInside()
                .into(target);
    }


    private void fetchImageWithAsyncTask(final String urlString,
                                         final ImageView target,
                                         final Bitmap.Config config) {

        new AsyncTask<Object,Void,Bitmap>(){
            @Override
            protected Bitmap doInBackground(Object... objects) {
                URL url = null;
                try {
                    url = new URL(urlString);
                } catch (MalformedURLException e) {
                    Log.e(TAG, e.getMessage(), e);
                    return null;
                }

                try {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = config;
                    return BitmapFactory.decodeStream(url.openConnection().getInputStream(),null, options);
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage(), e);
                    return null;
                }
            }
            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                if(bitmap !=null)
                    target.setImageBitmap(bitmap);
            }
        }.execute();

    }

    public static Point getScreenSize(Context context) {
        WindowManager windowManager =
                (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point result = new Point();
        display.getSize(result);
        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
