package com.mo.http_get_post;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mo.http_get_post.tools.ServerTools;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
    private static final int SUCCESS = 0;
    private static final int FAILE = 1;
    private static final int NET_ERROR = 3;
    private static final String TAG = "MainActivity";
    EditText et_username;
    EditText et_password;
    TextView show_result;
    String username;
    String password;

    final String path = "http://188.188.7.85/Android_Server/Login";

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;

            switch (what) {
                case SUCCESS:
                    String data = (String) msg.obj;
                    show_result.setText(data);
                    break;
                case FAILE:
                    Toast.makeText(MainActivity.this, "连接服务器失败", Toast.LENGTH_SHORT).show();
                    break;
                case NET_ERROR:
                    Toast.makeText(MainActivity.this, "网络出现异常", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);
        show_result = (TextView) findViewById(R.id.show_result);

        username = et_username.getText().toString().trim();
        password = et_password.getText().toString().trim();
    }

    public void login(View view) {
        username = et_username.getText().toString().trim();
        password = et_password.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "用户名和密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        //使用传统get方式的请求服务器
//        new Thread_get().start();


        //使用传统的post方式请求服务器
        new Thread_post().start();

    }

    //传统的post方式请求服务器端
    class Thread_post extends Thread {
        @Override
        public void run() {
            try {
                URL url = new URL(path);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                //1.设置请求方式
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(5000); //设置连接的超时事件是5秒

                //2.组合数据,一定要将数据进行URL编码
                String commitData = "username="+URLEncoder.encode(username,"UTF-8")+"&password="+URLEncoder.encode(password,"UTF-8");

                // 3. 指定content-type -实际上就是指定传输的数据类型
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");


                //4.指定content-length Content-Length: 数据的长度
                conn.setRequestProperty("Content-Length", commitData.length() + "");

                //5.打开输出流，告诉服务器，我要写数据了
                conn.setDoOutput(true);


                //6.开始写数据
                OutputStream os = conn.getOutputStream();
                os.write(commitData.getBytes());
//                os.close();

                int code = conn.getResponseCode();  //获取返回的成功代码
                Log.i(TAG, "code:---" + code);

                if (code == 200) {
                    //表示连接服务器成功返回信息
                    String data = ServerTools.getInfo(conn.getInputStream());

                    Log.i(TAG, "data:---" + data);
                    //使用消息处理机制，将数据传递给主线程
                    Message ms = new Message();
                    ms.what = SUCCESS;
                    ms.obj = data;
                    handler.sendMessage(ms);
                } else {
                    //使用消息处理机制，将数据传递给主线程
                    Message ms = new Message();
                    ms.what = FAILE;
                    handler.sendMessage(ms);
                }

            } catch (Exception e) {

                //使用消息处理机制，将数据传递给主线程
                Message ms = new Message();
                ms.what = NET_ERROR;
                handler.sendMessage(ms);
                e.printStackTrace();
            }
        }
    }

    //传统的get方式请求服务器端
    class Thread_get extends Thread {
        @Override
        public void run() {
            try {
                String getPath = path +
                        "?username=" + URLEncoder.encode(username, "UTF-8") +
                        "&password=" + URLEncoder.encode(password, "UTF-8");
                URL url = new URL(getPath);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000); //设置连接的超时事件是5秒
                int code = conn.getResponseCode();  //获取返回的成功代码

                Log.i(TAG, "code:---" + code);
                ;

                if (code == 200) {
                    //表示连接服务器成功返回信息
                    String data = ServerTools.getInfo(conn.getInputStream());

                    Log.i(TAG, "data:---" + data);
                    //使用消息处理机制，将数据传递给主线程
                    Message ms = new Message();
                    ms.what = SUCCESS;
                    ms.obj = data;
                    handler.sendMessage(ms);
                } else {
                    //使用消息处理机制，将数据传递给主线程
                    Message ms = new Message();
                    ms.what = FAILE;
                    handler.sendMessage(ms);
                }

            } catch (Exception e) {

                //使用消息处理机制，将数据传递给主线程
                Message ms = new Message();
                ms.what = NET_ERROR;
                handler.sendMessage(ms);
                e.printStackTrace();
            }
        }
    }



}

