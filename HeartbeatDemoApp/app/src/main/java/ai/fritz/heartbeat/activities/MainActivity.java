package ai.fritz.heartbeat.activities;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import ai.fritz.core.Fritz;
import ai.fritz.heartbeat.PredictorType;
import ai.fritz.heartbeat.R;
import ai.fritz.heartbeat.adapters.DemoAdapter;
import ai.fritz.heartbeat.adapters.DemoItem;
import ai.fritz.heartbeat.ui.SeparatorDecoration;
import ai.fritz.heartbeat.Navigation;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * The primary activity that shows the different model demos.
 */
public class MainActivity extends AppCompatActivity {

    private static final String FRITZ_URL = "https://sites.google.com/view/renaissance19";

    private Logger logger = Logger.getLogger(this.getClass().getName());

    @BindView(R.id.demo_list_view)
    RecyclerView recyclerView;

    @BindView(R.id.app_toolbar)
    Toolbar appBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.demo_title);
        ButterKnife.bind(this);

        // customize the action bar
        setSupportActionBar(appBar);

        // Initialize Fritz
        Fritz.configure(this);

        // Setup the recycler view
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager rvLinearLayoutMgr = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(rvLinearLayoutMgr);

        // Add a divider
        SeparatorDecoration decoration = new SeparatorDecoration(this, Color.GRAY, 1);
        recyclerView.addItemDecoration(decoration);

        // Add the adapter
        DemoAdapter adapter = new DemoAdapter(getDemoItems());
        recyclerView.setAdapter(adapter);
        recyclerView.setClickable(true);
    }

    private List<DemoItem> getDemoItems() {
        // Add different demo items here
        List<DemoItem> demoItems = new ArrayList<>();

        demoItems.add(new DemoItem(
                getString(R.string.fritz_vision_style_transfer),
                getString(R.string.fritz_vision_style_transfer_description),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Navigation.goToStyleTransfer(v.getContext());
                    }
                }));
        demoItems.add(new DemoItem(
                getString(R.string.fritz_info_title),
                getString(R.string.fritz_info_description),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(FRITZ_URL));
                        startActivity(i);
                    }
                }));
        return demoItems;
    }
}
