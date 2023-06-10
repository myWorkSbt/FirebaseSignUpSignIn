package com.example.fbtask;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.button.MaterialButton;


public class FirebaseBlogListAdapter extends FirebaseRecyclerAdapter<postsdetail, FirebaseBlogListAdapter.myViewHolder> {
    public FirebaseBlogListAdapter(FirebaseRecyclerOptions<postsdetail> options){
        super(options);
    }

    @NonNull
    @Override
    public FirebaseBlogListAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_lays,parent, false);
        return new myViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull FirebaseBlogListAdapter.myViewHolder holder, int position, @NonNull postsdetail model) {
        holder.tv_name.setText(model.getUser_name());
//        Glide.with(holder.user_imgs.getContext()).load(Uri.parse(model.getImgLinks())).into(holder.user_imgs);
        holder.tv_no_oflikes.setText(model.getNofl());
    }

    public class myViewHolder extends RecyclerView.ViewHolder{
        private ImageView user_imgs;
        private TextView tv_name,tv_no_oflikes;
        private MaterialButton btn_like;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            user_imgs= (ImageView) itemView.findViewById(R.id.posted_image_views);
            tv_name=(TextView) itemView.findViewById(R.id.tv_user_name);
            tv_no_oflikes= (TextView) itemView.findViewById(R.id.tv_no_of_likes);
            btn_like= (MaterialButton) itemView.findViewById(R.id.btn_like);
        }
    }
}
