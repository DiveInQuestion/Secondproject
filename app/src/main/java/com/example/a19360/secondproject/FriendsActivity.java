package com.example.a19360.secondproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;

public class FriendsActivity extends Activity {
    private Button btn_enemies,btn_radar,btn_add,btn_ok,btn_close;
    private ToggleButton btn_edit;
    private FriendsCollectionOperator friendsCollectionOperator;
    private TextView Name,PhoneNumber;
    private TextView delete_name,delete_number;
    private boolean flag = false;
    ListViewAdapter theListAdapter = new ListViewAdapter();
    ListView listView;
    LinearLayout add;
    //创建菜单项
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle("操作");
        menu.add(0, 1, 0, "修改");
        super.onCreateContextMenu(menu, v, menuInfo);
    }
    //响应菜单项点击事件
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // 获取当前被选择的菜单项的信息
        final AdapterView.AdapterContextMenuInfo itemInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case 1:
                final AlertDialog.Builder builder=new AlertDialog.Builder(FriendsActivity.this);
                //AlertDialog.Builder builder = new AlertDialog.Builder(FriendActivity.this);
                final Dialog dialog;
                //设置添加好友对话框并显示
                add=(LinearLayout)getLayoutInflater().inflate(R.layout.dialog_add_friend,null);
                Name=(EditText)add.findViewById(R.id.txt_friend_name);
                PhoneNumber=(EditText)add.findViewById(R.id.txt_friend_number);
                btn_ok=(Button)add.findViewById(R.id.btn_dialog_ok);
                btn_close=(Button)add.findViewById(R.id.btn_dialog_close);
                //dialog.setTitle("ADD FRIEND").setMessage("请输入好友的名字和电话号码").setView(add);
                builder.setView(add);
                //dialog.setIcon(R.drawable.dialog_add_friend);
                // builder.show();
                //AlertDialog.Builder builder = new AlertDialog.Builder(Activity.this);
                dialog = builder.show();
                //给确定和取消按钮设置点击事件。
                //关联添加好友的两个确定和取消按钮
                Name.setText(getFriendsCollection().get(itemInfo.position).getName());
                PhoneNumber.setText(getFriendsCollection().get(itemInfo.position).getPhoneNumber());
                btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String name=Name.getText().toString();
                        String number= PhoneNumber.getText().toString();
                       // getFriendsCollection().get(itemInfo.position).setLatitude(name);
                       // getFriendsCollection().get(itemInfo.position).setLongitude(number);
                        getFriendsCollection().get(itemInfo.position).setName(name);
                        getFriendsCollection().get(itemInfo.position).setPhoneNumber(number);
                        friendsCollectionOperator.save(FriendsActivity.this.getBaseContext(), getFriendsCollection());
                        //theListAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                        theListAdapter = new ListViewAdapter();
                        listView.setAdapter(theListAdapter);
                    }
                });
                btn_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.friends_list);
        listView = (ListView)findViewById(R.id.lvw_friends_list);
        btn_enemies = (Button) findViewById(R.id.btn_friends_list_enemies);
        btn_radar = (Button)findViewById(R.id.btn_friends_list_radar);
        btn_add = (Button)findViewById(R.id.btn_friends_list_add);
        btn_edit = (ToggleButton)findViewById(R.id.btn_friends_list_edit);

        friendsCollectionOperator = new FriendsCollectionOperator();
        if(getFriendsCollection()!=null&&getFriendsCollection().size()!=0){
            theListAdapter = new ListViewAdapter();
            listView.setAdapter(theListAdapter);
        }
        LayoutInflater inflater = (LayoutInflater)FriendsActivity.this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        registerForContextMenu(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(FriendsActivity.this,FriendsDetailActivity.class);
                intent.putExtra("name",""+getFriendsCollection().get(position).getName());
                intent.putExtra("phoneNumber",""+getFriendsCollection().get(position).getPhoneNumber());
                intent.putExtra("lantitude",""+getFriendsCollection().get(position).getLatitude());
                intent.putExtra("longtitude",""+getFriendsCollection().get(position).getLongitude());
                intent.putExtra("position",""+position);
                intent.putExtra("activity","2");
                //Toast.makeText(getApplicationContext(),getFriendsCollection().get(position).getLatitude(),Toast.LENGTH_SHORT).show();
                startActivityForResult(intent,1);
            }
        });
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag=!flag;
                theListAdapter = new ListViewAdapter();
                listView.setAdapter(theListAdapter);
            }
                /*
                change =(RelativeLayout)getLayoutInflater().inflate(R.layout.friends_list_item,null);
                delete = (Button) change.findViewById(R.id.delete_friend_button);
                if(flag == false) {

                    delete.setVisibility(View.VISIBLE);
                    theListAdapter = new ListViewAdapter();
                    listView.setAdapter(theListAdapter);
                    if(delete.getVisibility() == View.VISIBLE)
                        Toast.makeText(getApplicationContext(),"可视",Toast.LENGTH_SHORT).show();
                    flag = true;
                }
                else {
                    delete.setVisibility(View.INVISIBLE);
                    theListAdapter = new ListViewAdapter();
                    listView.setAdapter(theListAdapter);
                    Toast.makeText(getApplicationContext(),"不可视",Toast.LENGTH_SHORT).show();
                    flag = false;
                }
            }
            */
        });
        btn_add.setOnClickListener(new View.OnClickListener() {
            final AlertDialog.Builder builder=new AlertDialog.Builder(FriendsActivity.this);
            Dialog dialog;
            @Override
            public void onClick(View v) {
                add=(LinearLayout)getLayoutInflater().inflate(R.layout.dialog_add_friend,null);
                builder.setView(add);
                dialog = builder.show();
                btn_ok=(Button)add.findViewById(R.id.btn_dialog_ok);
                btn_close=(Button)add.findViewById(R.id.btn_dialog_close);
                btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Name=(EditText)add.findViewById(R.id.txt_friend_name);
                        PhoneNumber=(EditText)add.findViewById(R.id.txt_friend_number);
                        String name=Name.getText().toString();
                        String number= PhoneNumber.getText().toString();
                        if(name.length()!=0&&number.length()!=0){
                            Friends friends = new Friends();
                            friends.setName(name);
                            friends.setPhoneNumber(number);
                            getFriendsCollection().add(friends);
                            friendsCollectionOperator = new FriendsCollectionOperator();
                            friendsCollectionOperator.save(FriendsActivity.this.getBaseContext(), getFriendsCollection());
                            //Toast.makeText(getApplicationContext(),"保存成功",Toast.LENGTH_SHORT).show();
                            theListAdapter.notifyDataSetChanged();
                        }
                        dialog.dismiss();
                    }
                });
                btn_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });
        btn_enemies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(FriendsActivity.this,EnemiesActivity.class);
                startActivity(intent);
            }
        });
        btn_radar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(FriendsActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //Toast.makeText(getApplicationContext(), ""+temp, Toast.LENGTH_SHORT).show();
        if (requestCode==1)
        {
            if (resultCode==FriendsDetailActivity.RESULT_OK)
            {
                    theListAdapter.notifyDataSetChanged();
            }
        }
    }
    public class ListViewAdapter extends BaseAdapter{

        @Override
        public int getCount() {
                return getFriendsCollection().size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }
        @Override
        public long getItemId(int position) {
            return 0;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view =null;
            int type = getItemViewType(position);
            switch (type){
                case 0:
                    view= LayoutInflater.from(FriendsActivity.this).inflate(R.layout.friends_list_item,parent,false);
                    TextView textView1 = (TextView) view.findViewById(R.id.name_cell);
                    Button btn_delete = (Button)view.findViewById(R.id.delete_friend_button);
                    textView1.setText(getFriendsCollection().get(position).getName());
                    //item内点击按钮
                    if(flag)
                        btn_delete.setVisibility(View.VISIBLE);
                    else
                        btn_delete.setVisibility(View.INVISIBLE);
                    btn_delete.setOnClickListener(new View.OnClickListener() {
                        final AlertDialog.Builder builder=new AlertDialog.Builder(FriendsActivity.this);
                        //AlertDialog.Builder builder = new AlertDialog.Builder(FriendActivity.this);
                        Dialog dialog;
                        @Override
                        public void onClick(View v) {
                            //设置添加好友对话框并显示
                            add=(LinearLayout)getLayoutInflater().inflate(R.layout.dialog_delete,null);
                            delete_name = (TextView)add.findViewById(R.id.txt_friend_name);
                            delete_number = (TextView)add.findViewById(R.id.txt_friend_number);
                            //dialog.setTitle("ADD FRIEND").setMessage("请输入好友的名字和电话号码").setView(add);
                            builder.setView(add);
                            //dialog.setIcon(R.drawable.dialog_add_friend);
                            // builder.show();
                            //AlertDialog.Builder builder = new AlertDialog.Builder(Activity.this);
                            delete_name.setText(getFriendsCollection().get(position).getName());
                            delete_number.setText(getFriendsCollection().get(position).getPhoneNumber());
                            dialog = builder.show();
                            //给确定和取消按钮设置点击事件。
                            //关联添加好友的两个确定和取消按钮

                            btn_ok=(Button)add.findViewById(R.id.btn_dialog_ok);
                            btn_close=(Button)add.findViewById(R.id.btn_dialog_close);
                            btn_ok.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    getFriendsCollection().remove(position);
                                    friendsCollectionOperator.save(FriendsActivity.this.getBaseContext(), getFriendsCollection());
                                    notifyDataSetChanged();
                                    dialog.dismiss();
                                }
                            });
                            btn_close.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });

                        }
                    });
                    break;
                default:
                    break;
            }
            return view;
        }

        @Override
        public int getViewTypeCount() {
            return 1;//共1种布局
        }

        public int getItemViewType(int position) {
                    return 0;
        }

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setClass(FriendsActivity.this,MainActivity.class);
        startActivity(intent);
    }
    public ArrayList<Friends> getFriendsCollection(){
        return MainActivity.friendsCollection;
    }
}