/**
 * 
 */

package com.spacecaker.butter.tasks;

import java.lang.ref.WeakReference;
import java.util.Iterator;

import android.content.Context;
import android.os.AsyncTask;

import com.spacecaker.butter.lastfm.api.Artist;
import com.spacecaker.butter.lastfm.api.Image;
import com.spacecaker.butter.lastfm.api.ImageSize;
import com.spacecaker.butter.lastfm.api.PaginatedResult;
import com.spacecaker.butter.utils.ApolloUtils;
import com.androidquery.AQuery;

import static com.spacecaker.butter.Constants.ARTIST_IMAGE;
import static com.spacecaker.butter.Constants.LASTFM_API_KEY;

/**
 * @author Andrew Neal
 * @returns A convenient image size that's perfect for a GridView.
 */
public class LastfmGetArtistImages extends AsyncTask<String, Integer, String> {

    // URL to cache
    private String url = null;

    private PaginatedResult<Image> artist;

    // AQuery
    private final AQuery aq;

    private final WeakReference<Context> contextReference;

    public LastfmGetArtistImages(Context context) {
        contextReference = new WeakReference<Context>(context);

        // Initiate AQuery
        aq = new AQuery(contextReference.get());
    }

    @Override
    protected String doInBackground(String... artistname) {
        if (ApolloUtils.isOnline(contextReference.get()) && artistname[0] != null) {
            try {
                artist = Artist.getImages(artistname[0], 1, 1, LASTFM_API_KEY);
                Iterator<Image> iterator = artist.getPageResults().iterator();
                while (iterator.hasNext()) {
                    Image mTemp = iterator.next();
                    url = mTemp.getImageURL(ImageSize.LARGESQUARE);
                }
                aq.cache(url, 0);
                ApolloUtils.setImageURL(artistname[0], url, ARTIST_IMAGE, contextReference.get());
                return url;
            } catch (Exception e) {
                return null;
            }
        } else {
            url = ApolloUtils.getImageURL(artistname[0], ARTIST_IMAGE, contextReference.get());
        }
        return url;
    }
}
