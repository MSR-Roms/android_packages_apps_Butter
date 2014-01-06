/**
 * 
 */

package com.spacecaker.butter.activities;

import android.content.ComponentName;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.BaseColumns;
import android.provider.MediaStore.Audio;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Window;

import com.spacecaker.butter.BottomActionBarControlsFragment;
import com.spacecaker.butter.BottomActionBarFragment;
import com.spacecaker.butter.IApolloService;
import com.spacecaker.butter.R;
import com.spacecaker.butter.adapters.PagerAdapter;
import com.spacecaker.butter.adapters.ScrollingTabsAdapter;
import com.spacecaker.butter.grid.fragments.AlbumsFragment;
import com.spacecaker.butter.grid.fragments.ArtistsFragment;
import com.spacecaker.butter.list.fragments.GenresFragment;
import com.spacecaker.butter.list.fragments.PlaylistsFragment;
import com.spacecaker.butter.list.fragments.RecentlyAddedFragment;
import com.spacecaker.butter.list.fragments.TracksFragment;
import com.spacecaker.butter.service.ApolloService;
import com.spacecaker.butter.service.ServiceToken;
import com.spacecaker.butter.ui.widgets.ScrollableTabView;
import com.spacecaker.butter.utils.ApolloUtils;
import com.spacecaker.butter.utils.MusicUtils;
import com.spacecaker.butter.utils.ThemeUtils;

import static com.spacecaker.butter.Constants.MIME_TYPE;
import static com.spacecaker.butter.Constants.PLAYLIST_RECENTLY_ADDED;
import static com.spacecaker.butter.Constants.THEME_ITEM_BACKGROUND;

/**
 * @author Andrew Neal
 * @Note This is the "holder" for all of the tabs
 */
public class MusicLibrary extends FragmentActivity implements ServiceConnection {

    private ServiceToken mToken;

    @Override
    protected void onCreate(Bundle icicle) {
        // Landscape mode on phone isn't ready
        if (!ApolloUtils.isTablet(this))
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Scan for music
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        // Control Media volume
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        // Layout
        setContentView(R.layout.library_browser);

        // Hide the ActionBar
        getActionBar().hide();

        // Important!
        initPager();

        // Update the BottomActionBar
        initBottomActionBar();
        super.onCreate(icicle);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder obj) {
        MusicUtils.mService = IApolloService.Stub.asInterface(obj);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        MusicUtils.mService = null;
    }

    @Override
    protected void onStart() {
        // Bind to Service
        mToken = MusicUtils.bindToService(this, this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(ApolloService.META_CHANGED);
        super.onStart();
    }

    @Override
    protected void onStop() {
        // Unbind
        if (MusicUtils.mService != null)
            MusicUtils.unbindFromService(mToken);
        super.onStop();
    }

    /**
     * Initiate ViewPager and PagerAdapter
     */
    public void initPager() {
        // Initiate PagerAdapter
        PagerAdapter mPagerAdapter = new PagerAdapter(getSupportFragmentManager());

        Bundle bundle = new Bundle();
        bundle.putString(MIME_TYPE, Audio.Playlists.CONTENT_TYPE);
        bundle.putLong(BaseColumns._ID, PLAYLIST_RECENTLY_ADDED);
        // Recently added tracks
        mPagerAdapter.addFragment(new RecentlyAddedFragment(bundle));
        // Artists
        mPagerAdapter.addFragment(new ArtistsFragment());
        // Albums
        mPagerAdapter.addFragment(new AlbumsFragment());
        // // Tracks
        mPagerAdapter.addFragment(new TracksFragment());
        // // Playlists
        mPagerAdapter.addFragment(new PlaylistsFragment());
        // // Genres
        mPagerAdapter.addFragment(new GenresFragment());

        // Initiate ViewPager
        ViewPager mViewPager = (ViewPager)findViewById(R.id.viewPager);
        mViewPager.setPageMargin(getResources().getInteger(R.integer.viewpager_margin_width));
        mViewPager.setPageMarginDrawable(R.drawable.viewpager_margin);
        mViewPager.setOffscreenPageLimit(mPagerAdapter.getCount());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(1);

        // Tabs
        initScrollableTabs(mViewPager);

        // Theme chooser
        ThemeUtils.initThemeChooser(this, mViewPager, "viewpager", THEME_ITEM_BACKGROUND);
        ThemeUtils.setMarginDrawable(this, mViewPager, "viewpager_margin");
    }

    /**
     * Initiate the tabs
     */
    public void initScrollableTabs(ViewPager mViewPager) {
        ScrollableTabView mScrollingTabs = (ScrollableTabView)findViewById(R.id.scrollingTabs);
        ScrollingTabsAdapter mScrollingTabsAdapter = new ScrollingTabsAdapter(this);
        mScrollingTabs.setAdapter(mScrollingTabsAdapter);
        mScrollingTabs.setViewPager(mViewPager);

        // Theme chooser
        ThemeUtils.initThemeChooser(this, mScrollingTabs, "scrollable_tab_background",
                THEME_ITEM_BACKGROUND);
    }

    /**
     * Initiate the BottomActionBar
     */
    private void initBottomActionBar() {
        PagerAdapter pagerAdatper = new PagerAdapter(getSupportFragmentManager());
        pagerAdatper.addFragment(new BottomActionBarFragment());
        pagerAdatper.addFragment(new BottomActionBarControlsFragment());
        ViewPager viewPager = (ViewPager)findViewById(R.id.bottomActionBarPager);
        viewPager.setAdapter(pagerAdatper);
    }
}
