package com.example.startproject2;


public class Movie {
  //리퀘스트로 받아 오는 Json 데이터
    String title;
    String link;
    String image;
    String subtitle;
    String pubDate;
    String director;
    String actor;
    float userRating;




  //영화 정보 저장
  public Movie(String title, String link, String image,String subtitle, String pubDate,
               String director, String actor, float userRating){
      this.title = title;
      this.link = link;
      this.image = image;
      this.subtitle = subtitle;
      this.pubDate = pubDate;
      this.director = director;
      this.actor = actor;
      this.userRating = userRating;
    }
}
