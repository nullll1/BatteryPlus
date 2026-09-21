package in.sunilpaulmathew.batteryplus.dialogs;

import android.app.Activity;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;

import in.sunilpaulmathew.batteryplus.R;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on Sept. 07, 2026
 */
public class FailureDialog extends MaterialAlertDialogBuilder {

    private AlertDialog alertDialog;

    public FailureDialog(String statusText, Activity activity) {
        super(activity);

        if (activity.isFinishing() || activity.isDestroyed()) {
            return;
        }

        View rootView = View.inflate(activity, R.layout.layout_dialog_failure, null);

        MaterialButton exit = rootView.findViewById(R.id.exit);
        MaterialTextView text = rootView.findViewById(R.id.text);

        text.setText(statusText);

        setView(rootView);
        setCancelable(false);

        alertDialog = create();

        alertDialog.setOnDismissListener(dialog -> alertDialog.dismiss());

        exit.setOnClickListener(v -> {
            alertDialog.dismiss();
            if (!activity.isFinishing() && !activity.isDestroyed()) {
                activity.finish();
            }
        });

        alertDialog.show();
    }

}