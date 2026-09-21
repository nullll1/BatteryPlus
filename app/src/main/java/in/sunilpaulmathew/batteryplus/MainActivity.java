package in.sunilpaulmathew.batteryplus;

import static android.view.View.GONE;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import in.sunilpaulmathew.batteryplus.activities.BaseActivity;
import in.sunilpaulmathew.batteryplus.activities.SettingsActivity;
import in.sunilpaulmathew.batteryplus.adapters.StatusAdapter;
import in.sunilpaulmathew.batteryplus.dialogs.FailureDialog;
import in.sunilpaulmathew.batteryplus.serializables.StatusEntry;
import in.sunilpaulmathew.batteryplus.services.BatteryMonitorService;
import in.sunilpaulmathew.batteryplus.utils.Battery;
import in.sunilpaulmathew.batteryplus.utils.Utils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on Sept. 07, 2026
 */
public class MainActivity extends BaseActivity {

    private AppCompatImageButton icon, statusIcon;
    private boolean exitApp;
    private CircularProgressIndicator progress;
    private final StatusAdapter adapter = new StatusAdapter(new CopyOnWriteArrayList<>());
    private MaterialCheckBox startOnBoot, showNotification;
    private MaterialTextView progressText;

    private final BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                List<StatusEntry> stats = Battery.getData(intent, context);
                updateData(stats);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main, R.id.layout_main);

        icon = findViewById(R.id.icon);
        statusIcon = findViewById(R.id.status_icon);
        progress = findViewById(R.id.progress);
        LinearLayoutCompat layoutMain = findViewById(R.id.layout_main);
        MaterialButton settings = findViewById(R.id.settings);
        startOnBoot = findViewById(R.id.start_on_boot);
        showNotification = findViewById(R.id.show_notification);
        progressText = findViewById(R.id.progress_text);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        View containerTop = findViewById(R.id.container_top);

        ViewCompat.setOnApplyWindowInsetsListener(layoutMain, (view, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            view.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    systemBars.bottom
            );

            return insets;
        });

        boolean isLandscape = Utils.isLandscape(this);

        if (isLandscape) {
            layoutMain.setOrientation(LinearLayoutCompat.HORIZONTAL);
            FrameLayout.LayoutParams mainParams = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
            );
            mainParams.gravity = Gravity.CENTER;
            layoutMain.setLayoutParams(mainParams);
            LinearLayoutCompat.LayoutParams topParams;
            if (Utils.isTablet(this)) {
                topParams = new LinearLayoutCompat.LayoutParams(0, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 0.75f);
                recyclerView.setLayoutParams(new LinearLayoutCompat.LayoutParams(0, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 1.25f));
            } else {
                topParams = new LinearLayoutCompat.LayoutParams(0, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 1f);
                recyclerView.setLayoutParams(new LinearLayoutCompat.LayoutParams(0, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 1f));
            }
            containerTop.setLayoutParams(topParams);
        } else {
            layoutMain.setOrientation(LinearLayoutCompat.VERTICAL);
            FrameLayout.LayoutParams mainParams = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
            );
            mainParams.gravity = Gravity.TOP;
            layoutMain.setLayoutParams(mainParams);

            LinearLayoutCompat.LayoutParams topParams = new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
            containerTop.setLayoutParams(topParams);

            LinearLayoutCompat.LayoutParams recyclerParams = new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
            recyclerView.setLayoutParams(recyclerParams);
        }

        boolean notificationEnabled;
        if (Utils.hasNotificationGranted(this) && Utils.getBoolean("showNotification", false, this)) {
            startBatteryService();
            notificationEnabled = true;
        } else {
            notificationEnabled = false;
        }

        startOnBoot.setEnabled(notificationEnabled);
        startOnBoot.setChecked(Utils.getBoolean("startOnBoot", false, this));
        showNotification.setChecked(Utils.hasNotificationGranted(this) && Utils.getBoolean("showNotification", false, this));

        startOnBoot.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!buttonView.isPressed()) {
                return;
            }

            Utils.saveBoolean("startOnBoot", isChecked, this);
        });

        showNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!buttonView.isPressed()) {
                return;
            }

            if (isChecked) {
                if (Utils.hasNotificationGranted(this)) {
                    Utils.saveBoolean("showNotification", true, this);
                    startOnBoot.setEnabled(true);
                    startBatteryService();
                } else {
                    showNotification.setChecked(false);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        requestPermissions(new String[] {
                                Manifest.permission.POST_NOTIFICATIONS
                        }, 101);
                    }
                }
            } else {
                Utils.saveBoolean("showNotification", false, this);
                startOnBoot.setEnabled(false);
                stopBatteryService();
            }
        });

        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 2) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setAdapter(adapter);

        settings.setOnClickListener(v -> launchSettings.launch(new Intent(this, SettingsActivity.class)));

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (exitApp) {
                    exitApp = false;
                    finish();
                } else {
                    Utils.toast(getString(R.string.exit_confirmation_toast), MainActivity.this).show();
                    exitApp = true;
                    new Handler(Looper.getMainLooper()).postDelayed(() -> exitApp = false, 2000);
                }
            }
        });
    }

    private void startBatteryService() {
        Intent serviceIntent = new Intent(this, BatteryMonitorService.class);
        startForegroundService(serviceIntent);
    }

    private void stopBatteryService() {
        Intent serviceIntent = new Intent(this, BatteryMonitorService.class);
        stopService(serviceIntent);
    }

    private void updateData(List<StatusEntry> statusEntries) {
        if (statusEntries != null && !statusEntries.isEmpty()) {
            StatusEntry entry = statusEntries.get(0);
            int percentValue = entry.getValue();
            if (percentValue != -1) {
                String percentValueTxt = entry.getValue() + "%";
                progress.setProgress(entry.getValue(), true);
                icon.setImageBitmap(Battery.createBatteryIconBitmap(percentValue, this));
                statusIcon.setImageResource(entry.getIcon());
                progressText.setText(percentValueTxt);
                int indicatorColor = Battery.getProgressColor(percentValue, this);
                progress.setIndicatorColor(indicatorColor);
                progressText.setTextColor(indicatorColor);
                statusIcon.setColorFilter(indicatorColor);
            } else {
                shoeErrorDialog(entry.getDescription());
            }

            if (adapter != null) {
                adapter.updateData(statusEntries.subList(1, statusEntries.size()));
            }
        }
    }

    private final ActivityResultLauncher<Intent> launchSettings = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    boolean changed = Objects.requireNonNull(data).getBooleanExtra("STATUS_CHANGE", false);

                    if (changed) {
                        startOnBoot.setEnabled(Utils.hasNotificationGranted(this) && Utils.getBoolean("showNotification", false, this));
                        startOnBoot.setChecked(Utils.getBoolean("startOnBoot", false, this));
                        showNotification.setChecked(Utils.hasNotificationGranted(this) && Utils.getBoolean("showNotification", false, this));
                    }
                }
            }
    );

    private void shoeErrorDialog(String statusText) {
        startOnBoot.setVisibility(GONE);
        showNotification.setVisibility(GONE);
        progress.setVisibility(GONE);
        progressText.setVisibility(GONE);
        statusIcon.setVisibility(GONE);
        new FailureDialog(statusText, this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Utils.saveBoolean("showNotification", true, this);
            showNotification.setChecked(true);
            startOnBoot.setEnabled(true);
            startBatteryService();
        } else {
            showNotification.setChecked(false);
            new MaterialAlertDialogBuilder(this)
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle(R.string.permission_denied_title)
                    .setMessage(R.string.permission_denied_notification)
                    .setPositiveButton(R.string.cancel, (dialog, id) -> {
                    }).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            registerReceiver(batteryReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(batteryReceiver, filter);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(batteryReceiver);
    }

}