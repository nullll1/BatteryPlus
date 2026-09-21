package in.sunilpaulmathew.batteryplus.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import in.sunilpaulmathew.batteryplus.R;
import in.sunilpaulmathew.batteryplus.dialogs.InfoDialog;
import in.sunilpaulmathew.batteryplus.serializables.StatusEntry;
import in.sunilpaulmathew.batteryplus.utils.Utils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on Sept. 07, 2026
 */
public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.ViewHolder> {

    private final List<StatusEntry> data;

    public StatusAdapter(List<StatusEntry> items) {
        this.data = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_status, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StatusEntry item = this.data.get(position);
        holder.icon.setImageResource(item.getIcon());
        holder.icon.setColorFilter(item.getIconTintAttribute(holder.icon.getContext()));
        holder.title.setText(item.getTitle());
        holder.title.setTextColor(item.getIconTintAttribute(holder.icon.getContext()));
        holder.description.setText(item.getDescription());

        Utils.setSlideInAnimation(holder.title, position);
    }

    public void updateData(List<StatusEntry> newData) {
        final List<StatusEntry> oldList = new ArrayList<>(this.data);
        final List<StatusEntry> newList = (newData != null) ? new ArrayList<>(newData) : Collections.emptyList();

        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return oldList.size();
            }

            @Override
            public int getNewListSize() {
                return newList.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return Objects.equals(oldList.get(oldItemPosition),
                        newList.get(newItemPosition));
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return Objects.equals(oldList.get(oldItemPosition),
                        newList.get(newItemPosition)) && Objects.equals(oldList.get(oldItemPosition).getDescription(),
                        newList.get(newItemPosition).getDescription());
            }
        });

        this.data.clear();
        this.data.addAll(newList);

        diffResult.dispatchUpdatesTo(this);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final AppCompatImageButton icon;
        private final MaterialTextView description, title;
        ViewHolder(@NonNull View view) {
            super(view);
            icon = view.findViewById(R.id.icon);
            title = view.findViewById(R.id.title);
            description = view.findViewById(R.id.description);

            view.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    StatusEntry entry = data.get(position);
                    new InfoDialog(entry, v.getContext());
                }
            });
        }
    }

}