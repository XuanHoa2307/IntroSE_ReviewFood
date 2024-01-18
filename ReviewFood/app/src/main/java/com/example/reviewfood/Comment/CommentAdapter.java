package com.example.reviewfood.Comment;

import android.content.Context;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.reviewfood.CommentActivity;
import com.example.reviewfood.R;
import com.example.reviewfood.TimestampConverter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private Context context;
    private List<Comment> comments;

    FirebaseFirestore fireStore;

    String currentUserID;
    public CommentAdapter(Context context, List<Comment> comments, String currentUserID) {
        this.context = context;
        this.comments = comments;
        this.fireStore = FirebaseFirestore.getInstance();
        this.currentUserID = currentUserID;
    }




    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycleview_comment, parent, false);
        return new CommentAdapter.CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {

        String commentId = comments.get(position).commentId;

        String userID = comments.get(position).getUserID();
        if(userID != null){
            fireStore.collection("User").document(userID).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String avatarUri = documentSnapshot.getString("imageUri");
                    Glide.with(context).load(avatarUri).placeholder(R.drawable.avatar_default).into(holder.avatarUser);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Glide.with(context).load(R.drawable.avatar_default).into(holder.avatarUser);
                }
            });
        }

        holder.userName.setText(comments.get(position).getUserName());
        holder.content.setText(comments.get(position).getContent());
        holder.commentTime.setText(TimestampConverter.getTime(comments.get(position).getCmtTime()));

        holder.content.setOnClickListener(new View.OnClickListener() {
            boolean expanded = false;
            @Override
            public void onClick(View v) {
                if (expanded) {
                    // Giảm số dòng hiển thị khi đã mở rộng
                    holder.content.setMaxLines(2);
                    holder.content.setEllipsize(TextUtils.TruncateAt.END);
                } else {
                    // Hiển thị toàn bộ nội dung khi chưa mở rộng
                    holder.content.setMaxLines(Integer.MAX_VALUE);
                    holder.content.setEllipsize(null);
                }
                expanded = !expanded;
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });

        holder.itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                MenuInflater inflater = ((CommentActivity) context).getMenuInflater();
                inflater.inflate(R.menu.comment_context_menu, menu);
            }
        });

    }

    @Override
    public int getItemCount() {
        return comments.size();
    }



    public static class CommentViewHolder extends RecyclerView.ViewHolder {

        private TextView userName;
        private TextView content;
        private TextView commentTime;
        private CircleImageView avatarUser;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.userName_itemComment);
            content = itemView.findViewById(R.id.comment_itemComment);
            avatarUser = itemView.findViewById(R.id.avatarUser_itemComment);
            commentTime = itemView.findViewById(R.id.commentTime);
        }


    }

}
