package asm.uabierta.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import asm.uabierta.R;
import asm.uabierta.models.Country;
import asm.uabierta.fragments.dialogs.ChangeEmailDialog;
import asm.uabierta.fragments.dialogs.ChangeNameDialog;
import asm.uabierta.fragments.dialogs.ChangePasswordDialog;
import asm.uabierta.fragments.dialogs.ChangePhoneDialog;
import asm.uabierta.utils.UserPreferences;

public class SettingsActivity extends AppCompatActivity implements ChangeNameDialog.ChangeNameDialogListener,
        ChangeEmailDialog.ChangeEmailDialogListener, ChangePhoneDialog.ChangePhoneDialogListener,
        ChangePasswordDialog.ChangePasswordDialogListener{

    private UserPreferences uP;
    private TextView tvEmail, tvName, tvPhone;
    private Button btnPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        uP = new UserPreferences(getApplicationContext());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tvEmail = (TextView) findViewById(R.id.tvEmail);
        tvName = (TextView) findViewById(R.id.tvName);
        tvPhone = (TextView) findViewById(R.id.tvPhone);

        btnPass = (Button) findViewById(R.id.btnChangePass);

        tvName.setText(uP.getName());
        tvEmail.setText(uP.getEmail());
        tvPhone.setText(uP.getPrefixName()+" "+uP.getPrefix()+"   "+uP.getPhone());

        tvName.setOnClickListener(new ImageButton.OnClickListener() {
            public void onClick(View v) {
                new ChangeNameDialog().show(getSupportFragmentManager(), null);
            }
        });

        tvEmail.setOnClickListener(new ImageButton.OnClickListener() {
            public void onClick(View v) {
                new ChangeEmailDialog().show(getSupportFragmentManager(), null);
            }
        });

        tvPhone.setOnClickListener(new ImageButton.OnClickListener() {
            public void onClick(View v) {
                new ChangePhoneDialog().show(getSupportFragmentManager(), null);
            }
        });

        btnPass.setOnClickListener(new ImageButton.OnClickListener() {
            public void onClick(View v) {
                new ChangePasswordDialog().show(getSupportFragmentManager(), null);
            }
        });
    }

    @Override
    public void onFinishNameDialog(String name) {
        Toast.makeText(getApplicationContext(), R.string.name_changed, Toast.LENGTH_LONG).show();
        uP.setName(name);
        tvName.setText(name);
    }

    @Override
    public void onFinishEmailDialog(String email) {
        Toast.makeText(getApplicationContext(), R.string.email_changed, Toast.LENGTH_LONG).show();
        uP.setName(email);
        tvEmail.setText(email);
    }

    @Override
    public void onFinishPhoneDialog(String phone, Country prefix) {
        Toast.makeText(getApplicationContext(), R.string.phone_changed, Toast.LENGTH_LONG).show();
        if(prefix!=null){
            uP.setPrefixId(prefix.getId());
            uP.setPrefix("+"+prefix.getPrefix());
            uP.setPrefixName(prefix.getName());
        }
        else{
            uP.setPrefixId(0);
            uP.setPrefix("prefix");
            uP.setPrefixName("prefixName");
        }
        uP.setPhone(phone);
        tvPhone.setText(uP.getPrefixName()+" "+uP.getPrefix()+"   "+uP.getPhone());
    }

    @Override
    public void onFinishPasswordDialog(String current, String newPass) {
        Toast.makeText(getApplicationContext(), R.string.password_changed, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
