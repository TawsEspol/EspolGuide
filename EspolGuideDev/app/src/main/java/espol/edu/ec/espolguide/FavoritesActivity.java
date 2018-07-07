package espol.edu.ec.espolguide;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.Observable;
import java.util.Observer;

import espol.edu.ec.espolguide.viewModels.FavoritesViewModel;

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

    }
    private class ViewHolder{

        public ViewHolder(){
            findViews();
        }

        public void findViews(){
            return;
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
        if (message == "") {

        }
    }
}
