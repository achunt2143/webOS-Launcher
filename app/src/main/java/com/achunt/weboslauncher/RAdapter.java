package com.achunt.weboslauncher;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RAdapter extends RecyclerView.Adapter<RAdapter.ViewHolder> {

    public static List<AppInfo> appsList;
    public static int phone = 0;
    public static int contacts = 0;
    public static int messages = 0;

    public RAdapter(Context c) {

        new Thread(() -> {
            PackageManager pm = c.getPackageManager();
            appsList = new ArrayList<>();
            Intent i = new Intent(Intent.ACTION_MAIN, null);
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> allApps = pm.queryIntentActivities(i, 0);

            for (ResolveInfo ri : allApps) {
                AppInfo app = new AppInfo();
                app.label = ri.loadLabel(pm);
                app.packageName = ri.activityInfo.packageName;
                app.icon = ri.activityInfo.loadIcon(pm);
                appsList.add(app);
            }

            appsList.sort(Comparator.comparing(o -> o.label.toString()));
            int j = 0;

            while (j < appsList.size()) {
                if (appsList.get(j).packageName.toString().contains("dialer")) {
                    phone = j;
                } else if (appsList.get(j).packageName.toString().contains("contacts")) {
                    contacts = j;
                } else if (appsList.get(j).packageName.toString().contains("messag")) {
                    messages = j;
                }
                j++;
            }
        }).start();
    }

    @Override
    public int getItemCount() {
        return appsList.size();
    }

    public void onBindViewHolder(RAdapter.ViewHolder viewHolder, int i) {
        String appLabel = appsList.get(i).label.toString();
        Drawable appIcon = appsList.get(i).icon;
        TextView textView = viewHolder.textView;
        textView.setText(appLabel);
        ImageView imageView = viewHolder.img;
        imageView.setImageDrawable(appIcon);
    }

    @NonNull
    public RAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_row_list_view, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;
        public ImageView img;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_app_name);
            img = itemView.findViewById(R.id.app_icon);

            new Thread(() -> itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                Context context = v.getContext();
                Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(appsList.get(pos).packageName.toString());
                context.startActivity(launchIntent);
            })).start();
        }
    }
}