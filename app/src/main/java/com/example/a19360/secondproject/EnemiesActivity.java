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
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;


public class EnemiesActivity extends Activity {
    private Button btn_friends,btn_radar,btn_add,btn_ok,btn_close;
    private EnemiesCollectionOperator enemiesCollectionOperator = new EnemiesCollectionOperator();
    private EditText Name,PhoneNumber;
    private TextView delete_name,delete_number;
    private ToggleButton btn_edit;
    private boolean flag = false;
    ListViewAdapter theListAdapter;
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
                final AlertDialog.Builder builder=new AlertDialog.Builder(EnemiesActivity.this);
                //AlertDialog.Builder builder = new AlertDialog.Builder(FriendActivity.this);
                final Dialog dialog;
                //设置添加好友对话框并显示
                add=(LinearLayout)getLayoutInflater().inflate(R.layout.dialog_add_enemy,null);
                Name=(EditText)add.findViewById(R.id.txt_enemy_name);
                PhoneNumber=(EditText)add.findViewById(R.id.txt_enemy_number);
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
                Name.setText(getEnemiesCollection().get(itemInfo.position).getName());
                PhoneNumber.setText(getEnemiesCollection().get(itemInfo.position).getPhoneNumber());
                btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name=Name.getText().toString();
                        String number= PhoneNumber.getText().toString();
                        //getEnemiesCollection().get(itemInfo.position).setLatitude(name);
                        //getEnemiesCollection().get(itemInfo.position).setLongitude(number);
                        getEnemiesCollection().get(itemInfo.position).setName(name);
                        getEnemiesCollection().get(itemInfo.position).setPhoneNumber(number);
                        enemiesCollectionOperator.save(EnemiesActivity.this.getBaseContext(), getEnemiesCollection());
                        theListAdapter = new ListViewAdapter();
                        listView.setAdapter(theListAdapter);
                        dialog.dismiss();
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
        setContentView(R.layout.enemies_list);
        btn_friends = (Button) findViewById(R.id.btn_enemies_list_friends);
        btn_radar = (Button)findViewById(R.id.btn_enemies_list_radar);
        btn_add = (Button)findViewById(R.id.btn_enemies_list_add);
        listView = (ListView)findViewById(R.id.lvw_enemies_list);
        btn_edit = (ToggleButton)findViewById(R.id.btn_enemies_list_edit);

        theListAdapter = new ListViewAdapter();
        //Toast.makeText(getApplicationContext(), ""+friendsCollection.size(), Toast.LENGTH_SHORT).show();
        listView.setAdapter(theListAdapter);
        LayoutInflater inflater = (LayoutInflater)EnemiesActivity.this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        registerForContextMenu(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(EnemiesActivity.this,EnemiesDetailActivity.class);
                intent.putExtra("name",""+getEnemiesCollection().get(position).getName());
                intent.putExtra("phoneNumber",""+getEnemiesCollection().get(position).getPhoneNumber());
                intent.putExtra("latitude",""+getEnemiesCollection().get(position).getLatitude());
                intent.putExtra("longitude",""+getEnemiesCollection().get(position).getLongitude());
                intent.putExtra("position",""+position);
                intent.putExtra("activity","2");
                //Toast.makeText(getApplicationContext(),"点击了",Toast.LENGTH_SHORT).show();
                startActivityForResult(intent,1);
            }
        });
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag=!flag;
                theListAdapter = new EnemiesActivity.ListViewAdapter();
                listView.setAdapter(theListAdapter);
            }
        });
        btn_add.setOnClickListener(new View.OnClickListener() {
            final AlertDialog.Builder builder=new AlertDialog.Builder(EnemiesActivity.this);
            //AlertDialog.Builder builder = new AlertDialog.Builder(FriendActivity.this);
            Dialog dialog;
            @Override
            public void onClick(View v) {
                //设置添加好友对话框并显示
                add=(LinearLayout)getLayoutInflater().inflate(R.layout.dialog_add_enemy,null);
                //dialog.setTitle("ADD FRIEND").setMessage("请输入好友的名字和电话号码").setView(add);
                builder.setView(add);
                //dialog.setIcon(R.drawable.dialog_add_friend);
                // builder.show();
                //AlertDialog.Builder builder = new AlertDialog.Builder(Activity.this);
                dialog = builder.show();
                //给确定和取消按钮设置点击事件。
                //关联添加好友的两个确定和取消按钮
                btn_ok=(Button)add.findViewById(R.id.btn_dialog_ok);
                btn_close=(Button)add.findViewById(R.id.btn_dialog_close);
                btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Name=(EditText)add.findViewById(R.id.txt_enemy_name);
                        PhoneNumber=(EditText)add.findViewById(R.id.txt_enemy_number);
                        String name=Name.getText().toString();
                        String number= PhoneNumber.getText().toString();
                        if(name.length()!=0&&number.length()!=0) {
                            Enemies enemies = new Enemies();
                            enemies.setName(name);
                            enemies.setPhoneNumber(number);
                            getEnemiesCollection().add(enemies);
                            enemiesCollectionOperator = new EnemiesCollectionOperator();
                            enemiesCollectionOperator.save(EnemiesActivity.this.getBaseContext(), getEnemiesCollection());
                            listView.deferNotifyDataSetChanged();
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

        btn_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(EnemiesActivity.this,FriendsActivity.class);
                startActivity(intent);
            }
        });
        btn_radar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(EnemiesActivity.this,MainActivity.class);
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
            if (resultCode==EnemiesDetailActivity.RESULT_OK)
            {
                theListAdapter.notifyDataSetChanged();
            }
        }
    }
    public class ListViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return getEnemiesCollection().size();
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
                    view= LayoutInflater.from(EnemiesActivity.this).inflate(R.layout.enemies_list_item,parent,false);
                    TextView textView1 = (TextView) view.findViewById(R.id.name_cell);
                    Button btn_delete = (Button)view.findViewById(R.id.delete_enemies_button);
                    textView1.setText(getEnemiesCollection().get(position).getName());
                    if(flag)
                        btn_delete.setVisibility(View.VISIBLE);
                    else
                        btn_delete.setVisibility(View.INVISIBLE);
                    btn_delete.setOnClickListener(new View.OnClickListener() {
                        final AlertDialog.Builder builder=new AlertDialog.Builder(EnemiesActivity.this);
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
                            delete_name.setText(getEnemiesCollection().get(position).getName());
                            delete_number.setText(getEnemiesCollection().get(position).getPhoneNumber());
                            dialog = builder.show();
                            //给确定和取消按钮设置点击事件。
                            //关联添加好友的两个确定和取消按钮

                            btn_ok=(Button)add.findViewById(R.id.btn_dialog_ok);
                            btn_close=(Button)add.findViewById(R.id.btn_dialog_close);
                            btn_ok.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    getEnemiesCollection().remove(position);
                                    enemiesCollectionOperator.save(EnemiesActivity.this.getBaseContext(), getEnemiesCollection());
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
        intent.setClass(EnemiesActivity.this,MainActivity.class);
        startActivity(intent);
    }
    public ArrayList<Enemies> getEnemiesCollection(){
        return MainActivity.enemiesCollection;
    }
}

