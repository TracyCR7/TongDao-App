package lmq.tongdao;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SendAddFriendMsgActivity extends AppCompatActivity {

    private String theUserId;

    private class SendNewFriendRequestTask extends AsyncTask<String, Integer, String>
    {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String result="发送失败！";
            int type = 1;
            String defaultRequest = "和我签订契约，成为魔法少女吧！";
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
                sql = "INSERT INTO msg (sender, receiver, type, msg) VALUES ( \'" + params[0] + "\',\'" + params[1] + "\'," +  type + ",\'" + defaultRequest + "\')"  ;
                System.out.println("插入语句为："+ sql);
                int rs = stmt.executeUpdate(sql);
                if (rs != -1) // 插入成功
                {
                    System.out.println("插入成功");
                    result = "发送成功！";
                }

                // 完成后关闭
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
            return result;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // TODO Auto-generated method stub
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(SendAddFriendMsgActivity.this);
            builder.setMessage(result);
            builder.setTitle("提示");
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_add_friend_msg);

        Intent intent = getIntent();
        theUserId = intent.getStringExtra("id");
        TextView introduction =  (TextView) findViewById(R.id.safm_textView_FriendId);
        introduction.setText(theUserId);

    }
    public void onClick_btn_addFriend(View view)
    {
        new SendNewFriendRequestTask().execute(AppData.user_id,theUserId);
    }
    public void onClick_btn_return(View view)
    {
        SendAddFriendMsgActivity.this.finish();
    }
}
