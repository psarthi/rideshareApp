package com.parift.rideshare.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.parift.rideshare.R;
import com.google.android.gms.common.SignInButton;
import com.parift.rideshare.config.APIUrl;

public class LandingPageActivity extends BaseActivity{

    private static final String TAG = LandingPageActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);
        getSupportActionBar().hide();

        SignInButton googleSignInButton = findViewById(R.id.google_sign_in_button);

        //Change the text of google sign in button
        for (int i=0;i<googleSignInButton.getChildCount();i++){
            View view = googleSignInButton.getChildAt(i);
            if (view instanceof TextView){
                ((TextView) view).setText(R.string.google_sign_in);
                view.setPadding(24, 0, 0, 0);
            }
        }

        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignIn();
            }
        });

        setAgreementText();

    }

    private void setAgreementText() {
        SpannableString ss = new SpannableString(getResources().getString(R.string.agreement_text));
        ClickableSpan clickableSpanTerms = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(APIUrl.TERMS_OF_SERVICE_URL));
                startActivity(i);
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
                ds.setColor(getResources().getColor(android.R.color.white));
            }
        };
        ss.setSpan(clickableSpanTerms, 32, 48, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        ClickableSpan clickableSpanPrivacy = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(APIUrl.PRIVACY_POLICY_URL));
                startActivity(i);
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
                ds.setColor(getResources().getColor(android.R.color.white));
            }
        };
        ss.setSpan(clickableSpanPrivacy, 53, 67, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        TextView textView = findViewById(R.id.agreement_text);
        textView.setText(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setHighlightColor(Color.TRANSPARENT);
    }
}
