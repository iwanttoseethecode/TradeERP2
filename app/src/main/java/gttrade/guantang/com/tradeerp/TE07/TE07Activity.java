package gttrade.guantang.com.tradeerp.TE07;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import gttrade.guantang.com.tradeerp.R;
import gttrade.guantang.com.tradeerp.base.MyApplication;

public class TE07Activity extends AppCompatActivity {
    TextView phone, ver,copyrightTxtView;
    ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_te07);
        phone = (TextView) findViewById(R.id.phone);
        ver = (TextView) findViewById(R.id.ver);
        back = (ImageButton) findViewById(R.id.back);
        copyrightTxtView = (TextView) findViewById(R.id.copyrightTxtView);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
        copyrightTxtView.setText("copyright©"+simpleDateFormat.format(System.currentTimeMillis()));

        SpannableString num1 = new SpannableString("400-028-0130");
        // num1.setSpan(new StyleSpan(Typeface.BOLD), 0, 5,
        // Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        num1.setSpan(new URLSpan("tel:400-028-0130"), 0, 12,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // num1.setSpan(new UnderlineSpan(), 0, num1.length(),
        // Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        // num2.setSpan(new UnderlineSpan(), 0, num2.length(),
        // Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        phone.setText(num1);
        phone.setMovementMethod(LinkMovementMethod.getInstance());
        ver.setText(MyApplication.getInstance().getVisionName());

        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO 自动生成的方法存根
                finish();
            }
        });
    }
}
