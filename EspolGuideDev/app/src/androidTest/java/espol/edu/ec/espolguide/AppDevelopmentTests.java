package espol.edu.ec.espolguide;

/**
 * Created by fabricio on 14/08/18.
 */

import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.ListView;

import com.mapbox.mapboxsdk.maps.MapView;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.hdodenhof.circleimageview.CircleImageView;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class AppDevelopmentTests {

    @Rule
    public ActivityTestRule<MapActivity> rule  = new  ActivityTestRule<>(MapActivity.class);

    @Rule
    public ActivityTestRule<FavoritesActivity> rule2  = new  ActivityTestRule<>(FavoritesActivity.class);


    @Test
    public void ensureSearchViewIsAvailable() throws Exception {
        MapActivity activity = rule.getActivity();
        View viewById = activity.findViewById(R.id.listview);
        assertThat(viewById,notNullValue());
        assertThat(viewById, instanceOf(ListView.class));
    }

    @Test
    public void ensureFavoritesShowing() throws Exception {
        FavoritesActivity activity = rule2.getActivity();
        View viewById = activity.findViewById(R.id.favorites_lv);
        assertThat(viewById,notNullValue());
        assertThat(viewById, instanceOf(ListView.class));
        ListView fav_lv = (ListView) viewById;
        assertEquals(fav_lv.getAdapter().getCount(),activity.getViewModel().getFavoritePlaces().size());
    }

    @Test
    public void ensurePhotoIsShowed() throws Exception {
        MapActivity activity = rule.getActivity();
        View viewById = activity.findViewById(R.id.profile_image);
        assertThat(viewById,notNullValue());
        assertThat(viewById, instanceOf(CircleImageView.class));
    }

    @Test
    public void ensureMapIsReady() throws Exception {
        MapActivity activity = rule.getActivity();
        View viewById = activity.findViewById(R.id.mapView);
        assertThat(viewById,notNullValue());
        assertThat(viewById, instanceOf(MapView.class));
    }

}
