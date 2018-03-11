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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ShowMessage extends AppCompatActivity {

    private String theUserId;
    private String theUserMsg;
    private String nowId;
    private int number = 0;

    private class ShowMessageRequestTask extends AsyncTask<String, Integer, String>
    {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String result = "failed!";
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
                sql = "DELETE FROM msg WHERE sender = \'" + params[1] + "\' and msg = \'" + params[2] + "\' and receiver = \'" + params[0] + "\'";
                System.out.println("删除语句为："+ sql);
                int rs = stmt.executeUpdate(sql);

                // 展开结果集数据库
               if(rs != -1)
                {
                    System.out.println("删除成功");
                    result = "删除成功";
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
            AlertDialog.Builder builder = new AlertDialog.Builder(ShowMessage.this);
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
        setContentView(R.layout.activity_show_message);

        Intent intent = getIntent();
        theUserId = intent.getStringExtra("id");
        theUserMsg = intent.getStringExtra("msg");
        nowId = intent.getStringExtra("thisid");
        TextView introduction =  (TextView) findViewById(R.id.textView_FriendId);
        introduction.setText(theUserId);
        TextView msg =  (TextView) findViewById(R.id.textView_Message);
        msg.setText(theUserMsg);
        //new ShowMessageRequestTask().execute(theUserId,theUserMsg);
    }

    public void onClick_btn_delete(View view)
    {
        new ShowMessageRequestTask().execute(AppData.user_id,theUserId,theUserMsg,nowId);
    }

    public void onClick_btn_return(View view)
    {
        ShowMessage.this.finish();
    }
}
