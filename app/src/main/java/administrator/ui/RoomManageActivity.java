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
import com.qrcodescan.R;

import java.util.ArrayList;
import java.util.List;

import administrator.adapters.AreaItemAdapter;
import administrator.adapters.listener.SwipeItemCallbackListener;
import administrator.entity.Room;
import administrator.entity.Space;

public class RoomManageActivity extends AppCompatActivity {

    private RecyclerView rv;
    private FloatingActionButton fab;
    private AreaItemAdapter adapter;
    private SwipeItemCallbackListener listener;
    private ImageView goBack;

    private List<Room> roomList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_manage);

        initData();

        initViews();

        adapter = new AreaItemAdapter();
        adapter.setRoomList(roomList);
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

    private void addRoom(String s) {
        Room room = new Room(1,s,new Space());
        adapter.getRoomList().add(0,room);
        adapter.notifyDataSetChanged();
        Snackbar.make(fab,"新增房间："+s,Snackbar.LENGTH_SHORT).show();
    }

    private void editRoom(int position,String newName) {
        adapter.getRoomList().get(position).setName(newName);
        adapter.notifyDataSetChanged();

    }
    private void showEditDialog(final int position) {
        new MaterialDialog.Builder(this)
                .title("新名称")
                .content("请输入房间的新名称")
                .inputType(InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_VARIATION_PERSON_NAME
                        | InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .inputRange(2,10)
                .input("新名称", "", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        editRoom(position,input.toString());
                    }
                }).show();
    }

    private void showAddDialog() {
        new MaterialDialog.Builder(this)
                .title("新房间名称")
                .inputType(InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_VARIATION_PERSON_NAME
                        | InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .inputRange(2,16)
                .positiveText("确定")
                .input("请输入新名称", "我的房间", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        addRoom(input.toString());
                    }
                }).show();
    }

    private void showDeleteDialog(final int position) {
        new MaterialDialog.Builder(this)
                .title("确认删除?")
                .content("删除房间后，设备仍能正常工作")
                .positiveText("确认")
                .negativeText("取消")
                .onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if(which == DialogAction.POSITIVE) {
                            deleteRoom(position);
                        }
                    }
                }).show();
    }

    private void deleteRoom(int position) {
        adapter.getRoomList().remove(position);
        adapter.notifyDataSetChanged();
        Snackbar.make(fab,"删除成功",Snackbar.LENGTH_SHORT).show();
    }
    private void initData() {
        for(int i = 0;i < 20;i++) {
            roomList.add(new Room(i,"房间"+i,new Space()));
        }
    }
}
