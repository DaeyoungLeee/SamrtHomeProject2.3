package kr.co.aiotlab.samrthomeproject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView txt_networkState, txt_btnState;
    public static WebView weBView;
    private ActionBarDrawerToggle mToggle;
    public static String ip_address;
    private String text1, txt = null;
    private DrawerLayout drawer;
    private long backBtnTime = 0;
    private SwipeRefreshLayout swipeMain;
    private FloatingActionButton btn_button;
    ProgressDialog dialog;

    Animation anim_from_top;

    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DrawerLayout mDrawerLayout = findViewById(R.id.drawer);
        NavigationView navigationView = findViewById(R.id.navigationView);
        weBView = findViewById(R.id.webView);
        btn_button = findViewById(R.id.btn_button);
        txt_networkState = findViewById(R.id.txt_networkState);
        swipeMain = findViewById(R.id.swipeMain);
        drawer = findViewById(R.id.drawer);
        txt_btnState = findViewById(R.id.txt_buttonState);
        anim_from_top = AnimationUtils.loadAnimation(this, R.anim.from_top);

        // 지속적으로 현재상태 업데이트
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    //값 받아오기
                    txt = getNowState();
                    Message msg = handler.obtainMessage();
                    handler.sendMessage(msg);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        swipeMain.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread() {
                    public void run() {
                        Log.d(TAG, "run: " + txt);
                        txt = getNowState();

                        Message msg = handler.obtainMessage();
                        handler.sendMessage(msg);

                    }
                }.start();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeMain.setRefreshing(false);
                    }
                }, 500);    //새로고침 바 보이는 시간 설정
            }
        });

        // 드로어 열고 닫는 토글 스위치
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        //

        navigationView.setNavigationItemSelectedListener(this);

        // 이미지 버튼 클릭 동작
        btn_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                animationFab(btn_button);

                weBView.loadUrl("http://" + ip_address + "/room_light");
                weBView.getSettings().setJavaScriptEnabled(true);
                weBView.setWebViewClient(new WebViewClient());

                // UI를 변경하기 위한 코딩
                new Thread() {
                    public void run() {
                        txt = getNowState();

                        Message msg = handler.obtainMessage();
                        handler.sendMessage(msg);

                    }
                }.start();

            }
        });

    }

    // 버튼 클릭 애니메이션
    private void animationFab(final FloatingActionButton fab) {
        fab.animate().scaleX(0.7f).scaleY(0.7f).setDuration(100).withEndAction(new Runnable() {
            @Override
            public void run() {
                fab.animate().scaleX(1f).scaleY(1f);
            }
        });
    }

    //핸들러1

    @SuppressLint("HandlerLeak")
    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            txt_networkState.setText(txt);

            String switch_state = txt_networkState.getText().toString();

            switch (switch_state) {
                case "{'rl':'0','ml':'0','bl':'0','fan':'0'}":
                    txt_networkState.setText("연결됨");
                    txt_networkState.setTextColor(Color.WHITE);
                    drawer.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.mainbg));
                    txt_btnState.setText("스위치가 켜져있어요!");
                    txt_btnState.setTextColor(Color.rgb(10, 91, 254));

                    break;
                case "{'rl':'1','ml':'0','bl':'0','fan':'0'}":
                    txt_networkState.setText("연결됨");
                    txt_networkState.setTextColor(Color.BLUE);
                    drawer.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.mainbg_light_off));
                    txt_btnState.setText("스위치가 꺼져있어요!");
                    txt_btnState.setTextColor(Color.rgb(19, 19, 19));

                    break;
                default:
                    txt_networkState.setText("연결을 확인해주세요");
                    txt_networkState.setTextColor(Color.rgb(251, 0, 64));
                    break;
            }
        }
    };

//핸들러2

    @SuppressLint("HandlerLeak")
    final Handler handler2 = new Handler() {
        public void handleMessage(Message msg) {
            showProgressDialog();
        }
    };
//핸들러3

    @SuppressLint("HandlerLeak")
    final Handler handler3 = new Handler() {
        public void handleMessage(Message msg) {
            dialog.cancel();
        }
    };

    // 현재 상태 읽어오는 함수
    private String getNowState() {
        try {
            Document response = Jsoup.connect("http://" + ip_address + "/status").get();
            Connection.Response response1 = Jsoup.connect("http://" + ip_address + "/status").method(Connection.Method.GET).execute();
            Thread.sleep(200);
            Document document = response1.parse();
            text1 = document.text();
            String text = response.text();

//            Log.d("MainActivity", "getNowState : " + text);
//            Log.d("MainActivity", "getNowState1 : " + text1);


        } catch (IOException ignored) {

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return text1;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.set_IP_address:
                Intent intent = new Intent(this, SetIP.class);
                startActivity(intent);
                break;
            case R.id.set_wakeup:
                Intent intent_wakeup = new Intent(this, SetWakeUp.class);
                startActivity(intent_wakeup);
                break;
            case R.id.set_sleeptime:
                Intent intent_sleep = new Intent(this, SetSleepTime.class);
                startActivity(intent_sleep);
                break;
            case R.id.myCCTV:
                Intent intent_cctv = new Intent(this, CCTV_Activity.class);
                startActivity(intent_cctv);
                break;
            case R.id.myinfo:
                MyInfoDialogFragment myInfoDialogFragment = new MyInfoDialogFragment();
                myInfoDialogFragment.show(getSupportFragmentManager(), "개발자 정보");
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //입력된 ip주소 불러오기
        SharedPreferences ip = getSharedPreferences("IP_ADDRESS", MODE_PRIVATE);
        ip_address = ip.getString("IP", "0");

        //현재상태 url로 불러오는 동작
        new Thread() {
            @SuppressLint("SetTextI18n")
            public void run() {
                //로딩
                Message msg2 = handler2.obtainMessage();
                handler2.sendMessage(msg2);
                //값 받아오기
                txt = getNowState();
                //값에 따른 세팅
                Message msg = handler.obtainMessage();
                handler.sendMessage(msg);
                //세팅 되면 로딩 끝
                Message msg3 = handler3.obtainMessage();
                handler3.sendMessage(msg3);
            }
        }.start();
    }


    @Override
    public void onBackPressed() {

        long curTime = System.currentTimeMillis();
        long gapTime = curTime - backBtnTime;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
        } else {
            if (gapTime >= 0 && gapTime <= 2000) {
                super.onBackPressed();
            } else {
                backBtnTime = curTime;
                Toast.makeText(this, "한 번 더 누르면 종료됩니다~", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showProgressDialog() {
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("로딩중...");
        dialog.show();
    }

}
