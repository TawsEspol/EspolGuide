package espol.edu.ec.espolguide;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ListView;

import java.util.Observable;
import java.util.Observer;

import espol.edu.ec.espolguide.viewModels.FavoritesViewModel;

/**
 * Created by galo on 04/07/18.
 */

public class FavoritesActivity extends BaseActivity implements Observer {
    private FavoritesViewModel viewModel;
    private ViewHolder viewHolder;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame); //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.activity_favorites, contentFrameLayout);
        this.viewHolder = new ViewHolder();
        this.viewModel = new FavoritesViewModel(this);
        this.viewModel.addObserver(this);
        this.viewModel.loadFavorites();
    }

    public class ViewHolder{
        public ListView favoritesLv;

        public ViewHolder(){
            findViews();
        }

        public void findViews(){
            favoritesLv = (ListView) findViewById(R.id.favorites_lv);
        }
    }

    public ViewHolder getViewHolder() {
        return this.viewHolder;
    }

    public void setViewHolder(ViewHolder viewHolder){
        this.viewHolder = viewHolder;
    }

    public FavoritesViewModel getViewModel() {
        return viewModel;
    }

    public void setViewModel(FavoritesViewModel viewModel){
        this.viewModel = viewModel;
    }

    @Override
    public void update(Observable o, Object arg) {
        String message = (String)arg;
        if (message == viewModel.LOAD_FAVORITES_STARTED) {

        }
        if (message == viewModel.LOAD_FAVORITES_SUCCEEDED) {

        }
        if (message == viewModel.FAVORITES_NOT_FOUND) {

        }
        if (message == viewModel.LOAD_FAVORITES_FAILED) {

        }
        if (message == viewModel.REQUEST_FAILED_CONNECTION) {

        }
        if (message == viewModel.REQUEST_FAILED_HTTP) {

        }
        if (message == viewModel.GET_FAVORITES_REQUEST_STARTED) {

        }
        if (message == viewModel.GET_FAVORITES_REQUEST_SUCCEEDED) {

        }
        if (message == viewModel.GET_FAVORITES_REQUEST_FAILED_LOADING) {

        }
    }
}
