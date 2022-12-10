package com.shodh.lms.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.shodh.lms.R;

import java.io.File;

public class DownloadedEbookAdapter extends RecyclerView.Adapter<DownloadedEbookAdapter.ViewHolder> {
    Context context;
    File[] files;
    public DownloadedEbookAdapter(Context context,File[] files){
        this.context = context;
        this.files = files;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_file_ebook,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), android.R.anim.slide_in_left);
        holder.itemView.setAnimation(animation);
        holder.tvFileName.setText(files[position].getName());
        holder.tvFileLastModify.setText("");

        File file = new File("/sdcard/LMS/"+files[position].getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = FileProvider.getUriForFile(context,context.getApplicationContext().getPackageName() + ".provider",file);
                intent.setDataAndType(uri,"application/pdf");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (files != null){
            return files.length;
        }else{
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFileName,tvFileLastModify;
        ImageView imgFileIcon;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFileName = itemView.findViewById(R.id.tvFileTitle);
            tvFileLastModify = itemView.findViewById(R.id.tvFileSize);
            imgFileIcon = itemView.findViewById(R.id.imgFileIcon);
        }
    }
}
