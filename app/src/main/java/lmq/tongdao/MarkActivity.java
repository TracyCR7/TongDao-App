package lmq.tongdao;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MarkActivity extends AppCompatActivity {

    private MarkMovieAdapter mAdapter = null;

    private class SearchMoviesTask extends AsyncTask<String, Integer, List<MarkMovieUnit>>
    {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        protected List<MarkMovieUnit> doInBackground(String... params) {
            List<MarkMovieUnit> movieList = new ArrayList<MarkMovieUnit>();
            ArrayList<String> favourMovies = new ArrayList<String>();
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

                String sq0;
                sq0 = "SELECT movie_name FROM user_movie WHERE user_id = \'" + AppData.user_id + "\'";
                System.out.println("查询语句为："+ sq0);
                ResultSet rs0 = stmt.executeQuery(sq0);

                // 展开结果集数据库
                while(rs0.next())
                {
                    // 通过字段检索
                    String movie_name = rs0.getString("movie_name");
                    favourMovies.add(movie_name);
                }

                String sql1;
                sql1 = "SELECT movie_name FROM movie WHERE movie_name like \'%" + params[0] + "%\'";
                System.out.println("查询语句为："+ sql1);
                ResultSet rs1 = stmt.executeQuery(sql1);

                // 展开结果集数据库
                while(rs1.next())
                {
                    // 通过字段检索
                    String movie_name = rs1.getString("movie_name");
                    int j = 0;
                    for(j=0;j<favourMovies.size();j++) {
                        if(favourMovies.get(j).equals(movie_name)) {
                            break;
                        }
                    }
                    MarkMovieUnit mmu = new MarkMovieUnit(movie_name,j<favourMovies.size());
                    movieList.add(mmu);
                }
                // 完成后关闭
                rs1.close();
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
            return movieList;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // TODO Auto-generated method stub

            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(List<MarkMovieUnit> result)
        {


            mAdapter = new MarkMovieAdapter(MarkActivity.this,R.layout.mark_listview_item,result);
            final ListView user_movie_list = (ListView) findViewById(R.id.mark_ListView_Movies);
            user_movie_list.setAdapter(mAdapter);

            user_movie_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    CheckBox cb = (CheckBox) view.findViewById(R.id.mark_listview_item_checkbox);

                    if(cb!=null) {
                        cb.setChecked(!cb.isChecked());
                        mAdapter.getItem(i).setMarked(cb.isChecked());
                        mAdapter.notifyDataSetChanged();
                    }


                }
            });

        }
    }

    private class UserMovieTask extends AsyncTask<String, Integer, Boolean>
    {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            boolean match = false;
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

                for(int i=0;i<mAdapter.getCount();i++) {
                    MarkMovieUnit mmu = mAdapter.getItem(i);
                    if(!mmu.isChanged()) {
                        continue;
                    }
                    if(mmu.getMarked()) {
                        //需要插入
                        String sql;
                        sql = "INSERT INTO user_movie VALUES ( \'" + AppData.user_id + "\',\'" + mmu.getMovieName() + "\')"  ;
                        System.out.println(sql);
                        int rs = stmt.executeUpdate(sql);
                        if (rs != -1) // 插入成功
                        {
                            System.out.println("插入成功");
                            match = true;
                        }

                    } else {
                        //需要删除
                        String sql;
                        sql = "DELETE FROM user_movie  WHERE user_id = \'" + AppData.user_id + "\' and movie_name = \'" + mmu.getMovieName() + "\'" ;
                        System.out.println(sql);
                        int rs = stmt.executeUpdate(sql);
                        if (rs != -1) // 插入成功
                        {
                            System.out.println("删除成功");
                            match = true;
                        }

                    }

                }


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
            return  match;
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            // TODO Auto-generated method stub
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Boolean result) {

        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_mark:
                    return true;
                case R.id.navigation_recommend:
                    MarkActivity.this.finish();
                    Intent intent_recommend=new Intent(MarkActivity.this,RecommendActivity.class);
                    startActivity(intent_recommend);
                    return true;
                case R.id.navigation_friend:
                    MarkActivity.this.finish();
                    Intent intent_friend=new Intent(MarkActivity.this,FriendActivity.class);
                    startActivity(intent_friend);
                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_mark);

        final EditText m_search_text = (EditText) findViewById(R.id.mark_search_text);

        searchMovie();


        m_search_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        searchMovie();
                    }
                    return true;
                }
            });

    }

    private void searchMovie() {
        final EditText m_search_text = (EditText) findViewById(R.id.mark_search_text);
        String searchStr = m_search_text.getText().toString();
        new SearchMoviesTask().execute(searchStr);
    }

    public void onClick_search(View view)
    {
        searchMovie();
    }

    public void onClick_select(View view) {
        new UserMovieTask().execute();
    }

}