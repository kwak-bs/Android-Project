package com.example.startproject2;


import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.io.IOException;
import java.sql.SQLOutput;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static java.sql.DriverManager.println;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    MovieAdapter adapter;
    RecyclerView recyclerView;
    MovieList movielist;
    Handler handler = new Handler();
    String uriString = "content://com.example.startproject2.movieprovider/movie";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_search, container, false);

        adapter = new MovieAdapter();

        recyclerView = rootView.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);

        SearchView searchView = rootView.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query) {
              OkHttpClient client = new OkHttpClient(); // OkHttp3를 통한 API 연동

              final Request request = new Request.Builder()
                .addHeader("X-Naver-Client-Id","OC9ZvEQX8T4mrz8VBFNj") //Header에 ID와 Password 담아 호출하기
                .addHeader("X-Naver-Client-Secret","kZWCI4LJEr")
                .url("https://openapi.naver.com/v1/search/movie.json?query="+query) // Naver API url query에 요청하기
                .build();
                client.newCall(request).enqueue(new Callback() {
                  @Override
                  public void onFailure(Call call, IOException e) {
                    System.out.println(e);
                    System.out.println("영화 불러오기 실패");
                  }
                  //Gson 객체를 생성하여 가져온 문자열을 gson으로 변환 후 MovieList에 저장
                  @Override
                  public void onResponse(Call call, Response response) throws IOException {
                    try{
                      Gson gson = new Gson();
                      String string = response.body().string();
                      System.out.println(string + " ok");
                      movielist = gson.fromJson(string, MovieList.class);
                      clearMovie();
                      insertMovie(movielist);
                      //handler를 통하여 가져온 영화를 adapter에 저장.
                      handler.post(new Runnable() {
                        @Override
                        public void run() {
                          for(int i=0; i<movielist.items.size(); i++){
                            adapter.addItem(movielist.items.get(i));
                          }
                          adapter.notifyDataSetChanged();
                        }
                      });

                    } catch (Exception e){
                      e.printStackTrace();
                    }
                  }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

        });
      queryMovie();

        return rootView;
    }
  private void insertMovie(MovieList movieList) {
    System.out.println("insertMovie 호출");
    Uri uri = new Uri.Builder().build().parse(uriString);
    Cursor cursor = getActivity().getContentResolver().query(uri, null, null,
      null, null);
    if(movieList.items.size() != 0) {
      for (int i = 0; i < movieList.items.size(); i++) {
        Movie movie = movieList.items.get(i);
        ContentValues values = new ContentValues();
        values.put("title", movie.title);
        values.put("director", movie.director);
        values.put("actor", movie.actor);
        values.put("link", movie.link);
        values.put("rating", movie.userRating);
        values.put("image", movie.image);
        values.put("pubDate", movie.pubDate);
        uri = getActivity().getContentResolver().insert(uri, values);
        System.out.println("insert 결과" + uri.toString());
      }
    }
  }

  private void queryMovie() {
    System.out.println("queryMovie 호출");
    Uri uri = new Uri.Builder().build().parse(uriString);
    String[] columns = new String[] {"title", "director", "actor", "link", "rating", "image","pubDate"};
    Cursor cursor = getActivity().getContentResolver().query(uri,columns, null,null,"title ASC");

    while(cursor.moveToNext()){
      String title = cursor.getString(cursor.getColumnIndex(columns[0]));
      String director = cursor.getString(cursor.getColumnIndex(columns[1]));
      String actor = cursor.getString(cursor.getColumnIndex(columns[2]));
      String link = cursor.getString(cursor.getColumnIndex(columns[3]));
      float userRating = cursor.getFloat(cursor.getColumnIndex(columns[4]));
      String image = cursor.getString(cursor.getColumnIndex(columns[5]));
      String pubDate = cursor.getString(cursor.getColumnIndex(columns[6]));
      adapter.addItem(new Movie(title,link,image,null, pubDate,director,actor,userRating));
    }
    adapter.notifyDataSetChanged();
    System.out.println("query 결과 : " + cursor.getCount());
  }
  private void clearMovie() {
    Uri uri = new Uri.Builder().build().parse(uriString);
    int count = getActivity().getContentResolver().delete(uri, null, null);
  }


}
