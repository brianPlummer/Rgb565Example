package rgb565.codemonkeylabs.com.rgb565;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {


    public static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView mainImgDefault = (ImageView) findViewById(R.id.mainImgDefault);
        ImageView mainImgMod = (ImageView) findViewById(R.id.mainImgMod);

        String url = "http://static.stg.nytimes.com/images/2015/06/23/arts/obama-photo/obama-photo-jumbo.jpg";

        fetchImageWithAsyncTask(url, mainImgDefault, Bitmap.Config.ARGB_8888);
        fetchImageWithAsyncTask(url, mainImgMod, Bitmap.Config.RGB_565);

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
