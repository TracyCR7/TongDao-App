package lmq.tongdao;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class RecommendActivity extends AppCompatActivity {

    private Button btn_anonymous,btn_old_friend,btn_new_friend;
    private static int COLOR_TAB_SELECTED = 0xFF60DF3D;
    private static int COLOR_TAB_UNSELECTED = 0xFFa0a0a0;

    private class ShowAnonymousRecommendTask extends AsyncTask<String, Integer, String[]>
    {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        protected String[] doInBackground(String... params) {
            ArrayList<String> movieList = new ArrayList<String>();
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
                sql = "select distinct user_movie.movie_name from user_movie\n" +
                        "\twhere user_movie.user_id \n" +
                        "\t\t\t\tin (select second from relation where first = \'" + params[0] +"\' and rank = 0) \n" +
                        "        and\n" +
                        "\t\t\tuser_movie.movie_name\n" +
                        "\t\t\t\tnot in (select movie_name from user_movie where user_id = \'" + params[0] + "\')\n" +
                        "\torder by user_id;";

                System.out.println("查询语句为："+ sql);
                ResultSet rs = stmt.executeQuery(sql);

                // 展开结果集数据库
                while(rs.next())
                {
                    // 通过字段检索
                    String movie_name = rs.getString("movie_name");
                    movieList.add(movie_name);

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
            return (String[]) movieList.toArray(new String[0]);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // TODO Auto-generated method stub

            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String[] result)
        {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(RecommendActivity.this,android.R.layout.simple_list_item_1,result);
            final ListView user_movie_list = (ListView) findViewById(R.id.ListView_Friends);
            user_movie_list.setAdapter(adapter);
        }
    }

    private class ShowNewFriendsRecommendTask extends AsyncTask<String, Integer, String[]>
    {

        String lastFriend = "";

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        protected String[] doInBackground(String... params) {
            ArrayList<String> movieList = new ArrayList<String>();
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
                sql = "select user_movie.user_id,user_movie.movie_name from user_movie\n" +
                        "\twhere user_movie.user_id \n" +
                        "\t\t\t\tin (select second from relation where first = \'" + params[0] + "\' and rank = 1) \n" +
                        "        and\n" +
                        "\t\t\tuser_movie.movie_name\n" +
                        "\t\t\t\tnot in (select movie_name from user_movie where user_id = \'" + params[0] + "\')\n" +
                        "\torder by user_id;";

                System.out.println("查询语句为："+ sql);
                ResultSet rs = stmt.executeQuery(sql);

                // 展开结果集数据库
                while(rs.next())
                {
                    // 通过字段检索
                    String friend_name = rs.getString("user_id");
                    String movie_name = rs.getString("movie_name");

                    String addStr;

                    if(!lastFriend.equals((friend_name))) {
                        addStr = "来自用户 " + friend_name +" 的推荐:";
                        movieList.add(addStr);
                        lastFriend = friend_name;
                    }
                    addStr = "    " + movie_name;
                    movieList.add(addStr);

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
            return (String[]) movieList.toArray(new String[0]);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // TODO Auto-generated method stub

            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String[] result)
        {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(RecommendActivity.this,android.R.layout.simple_list_item_1,result);
            final ListView user_movie_list = (ListView) findViewById(R.id.ListView_Friends);
            user_movie_list.setAdapter(adapter);
        }
    }

    private class ShowOldFriendsRecommendTask extends AsyncTask<String, Integer, String[]>
    {

        String lastFriend = "";

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        protected String[] doInBackground(String... params) {
            ArrayList<String> movieList = new ArrayList<String>();
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
                sql = "select user_movie.user_id,user_movie.movie_name from user_movie\n" +
                        "\twhere user_movie.user_id \n" +
                        "\t\t\t\tin (select second from relation where first = \'" + params[0] + "\' and rank = 2) \n" +
                        "        and\n" +
                        "\t\t\tuser_movie.movie_name\n" +
                        "\t\t\t\tnot in (select movie_name from user_movie where user_id = \'" + params[0] + "\')\n" +
                        "\torder by user_id;";

                System.out.println("查询语句为："+ sql);
                ResultSet rs = stmt.executeQuery(sql);

                // 展开结果集数据库
                while(rs.next())
                {
                    // 通过字段检索
                    String friend_name = rs.getString("user_id");
                    String movie_name = rs.getString("movie_name");

                    String addStr;

                    if(!lastFriend.equals((friend_name))) {
                        addStr = "来自朋友 " + friend_name +" 的推荐:";
                        movieList.add(addStr);
                        lastFriend = friend_name;
                    }
                    addStr = "    " + movie_name;
                    movieList.add(addStr);


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
            return (String[]) movieList.toArray(new String[0]);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // TODO Auto-generated method stub

            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String[] result)
        {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(RecommendActivity.this,android.R.layout.simple_list_item_1,result);
            final ListView user_movie_list = (ListView) findViewById(R.id.ListView_Friends);
            user_movie_list.setAdapter(adapter);
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_mark:
                    RecommendActivity.this.finish();
                    Intent intent_mark=new Intent(RecommendActivity.this,MarkActivity.class);
                    startActivity(intent_mark);
                    return true;
                case R.id.navigation_recommend:

                    return true;
                case R.id.navigation_friend:
                    RecommendActivity.this.finish();
                    Intent intent_friend=new Intent(RecommendActivity.this,FriendActivity.class);
                    startActivity(intent_friend);
                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_recommend);

        btn_anonymous = (Button) findViewById(R.id.rcmd_anonymous_btn);
        btn_new_friend = (Button) findViewById(R.id.rcmd_new_friend_btn);
        btn_old_friend = (Button) findViewById(R.id.rcmd_old_friend_btn);

        btn_anonymous.setBackgroundColor(COLOR_TAB_SELECTED);
        btn_new_friend.setBackgroundColor(COLOR_TAB_UNSELECTED);
        btn_old_friend.setBackgroundColor(COLOR_TAB_UNSELECTED);
        new ShowAnonymousRecommendTask().execute(AppData.user_id);
    }

    public void onClick_oldFriend(View view)
    {
        btn_anonymous.setBackgroundColor(COLOR_TAB_UNSELECTED);
        btn_new_friend.setBackgroundColor(COLOR_TAB_UNSELECTED);
        btn_old_friend.setBackgroundColor(COLOR_TAB_SELECTED);
        new ShowOldFriendsRecommendTask().execute(AppData.user_id);
    }

    public void onClick_newFriend(View view)
    {
        btn_anonymous.setBackgroundColor(COLOR_TAB_UNSELECTED);
        btn_new_friend.setBackgroundColor(COLOR_TAB_SELECTED);
        btn_old_friend.setBackgroundColor(COLOR_TAB_UNSELECTED);
        new ShowNewFriendsRecommendTask().execute(AppData.user_id);
    }

    public void onClick_anonymous(View view)
    {
        btn_anonymous.setBackgroundColor(COLOR_TAB_SELECTED);
        btn_new_friend.setBackgroundColor(COLOR_TAB_UNSELECTED);
        btn_old_friend.setBackgroundColor(COLOR_TAB_UNSELECTED);
        new ShowAnonymousRecommendTask().execute(AppData.user_id);
    }
}
