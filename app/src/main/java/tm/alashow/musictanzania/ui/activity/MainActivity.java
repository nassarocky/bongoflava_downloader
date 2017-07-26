/*
 * Copyright 2015. Alashov Berkeli
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package tm.alashow.musictanzania.ui.activity;
import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cocosw.bottomsheet.BottomSheet;
import com.github.jksiezni.permissive.PermissionsGrantedListener;
import com.github.jksiezni.permissive.PermissionsRefusedListener;
import com.github.jksiezni.permissive.Permissive;
import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tumblr.remember.Remember;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import retrofit2.Call;
import retrofit2.Response;
import tm.alashow.musictanzania.Config;
import tm.alashow.musictanzania.R;
import tm.alashow.musictanzania.android.IntentManager;
import tm.alashow.musictanzania.interfaces.OnItemClickListener;
import tm.alashow.musictanzania.interfaces.OnPreparedListener;
import tm.alashow.musictanzania.model.Audio;
import tm.alashow.musictanzania.model.Result;
import tm.alashow.musictanzania.rest.ApiHelper;
import tm.alashow.musictanzania.rest.ApiService;
import tm.alashow.musictanzania.rest.Summon;
import tm.alashow.musictanzania.ui.adapter.AudioListAdapter;
import tm.alashow.musictanzania.ui.view.EndlessRecyclerView;
import tm.alashow.musictanzania.util.AudioWife;
import tm.alashow.musictanzania.util.U;
import tr.xip.errorview.ErrorView;

/**
 * Created by alashov on 21/03/15.
 */
public class MainActivity extends BaseActivity implements EndlessRecyclerView.Pager, OnItemClickListener,SearchView.OnQueryTextListener  {
   String URL_AUDIOS= "https://downloader-c35c6.firebaseio.com/audios";
    private boolean isLoading = false;
    private boolean stopLoadMore = false;
    private int pagination = 0;
    private static final String TAG = MainActivity.class.getSimpleName();
    private boolean showSearchView = true;
    ArrayList<Audio> audios=new ArrayList<>();
    ConnectivityManager cm;
    String newTextt;
  Thread  thread;
    int j=0;
    int i=0;
    private InterstitialAd mInterstitialAd;
     ArrayList<Audio> filteredList = new ArrayList<>();
    ArrayList<Audio> minFilteredList = new ArrayList<>();
    ArrayList<String> filterednotification = new ArrayList<>();
    private Handler mHandler;
   private DatabaseReference mDatabase;
   // private ArrayList<Audio> audioArrayList = new ArrayList<>();
    private AudioListAdapter audioListAdapter;
    private MediaPlayer mMediaPlayer;
    private LayoutInflater layoutInflater;
    private SearchView mSearchView;
    private ProgressBar spinner;
    NetworkInfo activeNetwork;
    private String oldQuery = "";
    private EditText searchBox;
    //Preferences
    private int CONFIG_SORT;
    private int CONFIG_COUNT;
    private int CONFIG_PERFORMER_ONLY;
   private AdView mAdView;
    RecyclerView recyclerView;

    @Bind(R.id.errorView) ErrorView errorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase=FirebaseDatabase.getInstance().getReferenceFromUrl(URL_AUDIOS);



Bundle extras=getIntent().getExtras();
        if (extras != null) {
            if(extras.containsKey("NotificationMessage")){
                filterednotification.clear();
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);

                filterednotification.add(value.toString());

            }}
        }
        init();
       MobileAds.initialize(getApplicationContext(), "ca-app-pub-5942769310685348~6396826510");
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-5942769310685348/7873559711");
        // [END instantiate_interstitial_ad]

        // [START create_interstitial_ad_listener]
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
             //   beginSecondActivity();
            }

            @Override
            public void onAdLoaded() {
                // Ad received, ready to display
                // [START_EXCLUDE]

                // [END_EXCLUDE]
            }

            @Override
            public void onAdFailedToLoad(int i) {
                // See https://goo.gl/sCZj0H for possible error codes.
                Log.w(TAG, "onAdFailedToLoad:" + i);
            }
        });
requestNewInterstitial();
/*
      final Handler h = new Handler();
        h.postDelayed(new Runnable()
        {
            private long time = 0;

            @Override
            public void run()
            {

                if (minFilteredList.size()>0){
                    audioListAdapter.setFilter(minFilteredList);
                    //   audioListAdapter.notifyDataSetChanged();

                }   else {
                    minFilteredList.addAll(filteredList);
                    audioListAdapter.setFilter(minFilteredList);
                    Toast.makeText(MainActivity.this, "ERROW "+filteredList.size(), Toast.LENGTH_LONG).show();
                    //   audioListAdapter.notifyDataSetChanged();
                }

            }
        }, 3000);*/

    }


    @Override
    protected void onResume() {
        super.onResume();
        getConfig();
        if (mAdView != null) {
            mAdView.resume();
        }
        if (!mInterstitialAd.isLoaded()) {
            requestNewInterstitial();
        }

        if (minFilteredList.size()>0){
            audioListAdapter.setFilter(minFilteredList);
         //   audioListAdapter.notifyDataSetChanged();

        }   else {
            minFilteredList.addAll(filteredList);
            audioListAdapter.setFilter(minFilteredList);
         //   audioListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        mSearchView.setOnQueryTextListener(this);
        MenuItemCompat.setOnActionExpandListener(searchItem,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
// Do something when collapsed
                        if(minFilteredList.size()>0){
                        audioListAdapter.setFilter(minFilteredList);
                        }else {
                            minFilteredList.addAll(filteredList);
                            audioListAdapter.setFilter(minFilteredList);
                        }

                        return true; // Return true to collapse action view
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
// Do something when expanded
                        return true; // Return true to expand action view
                    }
                });
        if (mSearchView != null) {
            mSearchView.setQueryHint(getString(R.string.search_hint));


        }


        return true;

    }


    @Override
    public boolean onQueryTextChange(String newText) {
        j=newText.length();
        newTextt=newText;
        minFilteredList.clear();
        minFilteredList  = filter(filteredList, newText);
if (minFilteredList.size()>0){
                        audioListAdapter.setFilter(minFilteredList);
   // audioListAdapter.notifyDataSetChanged();


        }   else {

    minFilteredList.addAll(filteredList);
                 audioListAdapter.setFilter(minFilteredList);
                     //     audioListAdapter.notifyDataSetChanged();
  Toast.makeText(MainActivity.this,newText+"- Not Found", Toast.LENGTH_SHORT).show();



}

        return true;
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private ArrayList<Audio> filter(ArrayList<Audio> models, String query) {
        query = query.toLowerCase();final ArrayList<Audio> filteredModelListt = new ArrayList<>();
        for (Audio model : models) {
            final String text = model.getFullName().toLowerCase();
            if (text.contains(query)) {
                filteredModelListt.add(model);
            }
        }
        return filteredModelListt;
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        getConfig();
        super.onConfigurationChanged(newConfig);
    }

    private void init() {
        mHandler = new Handler();

        initViews();
        getConfig();

        registerGCM();

        initIntent();
    }

    private void initViews() {
        setTitle(R.string.app_name);
        layoutInflater = LayoutInflater.from(this);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        spinner=(ProgressBar)findViewById(R.id.progressBar);
        audioListAdapter = new AudioListAdapter(this, this);
        recyclerView.setAdapter(audioListAdapter);
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequesty = new AdRequest.Builder().build();
        mAdView.loadAd(adRequesty);

        errorView.setOnRetryListener(new ErrorView.RetryListener() {
            @Override
            public void onRetry() {
                U.hideView(errorView);

                search(new OnLoadListener(TYPE_NEW), oldQuery);
            }
        });
    }

    private void initIntent() {


                search(new OnLoadListener(TYPE_NEW), "nassa");
                showSearchView();

    }

    /**
     * Sets searchview iconifed to false and sets oldQuery as searchQuery
     */
    private void showSearchView() {


        if (mSearchView != null && ! oldQuery.isEmpty()) {
            mSearchView.setIconified(false);
            mSearchView.setQuery(oldQuery, false);
            showSearchView = true;
        }
    }


    private void search(final OnLoadListener onLoadListener, String query) {
        oldQuery = query;
        spinner.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
       mDatabase.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

if(dataSnapshot==null){

}else {
    audios.clear();
//  onLoadListener.onStart();
             for( DataSnapshot snapshot : dataSnapshot.getChildren() ) {

                 audios.add(snapshot.getValue(Audio.class));


             }

    onLoadListener.onSuccess(audios);

   }


; // Toast.makeText(MainActivity.this, "GREAT " + "driverID", Toast.LENGTH_SHORT).show();
           //     Log.i(TAG, "User name: " + "audio.getTitle()" + ", email " + "audio.getArtist()");

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.i(TAG, "Failed to read value.", error.toException());
              //  Toast.makeText(MainActivity.this, "ERROW " + error.toException(), Toast.LENGTH_LONG).show();
            }
        });



    }

    @Override
    public void onItemClick(View view, int position) {
        final Audio audio = minFilteredList.get(position);
        final BottomSheet bottomSheet = new BottomSheet.Builder(MainActivity.this)
                .title(audio.getFullName())
                .sheet(R.menu.audio_actions)
                .listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case R.id.download:
                                downloadAudio(audio);
                                break;
                            case R.id.play:
                                playAudio(audio);
                                break;
                            case R.id.copy:
                                U.copyToClipboard(MainActivity.this, audio.getDownloadUrl());
                                break;
                            case R.id.share:
                                String shareText = getString(R.string.share_text) + audio.getDownloadUrl();
                                U.shareTextIntent(MainActivity.this, shareText);
                        }
                    }
                }).show();

        //If file size already set, show it
        if (audio.getBytes() > 1) {
            setSizeAndBitrate(bottomSheet, audio);
        } else {
            ApiService.getClientScalars().getBytes(audio.getEncodedAudioId()).enqueue(new Summon<String>() {
                @Override
                public void onSuccess(Call<String> call, Response<String> response) {
                    if (ApiHelper.isSuccess(response)) {
                        long bytes = Long.parseLong(response.body());
                        audio.setBytes(bytes);
                        setSizeAndBitrate(bottomSheet, audio);
                    }
                }
            });
        }
    }

    /**
     * Set file size and audio bitrate to download menu
     *
     * @param bottomSheet menu where located download button
     * @param audio       file for get info
     */
    private void setSizeAndBitrate(BottomSheet bottomSheet, Audio audio) {
        MenuItem menuItem = bottomSheet.getMenu().findItem(R.id.download);
        //menuItem.setTitle(String.format(Locale.US, "%s (%s, ~%.0f kbps)", menuItem.getTitle(), audio.getFileSize(), audio.getBitrate()));
        bottomSheet.invalidate();
    }

    /**
     * Shows error by given error type
     *
     * @param error errors type or error message
     */


    private void clearData() {
        setPage(0);
        stopLoadMore = false;
        filteredList.clear();
    }


    private void getConfig() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.getBoolean("languageChanged", false)) {
            PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("languageChanged", false).apply();
            IntentManager.with(this).main();
            finish();
        }
    //    CONFIG_COUNT = Integer.parseInt(sharedPreferences.getString("searchCount", Config.DEFAULT_COUNT));
        CONFIG_SORT = Integer.parseInt(sharedPreferences.getString("searchSort", Config.DEFAULT_SORT));
        CONFIG_PERFORMER_ONLY = (sharedPreferences.getBoolean("performerOnly", false)) ? 1 : 0;
    }

    /**
     * @param status 0 loading, 1 loaded, 2 error
     */

    @Override
    public boolean shouldLoad() {
        return ! isLoading && ! stopLoadMore;
    }

    @Override
    public void loadNextPage()

    {
        search(new OnLoadListener(TYPE_PAGINATION), oldQuery);
    }

    private int getPage() {
        return pagination;
    }

    private void setPage(int page) {
        this.pagination = page;
    }



    public class OnLoadListener {

        private int type;

        public OnLoadListener(int type) {
            this.type = type;
        }

        void onSuccess(ArrayList<Audio> newList) {
            //Clear if not paginating
            spinner.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            filteredList.clear();
            filteredList.addAll(newList);
            minFilteredList.clear();
            minFilteredList.addAll(newList);
            //TODO: refactor

            Collections.reverse(filteredList);
Collections.reverse(minFilteredList);

            audioListAdapter.setFilter(minFilteredList);

            if(filterednotification.size()>0){
                                   minFilteredList.clear();
for(String xx:filterednotification){
                final ArrayList<Audio> filteredModelListtt = filter(filteredList, xx);
    if(filteredModelListtt.size()>0) {
        minFilteredList.addAll(filteredModelListtt);
    }
}

                audioListAdapter.setFilter(minFilteredList);
                filterednotification.clear();
            }
            audioListAdapter.notifyDataSetChanged();
        }

        public int getType() {
            return type;
        }
    }

    /**
     * Shows dialog with bitrate list (with calculated file size for each bitrate if bytes available)
     *
     * @param audio audio
     */


    private void downloadAudio(Audio audio) {
if(isConnected()){

        downloadAudio(audio, - 1);
    final Handler handler = new Handler();
    handler.postDelayed(new Runnable() {
        @Override
        public void run() {
            // Do something after 5s = 5000ms
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
        }
    }, 3000);}else{

    Toast toast = Toast.makeText(MainActivity.this, "HAKUNA MTANDAO / NO-INTERNET CONNECTION", Toast.LENGTH_LONG);
    toast.setGravity(Gravity.CENTER, 0, 0);
    toast.show();
}


    }

    /**
     * Downloads safe file name audio to folder
     *
     * @param audio   audio object
     * @param bitrate bitrate, give < 0 if no need for convert
     */
    private void downloadAudio(final Audio audio, final int bitrate) {
        new Permissive.Request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .whenPermissionsGranted(new PermissionsGrantedListener() {
                    @Override
                    public void onPermissionsGranted(String[] permissions) throws SecurityException {
                        String safeFilename = audio.getSafeFileName(bitrate)+".mp3";
                        Uri downloadUri = Uri.parse(audio.getDownloadUrl(bitrate));

                        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                        DownloadManager.Request request = new DownloadManager.Request(downloadUri);

                        if (U.isAboveOfVersion(11)) {
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        }

                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                        request.setDestinationInExternalPublicDir(Config.DOWNLOAD_FOLDER_NAME, safeFilename);

                        downloadManager.enqueue(request);
                    }
                })
                .whenPermissionsRefused(new PermissionsRefusedListener() {
                    @Override
                    public void onPermissionsRefused(String[] permissions) {
                        U.showCenteredToast(MainActivity.this, R.string.error_permissionNotGranted);
                    }
                })
                .execute(this);

    }

    /**
     * Registers GCM and sends to server
     */
    private void registerGCM() {
        try {
            GCMRegistrar.checkDevice(this);
            GCMRegistrar.checkManifest(this);
            final String regId = GCMRegistrar.getRegistrationId(this);
            if (regId.equals("")) {
                GCMRegistrar.register(this, Config.GCM_SENDER_ID);
            } else {
                if (Remember.getBoolean("gcmSent", false)) {
                    ApiService.getClientJackson().register(
                            regId,
                            U.getDeviceId(this)
                    ).enqueue(new Summon<Result>() {
                        @Override
                        public void onSuccess(Call<Result> call, Response<Result> response) {
                            Remember.putBoolean("gcmSent", true);
                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Audio Player stuff

    /**
     * stop playing audio
     */
    private void resetPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void playAudio(final Audio audio) {

        if(isConnected()){
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    }
                }
            }, 20000);
            final LinearLayout rootView = new LinearLayout(this);
            ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(this, R.style.Base_Theme_AppCompat_Light_Dialog);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(contextThemeWrapper);
            alertDialogBuilder.setView(rootView);
            alertDialogBuilder.setNegativeButton(R.string.audio_player_close, null);

            final AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.setCanceledOnTouchOutside(false);

            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    //change flat button color
                    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.primary));
                }
            });
            //destroy mediaPlayer when dialog dismissed
            alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    resetPlayer();
                }
            });

            new PrepareAudioTask(rootView, new OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer, AudioWife audioWife) {
                    mMediaPlayer = mediaPlayer;
                    alertDialog.show();
                    TextView nameView = (TextView) rootView.findViewById(R.id.name);
                    if (nameView != null) {
                        nameView.setText(audio.getArtist() + " - " + audio.getTitle());
                    }
                    audioWife.play();
                }

                @Override
                public void onError(Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            U.showCenteredToast(MainActivity.this, R.string.exception);
                        }
                    });
                }
            }).execute(Uri.parse(audio.getStreamUrl()));}else{
            Toast toast = Toast.makeText(MainActivity.this, "HAKUNA MTANDAO / NO-INTERNET CONNECTION", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();

        }

    }

    /**
     * Shows progress dialog while preparing mediaPlayer
     */
    public class PrepareAudioTask extends AsyncTask<Uri, Void, Void> {
        private AudioWife audioWife;
        private ViewGroup rootView;
        private OnPreparedListener onPreparedListener;
        private ProgressDialog progressDialog;
        private boolean cancelled = false;

        public PrepareAudioTask(ViewGroup rootView, OnPreparedListener onPreparedListener) {
            this.rootView = rootView;
            this.onPreparedListener = onPreparedListener;
            progressDialog = U.createActionLoading(MainActivity.this);
            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.audio_player_close), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    cancelled = true;
                    progressDialog.dismiss();
                    if (audioWife != null) {
                        audioWife.release();
                    }
                }
            });
        }

        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Uri... params) {
            try {
                audioWife = AudioWife.getInstance()
                        .init(MainActivity.this, params[0], new OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mediaPlayer, AudioWife audioWife) {
                                if (! cancelled) {
                                    progressDialog.dismiss();
                                    onPreparedListener.onPrepared(mediaPlayer, audioWife);
                                }
                            }

                            @Override
                            public void onError(Exception e) {
                                if (! cancelled) {
                                    onPreparedListener.onError(e);
                                    progressDialog.dismiss();
                                }
                            }
                        }).useDefaultUi(rootView, layoutInflater);
            } catch (Exception e) {
                e.printStackTrace();
                progressDialog.dismiss();
                onPreparedListener.onError(e);
            }
            return null;
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    @Override
    protected Boolean isChildActivity() {
        return false;
    }

    @Override
    protected String getActivityTag() {
        return Config.ACTIVITY_TAG_MAIN;
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
        audioListAdapter.setFilter(filteredList);
    }

    /** Called when returning to the activity */

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }
    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .build();

        mInterstitialAd.loadAd(adRequest);

    }

    public boolean isConnected() {
        boolean connected;

        cm = (ConnectivityManager) getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetwork = cm.getActiveNetworkInfo();
        connected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        return connected;
    }
    }