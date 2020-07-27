package asm.uabierta.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import asm.uabierta.R;

/**
 * Created by alex on 3/11/15.
 */
public class UtilsFunctions {

    public static String getImagesFolder(){
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)+ Constants.appFolder;
    }

    public static void createFolders(){
        File imagesFolder = new File(getImagesFolder());
        if(!imagesFolder.exists())
            imagesFolder.mkdirs();
    }

    public static void sendMail(Context context, String email, String title){
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", email, null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name)+": "+title);
        emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(emailIntent, "Enviar email..."));
    }

    public static void callPhone( Context context, String prefix, String phone){
        String call;
        if(prefix!=null)
            call = prefix+phone;
        else
            call = phone;

        Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + call));
        phoneIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(phoneIntent);
    }

    public static Bitmap resizePhoto(String path){
        Bitmap resized = BitmapFactory.decodeFile(path);
        double maxWidth = 600;
        double maxHeight = 600;
        double width = resized.getWidth();
        double height = resized.getHeight();
        double rateX = maxWidth / width;
        double rateY = maxHeight / height;
        int finalWidth;
        int finalHeight;

        if((width <= maxWidth) && (height <= maxHeight)){
            finalWidth = (int)width;
            finalHeight = (int)height;
        }
        else if ((rateX * height) < maxHeight){
            finalHeight = (int)Math.ceil(rateX * height);
            finalWidth = (int)maxWidth;
        }
        else{
            finalWidth = (int)Math.ceil(rateY * width);
            finalHeight = (int)maxHeight;
        }
        resized = Bitmap.createScaledBitmap(resized, finalWidth, finalHeight, true);
        return resized;
    }

    public static boolean isLollipop(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean isUsernameValid(String n) {
        return n.matches("[A-Za-z0-9_]{3,50}");
    }

    public static boolean isPhoneValid(String p) {
        return p.matches("^\\d{1,25}$");
    }

    public static boolean isNameValid(String n) {
        return n.matches("[A-Za-z0-9 ]{1,50}");
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        ///^[\w-\.]+@([\w-]+\.)+[\w]{2,4}$/   JAVASCRIPT
        //"/^[^0-9][a-zA-Z0-9_]+([.][a-zA-Z0-9_]+)*[@][a-zA-Z0-9_]+([.][a-zA-Z0-9_]+)*[.][a-zA-Z]{2,4}$/"   PHP
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static String parseTime(int sTime) {
        int hor,min,seg;
        String ret = "";
        hor=sTime/3600;
        min=(sTime-(3600*hor))/60;
        seg=sTime-((hor*3600)+(min*60));
        if (hor > 0)
            ret += hor + "h ";
        if (min > 0)
            ret += min + "min ";
        if (seg > 0)
            ret += seg + "s";
        return ret;
    }

    public static String parseDistance(double distance) {
        int km, m;
        String ret = "";
        km= (int) (distance/1000);
        m= (int) (distance-(1000*km));
        if (km > 0)
            ret += km + "km ";
        if (m > 0)
            ret += m + "m";
        return ret;
    }

    public static String formatDate(String d){
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(d);
            //date = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new SimpleDateFormat("dd/MM/yyyy").format(date);
        //return new SimpleDateFormat("dd/MM/yyyy HH:mm").format(date);
    }

    public static Bitmap scaleDownBitmapWithHeight(Bitmap photo, int newHeight, Context context) {
        final float densityMultiplier = context.getResources().getDisplayMetrics().density;
        int h= (int) (newHeight*densityMultiplier);
        int w= (int) (h * photo.getWidth()/((double) photo.getHeight()));
        photo=Bitmap.createScaledBitmap(photo, w, h, true);
        return photo;
    }

    public static Bitmap scaleDownBitmapWithWidth(Bitmap photo, int newWidth, Context context) {
        final float densityMultiplier = context.getResources().getDisplayMetrics().density;
        int w= (int) (newWidth*densityMultiplier);
        int h= (int) (w * photo.getHeight()/((double) photo.getWidth()));
        photo=Bitmap.createScaledBitmap(photo, w, h, true);
        return photo;
    }
}