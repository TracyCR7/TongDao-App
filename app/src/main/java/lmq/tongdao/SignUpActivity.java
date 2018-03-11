package lmq.tongdao;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "ASYNC_TASK";

    private class SignUpTask extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            Log.d(TAG, "onPreExecute");
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
            try {
                // 注册 JDBC 驱动
                Class.forName(JDBC_DRIVER);

                // 打开链接
                System.out.println("连接数据库...");
                conn = DriverManager.getConnection(URL, USER, PASS);

                // 执行查询
                System.out.println(" 实例化Statement对...");
                stmt = conn.createStatement();
                String sql;
                sql = "INSERT INTO user VALUES ( \'" + params[0] + "\',\'" + params[1] + "\',\'" + params[2] + "\')";
                System.out.println(sql);
                int rs = stmt.executeUpdate(sql);
                if (rs != -1) // 插入成功
                {
                    System.out.println("插入成功");
                    match = true;
                }
                stmt.close();
                conn.close();
            } catch (SQLException se) {
                // 处理 JDBC 错误
                se.printStackTrace();
            } catch (Exception e) {
                // 处理 Class.forName 错误
                e.printStackTrace();
            } finally {
                /* 关闭资源 */
                try {
                    if (stmt != null) stmt.close();
                } catch (SQLException se2) {
                }// 什么都不做
                try {
                    if (conn != null) conn.close();
                } catch (SQLException se) {
                    se.printStackTrace();
                }
            }
            return match;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // TODO Auto-generated method stub
            Log.d(TAG, "onProgressUpdate values[0]=" + values[0]);
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            final TextView text = (TextView) findViewById(R.id.sign_up_result);
            if (result) {
                text.setText("Success!");
                SignUpActivity.this.finish();
            } else {
                text.setText("Failed!");
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
    }

    public void onClick_sign_up(View view)
    {
        final TextInputLayout idText = (TextInputLayout) findViewById(R.id.sign_up_user_id);
        final TextInputLayout passwordText = (TextInputLayout) findViewById(R.id.sign_up_password);
        final TextInputLayout introductionText = (TextInputLayout) findViewById(R.id.sign_up_introduction);
        String id = idText.getEditText().getText().toString();
        String password = passwordText.getEditText().getText().toString();
        String introduction = introductionText.getEditText().getText().toString();
        new SignUpTask().execute(id,password,introduction);
    }

    public void onClick_cancle(View view)
    {
        SignUpActivity.this.finish();
    }

}
