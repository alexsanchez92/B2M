package asm.uabierta.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.path.android.jobqueue.JobManager;

import asm.uabierta.activities.IndexActivity;
import asm.uabierta.jobs.LogoutJob;
import asm.uabierta.listeners.LogoutListener;
import asm.uabierta.responses.LogoutResponse;

/**
 * Created by alex on 30/10/15.
 */
public class UserPreferences{

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    public UserPreferences(Context context) {
        prefs = context.getSharedPreferences("userPreferences", Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public boolean isLogged(){
        return !(this.getId().equals("id") && !this.getId().equals(""));
    }

    public String getId(){
        return this.prefs.getString("id", "id");
    }
    public int getIntId(){
        try {
            return Integer.parseInt(this.prefs.getString("id", "id"));
        }catch (NumberFormatException e){
            return 0;
        }
    }
    public void setId(String id){
        this.editor.putString("id", id);
        this.editor.commit();
    }

    public String getName(){
        return this.prefs.getString("name", "name");
    }
    public void setName(String name){
        this.editor.putString("name", name);
        this.editor.commit();
    }

    public String getEmail(){
        return this.prefs.getString("email", "email");
    }
    public void setEmail(String email){
        this.editor.putString("email", email);
        this.editor.commit();
    }

    public int getPrefixId(){
        return Integer.parseInt(this.prefs.getString("prefixId", "0"));
    }
    public void setPrefixId(int prefixId){
        this.editor.putString("prefixId", Integer.toString(prefixId));
        this.editor.commit();
    }

    public String getPrefix(){
        return this.prefs.getString("prefix", "prefix");
    }
    public void setPrefix(String prefix){
        this.editor.putString("prefix", prefix);
        this.editor.commit();
    }

    public String getPrefixName(){
        return this.prefs.getString("prefixName", "prefixName");
    }
    public void setPrefixName(String prefixName){
        this.editor.putString("prefixName", prefixName);
        this.editor.commit();
    }

    public String getPhone(){
        return this.prefs.getString("phone", "phone");
    }
    public void setPhone(String phone){
        this.editor.putString("phone", phone);
        this.editor.commit();
    }

    public String getToken(){
        return this.prefs.getString("token", "token");
    }
    public void setToken(String token){
        this.editor.putString("token", token);
        this.editor.commit();
    }

    public void closeSession(){
        //editor.clear();
        //editor.commit();

        this.editor.putString("id", "id");
        this.editor.putString("name", "");
        this.editor.putString("email", "");
        this.editor.putString("phone", "");
        this.editor.putString("token", "");
        this.editor.putString("prefix", "");
        this.editor.putString("prefixId", "0");
        this.editor.putString("prefixName", "");
        this.editor.commit();
    }

    public void logUserPreferences(){
        Log.i("User Preferences", "logUP");
        Log.i("id: ", this.getId());
        Log.i("name: ", this.getName());
        Log.i("email: ", this.getEmail());
        Log.i("name: ", this.getName());
        Log.i("phone: ", this.getPhone());
        Log.i("token: ", this.getToken());
        Log.i("prefixId: ", this.getPrefixId()+"");
        Log.i("prefixName: ", this.getPrefixName());
        Log.i("prefix: ", this.getPrefix());
        //Log.i("hasPhoto: ", Integer.toString(this.getHasPhoto()));
    }

    public static void closeSession(final Context context, final Activity activity){
        FacebookSdk.sdkInitialize(context);

        AlertDialog.Builder alertbox = new AlertDialog.Builder(activity);
        //alertbox.setIcon(R.drawable.ic_dialog_alert);
        alertbox.setTitle("Cerrar sesion");
        //alertbox.setTitle(R.string.close_session);
        //alertbox.setMessage(R.string.close_session_mes);

        alertbox.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                JobManager jM = new JobManager(context);
                LogoutListener a = new LogoutListener() {
                    @Override
                    public void onLogoutFinish(final LogoutResponse logoutResponse) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(logoutResponse!=null) {
                                    if(logoutResponse.isSuccess() != 0) {
                                        Intent index = new Intent(context, IndexActivity.class);
                                        index.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        activity.startActivity(index);
                                        LoginManager.getInstance().logOut();
                                        UserPreferences uP = new UserPreferences(context);
                                        uP.closeSession();
                                        uP.logUserPreferences();
                                        activity.finish();
                                    }
                                }
                            }
                        });
                    }
                };
                jM.addJobInBackground(new LogoutJob(a, context, activity));
            }
        });

        alertbox.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
        alertbox.show();
    }
}
