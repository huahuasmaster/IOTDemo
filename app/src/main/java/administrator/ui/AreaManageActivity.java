package administrator.ui;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;
import android.widget.ImageView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qrcodescan.R;

import java.util.ArrayList;
import java.util.List;

import administrator.adapters.AreaItemAdapter;
import administrator.adapters.listener.SwipeItemCallbackListener;
import administrator.base.http.HttpCallbackListener;
import administrator.base.http.HttpUtil;
import administrator.base.http.UrlHandler;
import administrator.entity.AreaDto;

public class AreaManageActivity extends AppCompatActivity {

    private RecyclerView rv;
    private FloatingActionButton fab;
    private AreaItemAdapter adapter;
    private SwipeItemCallbackListener listener;
    private ImageView goBack;

    private List<AreaDto> areaList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_manage);


        initViews();

        adapter = new AreaItemAdapter();
        adapter.setRoomList(areaList);
        adapter.setListener(listener);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        DividerItemDecoration decoration = new DividerItemDecoration(
                this,DividerItemDecoration.VERTICAL);
        rv.setAdapter(adapter);
        rv.setLayoutManager(manager);
        rv.addItemDecoration(decoration);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddDialog();
            }
        });

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        initData();

    }

    private void initViews() {
        rv = (RecyclerView) findViewById(R.id.smr);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        goBack = (ImageView) findViewById(R.id.go_back);

        listener = new SwipeItemCallbackListener() {
            @Override
            public void onDelete(int position) {
                showDeleteDialog(position);
            }

            @Override
            public void onEdit(int position) {
                showEditDialog(position);
            }
        };
    }

    private void addArea(final String s) {
        String url = UrlHandler.addArea(s);
        HttpCallbackListener listener = new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(fab,getString(R.string.new_area)+s,Snackbar.LENGTH_SHORT)
                                .show();
                        initData();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                Snackbar.make(fab, R.string.failed_work,Snackbar.LENGTH_SHORT)
                        .show();
            }
        };
        HttpUtil.sendRequestWithCallback(url,listener);
    }

    private void editArea(int position, String newName) {
        long areaId = adapter.getRoomList().get(position).getId();
        String url = UrlHandler.editArea(areaId,newName);
        HttpCallbackListener listener = new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(fab, R.string.edit_successfully,Snackbar.LENGTH_SHORT)
                                .show();
                        initData();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                Snackbar.make(fab, R.string.failed_work,Snackbar.LENGTH_SHORT)
                        .show();
            }
        };
        HttpUtil.sendRequestWithCallback(url,listener);
    }
    private void showEditDialog(final int position) {
        new MaterialDialog.Builder(this)
                .title(R.string.new_name)
                .content(R.string.plz_type_in_new_area_name)
                .inputType(InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_VARIATION_PERSON_NAME
                        | InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .inputRange(2,10)
                .input(getString(R.string.new_name), "", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        editArea(position,input.toString());
                    }
                }).show();
    }

    private void showAddDialog() {
        new MaterialDialog.Builder(this)
                .title(R.string.new_area_name)
                .inputType(InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_VARIATION_PERSON_NAME
                        | InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .inputRange(2,16)
                .positiveText(R.string.confirm)
                .input(getString(R.string.plz_type_in_new_area_name),
                        getString(R.string.default_area), false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        addArea(input.toString());
                    }
                }).show();
    }

    private void showDeleteDialog(final int position) {
        new MaterialDialog.Builder(this)
                .title(R.string.sure_to_delete)
                .content(R.string.device_still_work)
                .positiveText(R.string.confirm)
                .negativeText(R.string.cancle)
                .onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if(which == DialogAction.POSITIVE) {
                            deleteArea(position);
                        }
                    }
                }).show();
    }

    private void deleteArea(int position) {
        long areaId = adapter.getRoomList().get(position).getId();
        String url = UrlHandler.deleteArea(areaId);
        HttpCallbackListener listener = new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(fab, R.string.delete_successfully,Snackbar.LENGTH_SHORT)
                                .show();
                        initData();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                Snackbar.make(fab, R.string.failed_work,Snackbar.LENGTH_SHORT)
                        .show();
            }
        };
        HttpUtil.sendRequestWithCallback(url,listener);
    }
    private void initData() {
        HttpCallbackListener listener = new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                final List<AreaDto> areaDtos = new Gson().fromJson(response,
                        new TypeToken<List<AreaDto>>(){}.getType());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.setRoomList(areaDtos);
                        adapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onError(Exception e) {

            }
        };
        String url = UrlHandler.getAreaOfDefaultSpace();
        HttpUtil.sendRequestWithCallback(url,listener);
    }
}
