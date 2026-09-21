package in.sunilpaulmathew.batteryplus.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import in.sunilpaulmathew.batteryplus.R;
import in.sunilpaulmathew.batteryplus.serializables.StatusEntry;
import in.sunilpaulmathew.batteryplus.utils.Utils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on Sept. 07, 2026
 */
public class PolicyAdapter extends RecyclerView.Adapter<PolicyAdapter.ViewHolder> {

    private final List<StatusEntry> data;

    public PolicyAdapter(List<StatusEntry> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_policy, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(data.get(position).getTitle());
        holder.text.setText(data.get(position).getDescription());
        holder.text.setTextColor(holder.text.getHintTextColors());

        Utils.setSlideInAnimation(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final MaterialTextView text, title;

        public ViewHolder(View view) {
            super(view);
            this.text = view.findViewById(R.id.text);
            this.title = view.findViewById(R.id.title);
            view.setOnClickListener(v -> {
                if (text.getMaxLines() == 1) {
                    text.setSingleLine(false);
                } else {
                    text.setMaxLines(1);
                }
            });
        }
    }

}