package administrator.ui;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.aitsuki.swipe.SwipeMenuRecyclerView;
import com.qrcodescan.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import administrator.adapters.SpaceItemAdapter;
import administrator.base.DeleteCallbackListener;
import administrator.entity.Space;

public class SpaceManageActivity extends AppCompatActivity {
    private RecyclerView smr;
    private List<Space> spaceList = new ArrayList<>();
    private SpaceItemAdapter adapter;
    private DeleteCallbackListener listener;
    private ImageView goBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_space_manage);

        initData();

        initViews();

        LinearLayoutManager manager = new LinearLayoutManager(this);
        adapter = new SpaceItemAdapter();
        adapter.setSpaceList(spaceList);
        //增加分割线
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        smr.addItemDecoration(dividerItemDecoration);
        adapter.setListener(listener);
        smr.setAdapter(adapter);
        smr.setLayoutManager(manager);

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initViews() {
        smr = (RecyclerView) findViewById(R.id.smr);
        goBack = (ImageView)findViewById(R.id.go_back);

        listener = new DeleteCallbackListener() {
            @Override
            public void onDelete(int position) {
                deleteSpace(position);
            }
        };
    }

    private void initData() {
        for (int i = 0; i < 20;i++) {
            spaceList.add(new Space(i,"空间"+i,"lalala",1,new Date()));
        }
    }
    private void deleteSpace(int position) {
        adapter.getSpaceList().remove(position);
        adapter.notifyDataSetChanged();
        }

}
