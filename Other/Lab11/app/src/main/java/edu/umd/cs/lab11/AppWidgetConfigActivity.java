package edu.umd.cs.lab11;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RemoteViews;

/**
 * Created by Jaemoon on 4/27/17.
 */

public class AppWidgetConfigActivity extends Activity {
    private int mAppWidgetId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_widget_config);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        findViewById(R.id.title_button).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                String title = ((EditText)findViewById(R.id.title_edit_text))
                        .getText().toString();
                String version = getResources().getString(R.string.version_name);
                version += ": " + Build.VERSION.SDK_INT;

                Context context = AppWidgetConfigActivity.this;
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.my_appwidget_provider);
                views.setTextViewText(R.id.title_text_view, title);
                views.setTextViewText(R.id.version_text_view, version);
                appWidgetManager.updateAppWidget(mAppWidgetId, views);

                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                setResult(RESULT_OK, resultValue);
                finish();
            }
        });

    }
}
