package asm.uabierta.utils;

import android.content.Context;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by alex on 16/11/15.
 */
public class AnalyticsTracker {

    public static GoogleAnalytics analytics;
    public static Tracker tracker;

    public AnalyticsTracker(Context context) {
        analytics = GoogleAnalytics.getInstance(context);
        analytics.setLocalDispatchPeriod(1800);

        tracker = analytics.newTracker("");
        tracker.enableExceptionReporting(true);
        tracker.enableAdvertisingIdCollection(true);
        tracker.enableAutoActivityTracking(true);
    }
}
