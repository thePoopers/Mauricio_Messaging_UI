package materialtest.theartistandtheengineer.co.materialtest.app;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.PushService;
import com.parse.SaveCallback;

import materialtest.theartistandtheengineer.co.materialtest.MainActivity;

public class AppController extends Application {

	public static final String TAG = AppController.class.getSimpleName();
	private RequestQueue mRequestQueue;
	private static AppController mInstance;

	public static final String API_KEY_GOOGLE_BOOKS = "AIzaSyBlZGnFuy7ro_RieQn8zRNr2QSM5CjHatI";
	private static AppController sInstance;

    //Messaging constants
    public static final String APP_ID = "aCFOhRA9gssJXdc1OTVMWXoZU2HhMIvf8Pu14WrJ";
    public static final String CLIENT_KEY = "gVIWFxhRe1iOAipsDAfYAijjXSpyrwAWL3QN1NsG";

	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;

        //intitialize messaging
        Parse.initialize(this, APP_ID, CLIENT_KEY);
        ParseInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                PushService.setDefaultPushCallback(AppController.this, MainActivity.class);
            }
        });
	}

	public static synchronized AppController getInstance() {
		return mInstance;
	}

	public static Context getAppContext(){
		return mInstance.getApplicationContext();
	}

	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		}

		return mRequestQueue;
	}

	public <T> void addToRequestQueue(Request<T> req, String tag) {
		req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
		getRequestQueue().add(req);
	}

	public <T> void addToRequestQueue(Request<T> req) {
		req.setTag(TAG);
		getRequestQueue().add(req);
	}

	public void cancelPendingRequests(Object tag) {
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(tag);
		}
	}
}