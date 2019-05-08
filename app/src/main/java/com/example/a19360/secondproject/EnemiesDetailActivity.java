package com.example.a19360.secondproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;

public class EnemiesDetailActivity extends Activity {
    private Button btn_friends,btn_radar,btn_list,btn_delete,btn_ok,btn_close;
    private ToggleButton btn_edit;
    private TextView name,phone,lan_long,delete_name,delete_number;
    private EnemiesCollectionOperator enemiesCollectionOperator = new EnemiesCollectionOperator();
    private boolean flag = false;
    private String activity;
    LinearLayout add;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.enemy_detail);
        btn_friends = (Button) findViewById(R.id.btn_friends);
        btn_radar = (Button)findViewById(R.id.btn_radar);
        btn_list = (Button)findViewById(R.id.btn_enemies_list);
        btn_edit = (ToggleButton)findViewById(R.id.btn_enemies_list_edit);
        btn_delete = (Button)findViewById(R.id.btn_delete);
        name = (TextView) findViewById(R.id.txt_enemy_name);
        phone = (TextView)findViewById(R.id.txt_enemy_number);
        lan_long = (TextView)findViewById(R.id.txt_enemy_long_lang);
        activity=getIntent().getStringExtra("activity");
        name.setText(getIntent().getStringExtra("name"));
        phone.setText(getIntent().getStringExtra("phoneNumber"));
        final int position =Integer.valueOf(getIntent().getStringExtra("position"));
        lan_long.setText(getEnemiesCollection().get(position).getLatitude()+"N"+"/"+getEnemiesCollection().get(position).getLongitude()+"E");
        btn_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activity.equals("2")){
                    Intent intent = new Intent();
                    intent.setClass(EnemiesDetailActivity.this,EnemiesActivity.class);
                    setResult(RESULT_OK,intent);
                    finish();
                }
                else if(activity.equals("1")){
                    Intent intent = new Intent();
                    intent.setClass(EnemiesDetailActivity.this,MainActivity.class);
                    setResult(RESULT_OK,intent);
                    finish();
                }
            }
        });
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = !flag;
                if(flag)
                    btn_delete.setVisibility(View.VISIBLE);
                else
                    btn_delete.setVisibility(View.INVISIBLE);
            }
        });
        btn_delete.setOnClickListener(new View.OnClickListener() {
            final AlertDialog.Builder builder=new AlertDialog.Builder(EnemiesDetailActivity.this);
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
                delete_name.setText(getIntent().getStringExtra("name"));
                delete_number.setText(getIntent().getStringExtra("phoneNumber"));
                dialog = builder.show();
                //给确定和取消按钮设置点击事件。
                //关联添加好友的两个确定和取消按钮

                btn_ok=(Button)add.findViewById(R.id.btn_dialog_ok);
                btn_close=(Button)add.findViewById(R.id.btn_dialog_close);
                btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getEnemiesCollection().remove(position);
                        //Toast.makeText(getApplication(),""+getFriendsCollection().size(),Toast.LENGTH_SHORT).show();
                        enemiesCollectionOperator.save(EnemiesDetailActivity.this.getBaseContext(), getEnemiesCollection());
                        dialog.dismiss();
                        Intent intent = new Intent();
                        intent.setClass(EnemiesDetailActivity.this,EnemiesActivity.class);
                        setResult(RESULT_OK,intent);
                        finish();
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
                intent.setClass(EnemiesDetailActivity.this,FriendsActivity.class);
                startActivity(intent);
            }
        });
        btn_radar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(EnemiesDetailActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(activity.equals("1")){
            Intent intent = new Intent();
            intent.setClass(EnemiesDetailActivity.this,MainActivity.class);
            setResult(RESULT_OK,intent);
            finish();
        }
        else if(activity.equals("2")){
            Intent intent = new Intent();
            intent.setClass(EnemiesDetailActivity.this,EnemiesActivity.class);
            setResult(RESULT_OK,intent);
            finish();
        }
    }
    public ArrayList<Enemies> getEnemiesCollection(){
        return MainActivity.enemiesCollection;
    }
}
