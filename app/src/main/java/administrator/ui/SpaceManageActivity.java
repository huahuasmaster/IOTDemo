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
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lichfaker.log.Logger;
import com.qrcodescan.R;

import java.util.ArrayList;
import java.util.List;

import administrator.adapters.SpaceItemAdapter;
import administrator.adapters.listener.SwipeItemCallbackListener;
import administrator.base.http.HttpCallbackListener;
import administrator.base.http.HttpUtil;
import administrator.base.http.UrlHandler;
import administrator.entity.SpaceDto;

public class SpaceManageActivity extends AppCompatActivity {
    private RecyclerView smr;
    private List<SpaceDto> spaceList = new ArrayList<>();
    private SpaceItemAdapter adapter;
    private SwipeItemCallbackListener listener;
    private ImageView goBack;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_space_manage);



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

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddDialog();
            }
        });

        initData();

    }

    private void initData() {
        HttpCallbackListener listener = new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                List<SpaceDto> newList = new Gson().fromJson(response,
                        new TypeToken<List<SpaceDto>>(){}.getType());
                adapter.setSpaceList(newList);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
            }
        };

        String url = UrlHandler.getAllSpace();

        HttpUtil.sendRequestWithCallback(url,listener);
    }

    private void initViews() {
        smr = (RecyclerView) findViewById(R.id.smr);
        goBack = (ImageView) findViewById(R.id.go_back);
        fab = (FloatingActionButton) findViewById(R.id.fab);

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

    private void showEditDialog(final int position) {
        new MaterialDialog.Builder(this)
                .title(R.string.new_name)
                .content(R.string.plz_type_in_new_space_name)
                .inputType(InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_VARIATION_PERSON_NAME
                        | InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .inputRange(2, 10)
                .input(getString(R.string.new_name), "", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        editSpace(position, input.toString());
                    }
                }).show();
    }

    private void showAddDialog() {
        new MaterialDialog.Builder(this)
                .title(R.string.new_space_name)
                .inputType(InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_VARIATION_PERSON_NAME
                        | InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .inputRange(2, 16)
                .positiveText(R.string.confirm)
                .input(getString(R.string.plz_type_in_new_space_name), getString(R.string.default_space),
                        false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {addSpace(input.toString());
                    }
                })
                .show();
    }

    private void showDeleteDialog(final int position) {
        new MaterialDialog.Builder(this)
                .title(R.string.sure_to_delete)
                .content(R.string.sure_to_delete_content)
                .positiveText(R.string.confirm)
                .negativeText(R.string.cancle)
                .onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (which == DialogAction.POSITIVE) {
                            deleteSpace(position);
                        }
                    }
                }).show();
    }


    /**
     * 用以修改空间的方法
     *
     * @param position
     * @param newName
     */
    private void editSpace(int position, String newName) {
        long spaceId = adapter.getSpaceList().get(position).getId();
        String url = UrlHandler.editSpace(spaceId,newName);
        Logger.i("url = "+url);
        HttpCallbackListener listener = new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(fab, R.string.edit_successfully,Snackbar.LENGTH_SHORT).show();
                        initData();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(fab, R.string.failed_work,Snackbar.LENGTH_SHORT).show();
                    }
                });
            }
        };
        HttpUtil.sendRequestWithCallback(url,listener);
    }

    private void addSpace(String newName) {
        HttpCallbackListener listener = new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(fab, R.string.add_successfully,Snackbar.LENGTH_SHORT).show();
                        initData();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(fab, R.string.failed_work,Snackbar.LENGTH_SHORT).show();
                    }
                });
            }
        };
        String url = UrlHandler.addSpace(newName);
        HttpUtil.sendRequestWithCallback(url,listener);
    }

    /**
     * 用以删除空间的方法
     *
     * @param position
     */
    private void deleteSpace(int position) {
        long spaceId = adapter.getSpaceList().get(position).getId();
        Logger.i("spaceId = "+spaceId);
        String url = UrlHandler.deleteSpace(spaceId);
        Logger.i("url = "+url);
        HttpCallbackListener listener = new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(smr,R.string.delete_successfully,Snackbar.LENGTH_SHORT).show();
                        initData();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(smr,R.string.failed_work,Snackbar.LENGTH_SHORT).show();
                    }
                });
            }
        };
        HttpUtil.sendRequestWithCallback(url,listener);
    }

}
