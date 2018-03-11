package lmq.tongdao;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class FriendActivity extends AppCompatActivity {

    private Button btn_inbox,btn_old_friend,btn_new_friend;
    private static int COLOR_TAB_SELECTED = 0xFF60DF3D;
    private static int COLOR_TAB_UNSELECTED = 0xFFa0a0a0;

    public class message {
        public String id;
        public String mes;
        public int type;
        public String time;
        public String thisid;
        message() {
            id = "";
            mes = "";
            type = 0;
            time = "";
            thisid = "";
        }
        message(String a, String b, int c, String d, String e) {
            id = a;
            mes = b;
            type = c;
            time = d;
            thisid = e;
        }
    };

    private class ShowFriendsTask extends AsyncTask<String, Integer, String[]>
    {

        private int rank;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            rank = 0;
            super.onPreExecute();
        }

        @Override
        protected String[] doInBackground(String... params) {
            ArrayList<String> friends = new ArrayList<String>();
            Connection conn = null;
            Statement stmt = null;
            // JDBC 驱动名及数据库 URL
            String JDBC_DRIVER = "com.mysql.jdbc.Driver";
            String URL = AppData.DB_URL;
            String USER = AppData.DB_USER;
            String PASS = AppData.DB_PASS;
            try{
                // 注册 JDBC 驱动
                Class.forName(JDBC_DRIVER);

                // 打开链接
                System.out.println("连接数据库...");
                conn = DriverManager.getConnection(URL,USER,PASS);

                // 执行查询
                System.out.println(" 实例化Statement对...");
                stmt = conn.createStatement();
                String sql;
                if(params[1].equals("old")) {
                    sql = "SELECT second FROM relation WHERE first = \'" + params[0] + "\' and rank = 2" ;
                    rank = 2;
                } else if(params[1].equals("inbox")) {
                    sql = "SELECT sender FROM msg WHERE receiver = \'" + params[0] + "\'";
                    rank = 5;
                }else{
                    sql = "SELECT second FROM relation WHERE first = \'" + params[0] + "\' and rank = 1" ;
                    rank = 1;
                }

                System.out.println("查询语句为："+ sql);
                ResultSet rs = stmt.executeQuery(sql);

                // 展开结果集数据库
                while(rs.next())
                {
                    // 通过字段检索
                    if(rank != 5) {
                        String friendId = rs.getString("second");
                        friends.add(friendId);
                    }
                    else {
                        String friendId = rs.getString("sender");
                        friends.add(friendId);
                    }

                }
                // 完成后关闭
                rs.close();
                stmt.close();
                conn.close();
            }catch(SQLException se){
                // 处理 JDBC 错误
                se.printStackTrace();
            }catch(Exception e){
                // 处理 Class.forName 错误
                e.printStackTrace();
            }finally{
                // 关闭资源
                try{
                    if(stmt!=null) stmt.close();
                }catch(SQLException se2){
                }// 什么都不做
                try{
                    if(conn!=null) conn.close();
                }catch(SQLException se){
                    se.printStackTrace();
                }
            }
            return (String[]) friends.toArray(new String[0]);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // TODO Auto-generated method stub

            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String[] result)
        {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(FriendActivity.this,android.R.layout.simple_list_item_1,result);
            final ListView Friends = (ListView) findViewById(R.id.ListView_Friends);
            Friends.setAdapter(adapter);
            if(rank == 2) {
                Friends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String friendId = Friends.getItemAtPosition(i).toString();
                        Intent intent=new Intent(FriendActivity.this,UserInfoActivity.class);
                        intent.putExtra("id",friendId);
                        startActivity(intent);
                    }
                });
            } else if(rank == 1) {
                Friends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String friendId = Friends.getItemAtPosition(i).toString();
                        Intent intent=new Intent(FriendActivity.this,SendAddFriendMsgActivity.class);
                        intent.putExtra("id",friendId);
                        startActivity(intent);
                    }
                });
            }

        }
    }

    private class ShowFriendsTask1 extends AsyncTask<String, Integer, message[]>
    {

        private int rank;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            rank = 0;
            super.onPreExecute();
        }

        @Override
        protected message[] doInBackground(String... params) {
            ArrayList<message> friends = new ArrayList<message>();
            Connection conn = null;
            Statement stmt = null;
            // JDBC 驱动名及数据库 URL
            String JDBC_DRIVER = "com.mysql.jdbc.Driver";
            String URL = AppData.DB_URL;
            String USER = AppData.DB_USER;
            String PASS = AppData.DB_PASS;
            try{
                // 注册 JDBC 驱动
                Class.forName(JDBC_DRIVER);

                // 打开链接
                System.out.println("连接数据库...");
                conn = DriverManager.getConnection(URL,USER,PASS);

                // 执行查询
                System.out.println(" 实例化Statement对...");
                stmt = conn.createStatement();
                String sql;
                sql = "SELECT sender, msg, type, time FROM msg WHERE receiver = \'" + params[0] + "\'";



                System.out.println("查询语句为："+ sql);
                ResultSet rs = stmt.executeQuery(sql);
                // 展开结果集数据库
                while(rs.next())
                {
                    // 通过字段检索
                    String friendId = rs.getString("sender");
                    String friendMsg = rs.getString("msg");
                    int friendType = rs.getInt("type");
                    String friendTime = rs.getString("time");
                    friends.add(new message(friendId, friendMsg, friendType,friendTime,params[0]));
                }
                // 完成后关闭
                rs.close();
                stmt.close();
                conn.close();
            }catch(SQLException se){
                // 处理 JDBC 错误
                se.printStackTrace();
            }catch(Exception e){
                // 处理 Class.forName 错误
                e.printStackTrace();
            }finally{
                // 关闭资源
                try{
                    if(stmt!=null) stmt.close();
                }catch(SQLException se2){
                }// 什么都不做
                try{
                    if(conn!=null) conn.close();
                }catch(SQLException se){
                    se.printStackTrace();
                }
            }
            return (message[]) friends.toArray(new message[0]);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // TODO Auto-generated method stub

            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(message[] result)
        {
            int length = result.length;
            if(length != 0) {
                final ArrayList<String> friendsId = new ArrayList<String>();
                final ArrayList<String> friendsId1 = new ArrayList<String>();
                final ArrayList<String> friendsMessage = new ArrayList<String>();
                final int[] friendsType = new int[length];
                final String[] friendsTime = new String[length];
                int[] temp = new int[length];
                final String nowid = result[0].thisid;
                for (int i = 0; i < length; i++) {
                    friendsTime[i] = result[i].time;
                    temp[i] = i;
                }
                for (int i = 0; i < length - 1; i++) {
                    for (int j = i + 1; j < length; j++) {
                        boolean a = true;
                        char[] i1 = friendsTime[i].toCharArray();
                        char[] j1 = friendsTime[j].toCharArray();
                        for (int k = 0; k < friendsTime[i].length(); k++) {
                            if ((int) i1[k] < (int) j1[k]) {
                                a = false;
                                break;
                            }
                            if ((int) i1[k] > (int) j1[k]) {
                                a = true;
                                break;
                            }
                        }
                        if (a == false) {
                            String time = friendsTime[i];
                            int te = temp[i];
                            friendsTime[i] = friendsTime[j];
                            temp[i] = temp[j];
                            friendsTime[j] = time;
                            temp[j] = te;
                        }
                    }
                }
                for (int i = 0; i < length; i++) {
                    friendsId.add(result[temp[i]].id);
                    friendsMessage.add(result[temp[i]].mes);
                    friendsType[i] = result[temp[i]].type;
                    if (friendsType[i] == 0) {
                        friendsId1.add(result[temp[i]].id + ":" + result[temp[i]].mes);
                    } else {
                        friendsId1.add("来自" + result[temp[i]].id + "的好友邀请");
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(FriendActivity.this, android.R.layout.simple_list_item_1, friendsId1);
                final ListView Friends = (ListView) findViewById(R.id.ListView_Friends);
                Friends.setAdapter(adapter);

                Friends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String friendId = friendsId.get(i);
                        String friendMsg = friendsMessage.get(i);
                        int type = friendsType[i];
                        Intent intent;
                        if (type == 1) {
                            intent = new Intent(FriendActivity.this, ApplyAddFriendMessageActivity.class);
                        } else {
                            intent = new Intent(FriendActivity.this, ShowMessage.class);
                        }
                        intent.putExtra("id", friendId);
                        intent.putExtra("msg", friendMsg);
                        intent.putExtra("thisid", nowid);
                        startActivity(intent);
                    }
                });
            }

        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_mark:
                    FriendActivity.this.finish();
                    Intent intent_mark=new Intent(FriendActivity.this,MarkActivity.class);
                    startActivity(intent_mark);
                    return true;
                case R.id.navigation_recommend:
                    FriendActivity.this.finish();
                    Intent intent_recommend=new Intent(FriendActivity.this,RecommendActivity.class);
                    startActivity(intent_recommend);
                    return true;
                case R.id.navigation_friend:

                    return true;
            }
            return false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_friend);

        btn_inbox = (Button) findViewById(R.id.friend_inbox_btn);
        btn_new_friend = (Button) findViewById(R.id.friend_new_friend_btn);
        btn_old_friend = (Button) findViewById(R.id.friend_old_friend_btn);

        btn_inbox.setBackgroundColor(COLOR_TAB_UNSELECTED);
        btn_new_friend.setBackgroundColor(COLOR_TAB_UNSELECTED);
        btn_old_friend.setBackgroundColor(COLOR_TAB_SELECTED);

        new ShowFriendsTask().execute(AppData.user_id,"old");
    }

    public void onClick_oldFriend(View view)
    {
        btn_inbox.setBackgroundColor(COLOR_TAB_UNSELECTED);
        btn_new_friend.setBackgroundColor(COLOR_TAB_UNSELECTED);
        btn_old_friend.setBackgroundColor(COLOR_TAB_SELECTED);
        new ShowFriendsTask().execute(AppData.user_id,"old");
    }

    public void onClick_newFriend(View view)
    {
        btn_inbox.setBackgroundColor(COLOR_TAB_UNSELECTED);
        btn_new_friend.setBackgroundColor(COLOR_TAB_SELECTED);
        btn_old_friend.setBackgroundColor(COLOR_TAB_UNSELECTED);
        new ShowFriendsTask().execute(AppData.user_id,"new");
    }

    public void onClick_inbox(View view)
    {
        btn_inbox.setBackgroundColor(COLOR_TAB_SELECTED);
        btn_new_friend.setBackgroundColor(COLOR_TAB_UNSELECTED);
        btn_old_friend.setBackgroundColor(COLOR_TAB_UNSELECTED);
        new ShowFriendsTask1().execute(AppData.user_id,"inbox");
    }

}
