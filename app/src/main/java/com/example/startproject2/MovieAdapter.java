package com.example.startproject2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder>{

    ArrayList<Movie> items = new ArrayList<Movie>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.movie_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Movie item = items.get(position);
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(Movie item){
        items.add(item);
    }

    public void setItems(ArrayList<Movie> items){
        this.items = items;
    }

    public Movie getItem(int position) {
        return items.get(position);
    }

    public void clearItems() {
        this.items.clear();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView, textView2, textView3, textView4;
        ImageView imageView;
        RatingBar ratingBar;
        Context context;
        public final View layout;

        Bitmap bitmap;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.textView);
            textView2 = itemView.findViewById(R.id.textView2);
            textView3 = itemView.findViewById(R.id.textView3);
            textView4 = itemView.findViewById(R.id.textView4);
            imageView = itemView.findViewById(R.id.imageView);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            context = itemView.getContext();
            layout = itemView;
        }

        public void setItem(final Movie item) {

            //String 값에 포함된 태그 제거
            item.title = item.title.replace("<b>", "");
            item.title = item.title.replace("</b>", "");
            item.director = item.director.replace("|"," ");
            item.actor = item.actor.replace("|", " ");

            //textView에 출력하기
            textView.setText(item.title+" ("+item.pubDate+")");
            textView2.setText("감독: " + item.director);
            textView3.setText("출연: " + item.actor);

            //Glide 라이브러리를 이용하여 imageView 표현.
            Glide.with(context).load(item.image).error(R.drawable.movie).into(imageView);

            if(item.userRating != 0) {
                textView4.setText(item.userRating + " ");
                ratingBar.setVisibility(View.VISIBLE);
                ratingBar.setRating(item.userRating / 2);
            } else {
                textView4.setText("평점 없음");
                ratingBar.setVisibility(View.GONE);
            }

          //해당 아이템을 클릭했을 때 링크주소로 상세화면 이동.
          itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
              Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.link));
              itemView.getContext().startActivity(intent);
            }
          });
        }
    }
}
