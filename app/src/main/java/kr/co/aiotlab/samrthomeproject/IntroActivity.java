package kr.co.aiotlab.samrthomeproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class IntroActivity extends AppCompatActivity {

    private ImageView img1, img2, img3, img4, light;
    private TextView txt1, txt2;
    private Animation from_top, from_bottom, from_left, from_right, from_bottom_100, from_bottom_200, from_bottom_300;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        from_top = AnimationUtils.loadAnimation(this, R.anim.from_top);
        from_bottom = AnimationUtils.loadAnimation(this, R.anim.from_bottom);
        from_left = AnimationUtils.loadAnimation(this, R.anim.from_left);
        from_right = AnimationUtils.loadAnimation(this, R.anim.from_right);
        from_bottom_100 = AnimationUtils.loadAnimation(this, R.anim.from_bottom_delay100);
        from_bottom_200 = AnimationUtils.loadAnimation(this, R.anim.from_bottom_delay200);
        from_bottom_300 = AnimationUtils.loadAnimation(this, R.anim.from_bottom_delay300);

        img1 = findViewById(R.id.img_intro_1);
        img2 = findViewById(R.id.img_intro_2);
        img3 = findViewById(R.id.img_intro_3);
        img4 = findViewById(R.id.img_intro_4);

        txt1 = findViewById(R.id.txt_intro_1);
        txt2 = findViewById(R.id.txt_intro_2);

        txt1.setAnimation(from_right);
        img1.setAnimation(from_bottom);
        img2.setAnimation(from_bottom_100);
        txt2.setAnimation(from_left);
        img3.setAnimation(from_bottom_200);
        img4.setAnimation(from_bottom_300);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 900);
    }
}
