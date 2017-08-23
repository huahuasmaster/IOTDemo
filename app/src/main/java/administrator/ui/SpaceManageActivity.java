package administrator.ui;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
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
import com.qrcodescan.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import administrator.adapters.SpaceItemAdapter;
import administrator.adapters.listener.SwipeItemCallbackListener;
import administrator.entity.Space;

public class SpaceManageActivity extends AppCompatActivity {
    private RecyclerView smr;
    private List<Space> spaceList = new ArrayList<>();
    private SpaceItemAdapter adapter;
    private SwipeItemCallbackListener listener;
    private ImageView goBack;
    private FloatingActionButton fab;

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

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddDialog();
            }
        });
    }

    private void initData() {
        for (int i = 0; i < 20; i++) {
            spaceList.add(new Space(i, "空间" + i, "lalala", 1, new Date()));
        }
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
                .title("新名称")
                .content("请输入空间的新名称")
                .inputType(InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_VARIATION_PERSON_NAME
                        | InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .inputRange(2, 10)
                .input("新名称", "", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        editSpace(position, input.toString());
                    }
                }).show();
    }

    private void showAddDialog() {
        new MaterialDialog.Builder(this)
                .title("新空间名称")
                .inputType(InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_VARIATION_PERSON_NAME
                        | InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .inputRange(2, 16)
                .positiveText("确定")
                .input("请输入新名称", "我的空间", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        addSpace(input.toString());
                    }
                }).show();
    }

    private void showDeleteDialog(final int position) {
        new MaterialDialog.Builder(this)
                .title("确认删除?")
                .content("删除空间后，您将无法查看此空间的所有信息")
                .positiveText("确认")
                .negativeText("取消")
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
        adapter.getSpaceList().get(position)
                .setName(newName);
        adapter.notifyDataSetChanged();
    }

    private void addSpace(String newName) {
        adapter.getSpaceList().add(0, new Space(1, newName, "lalal", 1, new Date()));
        adapter.notifyDataSetChanged();
    }

    /**
     * 用以删除空间的方法
     *
     * @param position
     */
    private void deleteSpace(int position) {
        adapter.getSpaceList().remove(position);
        adapter.notifyDataSetChanged();
    }

}
