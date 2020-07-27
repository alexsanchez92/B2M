package asm.uabierta.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import asm.uabierta.R;
import asm.uabierta.activities.SignUpActivity;

/**
 * Created by alex on 22/07/15.
 */
public class TermsB2M extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.terms);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    public void back(View v) {
        switch (v.getId()) {
            case R.id.buttonAccept:
                SignUpActivity.acceptTerms();
                finish();
                break;
        }
    }
}