package lmq.tongdao;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SignInActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    /*static {
        System.loadLibrary("native-lib");
    }*/
    private static final String TAG = "ASYNC_TASK";

    private class LogInTask extends AsyncTask<String, Integer, Boolean>
    {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            //Log.d(TAG,"onPreExecute");
            final TextView resultText = (TextView) findViewById(R.id.sign_in_result);
            resultText.setText("登陆中...");
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {

            System.out.println("doInBackground.");
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
                // System.out.println("注册 JDBC 驱动");
                Class.forName(JDBC_DRIVER);

                // 打开链接
                // System.out.println("连接数据库...");
                conn = DriverManager.getConnection(URL,USER,PASS);

                // 执行查询
                // System.out.println(" 实例化Statement对...");
                stmt = conn.createStatement();
                String sql;
                sql = "SELECT password FROM user WHERE id = \'" + params[0] + "\'" ;
                // System.out.println(sql);
                ResultSet rs = stmt.executeQuery(sql);

                // 展开结果集数据库
                while(rs.next()){
                    // 通过字段检索
                    String password_db = rs.getString("password");
                    System.out.println(password_db);
                    if(params[1].equals(password_db)) {
                        match = true;
                        AppData.user_id = params[0];
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
            return match;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // TODO Auto-generated method stub
            // Log.d(TAG,"onProgressUpdate values[0]="+ values[0]);
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            final TextView resultText = (TextView) findViewById(R.id.sign_in_result);
            if(result)
            {
                resultText.setText("登陆成功！");
                SignInActivity.this.finish();
                Intent intent=new Intent(SignInActivity.this,MarkActivity.class);
                startActivity(intent);
            }
            else
            {
                resultText.setText("登陆失败！");
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        final EditText passwordText = (EditText) findViewById(R.id.sign_in_password_edittext);
        passwordText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    login();
                }
                return true;
            }
        });

    }

    private void login() {
        final TextInputLayout idText = (TextInputLayout) findViewById(R.id.sign_in_user_id);
        final TextInputLayout passwordText = (TextInputLayout) findViewById(R.id.sign_in_password);
        String id = idText.getEditText().getText().toString();
        String password = passwordText.getEditText().getText().toString();
        new LogInTask().execute(id,password);
    }

    public void onClick_sign_in(View view)
    {
        login();
    }

    public void onClick_sign_up(View view)
    {
        Intent intent=new Intent(this,SignUpActivity.class);
        startActivity(intent);
    }
}
