package com.example.startproject2;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyFragment extends Fragment {

    EditText editText1, editText2, editText3, editText4;
    ImageView imageView4;
    RadioGroup radioGroup;
    File file;
    PaintBoard board;
    Calendar calendar = Calendar.getInstance();
    Button button;

    public MyFragment() {
        // Required empty public constructor
    }

    //OnDateSetListener 구현 후 onDateSet() 함수 오버 라이딩 , DatePickDialog창이 활성화되고 사용자가
    //입력한 뒤 완료하였을 경우 실행되는 함수.
    DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
      @Override
      public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
         calendar.set(Calendar.YEAR, year);
         calendar.set(Calendar.MONTH, month);
         calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
         upadteLabel();
    }
  };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_my, container, false);

        editText1 = rootView.findViewById(R.id.editText);
        editText2 = rootView.findViewById(R.id.editText2);
        editText3 = rootView.findViewById(R.id.editText3);
        editText4 = rootView.findViewById(R.id.editText4);
        radioGroup = rootView.findViewById(R.id.radioGroup);
        imageView4 = rootView.findViewById(R.id.imageView4);
        button = rootView.findViewById(R.id.button);
        board = rootView.findViewById(R.id.PaintBoard);

        button.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            board.mBitmap.eraseColor(Color.WHITE);

          }
        });

        //내 정보 안의 프로필 이미지를 클릭했을 경우
        imageView4.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            takePicture();
          }
        });

        //생년월일 editText2를 클릭할 경우 Calender 보여주기
        editText2.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            new DatePickerDialog(getActivity(),onDateSetListener,calendar.get(Calendar.YEAR),
              calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
          }
        });

        //저장된 값을 불러오기 위해 같은 네임파일을 찾음.
        SharedPreferences sf = this.getActivity().getSharedPreferences("File", Context.MODE_PRIVATE);

        //key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 defalut 값을 반환.
        String text = sf.getString("text","");
        int radio = sf.getInt("radioGroup",0);
        String email = sf.getString("email", "");
        String password = sf.getString("password", "");
        String birtyday = sf.getString("birthday", "");

        editText1.setText(text);
        radioGroup.check(radio);
        editText3.setText(email);
        editText4.setText(password);
        editText2.setText(birtyday);
        return rootView;
    }

    private void takePicture() {
      if(file == null) {
        file = createFile();
      }
      Uri fileUri = FileProvider.getUriForFile(this.getActivity(), "com.example.startproject2.fileprovider", file);
      Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
      if(intent.resolveActivity(getActivity().getPackageManager()) != null){
        startActivityForResult(intent, 101);
      }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @NonNull Intent data){
      super.onActivityResult(requestCode, resultCode, data);

      if(requestCode ==101 && resultCode == RESULT_OK) {


        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), null);
        int degree = getExifOrientation(file.getAbsolutePath());
        bitmap = getRotatedBitmap(bitmap, degree);
        imageView4.setImageBitmap(bitmap);
      }
    }

    private File createFile() {
      String filename = "capture.jpg";
      File storageDir = getActivity().getExternalFilesDir(null);
      File outFile = new File(storageDir, filename);

      return outFile;
    }
  private File createFile2() {
    String filename = "signature.png";
    File storageDir = getActivity().getExternalFilesDir(null);
    File outFile = new File(storageDir, filename);

    return outFile;
  }

    //사용자가 날짜를 선택 후 eidtText2에 출력하기.
    public void upadteLabel() {
      String myFormat = "yyyy년 MM월 dd일"; //출력형식 2018년 11월 28일
      SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);
      editText2.setText(sdf.format(calendar.getTime()));
    }

    public void onStart() {
      super.onStart();

        file = createFile();
        if(file.exists()) {
          Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), null);

          int degree = getExifOrientation(file.getAbsolutePath());
          bitmap = getRotatedBitmap(bitmap, degree);
          imageView4.setImageBitmap(bitmap);

      }
    }
    @Override
    public void onResume(){
      super.onResume();

      File signatureFile = createFile2();
      if (signatureFile.exists() ) {
        Bitmap bitmap = BitmapFactory.decodeFile(signatureFile.getAbsolutePath(), null);
        board.changeBitmap(bitmap);
      }
    }

    @Override
    public void onPause() {
      super.onPause();

      File signatureFile = new File(getActivity().getExternalFilesDir(null),
        "signature.png");
      Bitmap bitmap = board.mBitmap;
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos); //compress 함수를 사용해 스트림에 비트맵을 저장.
      try {
        FileOutputStream fos = new FileOutputStream(signatureFile); //파일을 쓸 수 있는 스트림 준비
        fos.write(bos.toByteArray());
        fos.flush();
        fos.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    public void onStop(){
      super.onStop();

      // Activity가 종료되기 전에 저장한다.
      //SharedPreferences를 File을 이름으로 설정 및 기본모드로 설정
      SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("File", Context.MODE_PRIVATE);

      //저장을 하기위해 editor를 이용하여 값을 저장시켜준다.
      SharedPreferences.Editor editor = sharedPreferences.edit();

      //사용자가 입력한 저장할 데이터
      String text = editText1.getText().toString();
      int radio = radioGroup.getCheckedRadioButtonId();
      String email = editText3.getText().toString();
      String password = editText4.getText().toString();
      String birthday = editText2.getText().toString();

      //key, value를 이용하여 저장, 다양한 형태의 변수값 저장 가능.
      editor.putString("text",text);
      editor.putInt("radioGroup", radio);
      editor.putString("email", email);
      editor.putString("password", password);
      editor.putString("birthday", birthday);


      //최종 커밋
      editor.commit();
    }
  private int getExifOrientation(String filePath) {
    ExifInterface exif = null;

    try {
      exif = new ExifInterface(filePath);
    } catch (IOException e) {
      e.printStackTrace();
    }

    if (exif != null) {
      int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_NORMAL);

      if (orientation != -1) {
        switch (orientation) {
          case ExifInterface.ORIENTATION_ROTATE_90:
            return 90;

          case ExifInterface.ORIENTATION_ROTATE_180:
            return 180;

          case ExifInterface.ORIENTATION_ROTATE_270:
            return 270;
        }
      }
    }
    return 0;
  }
  private Bitmap getRotatedBitmap(Bitmap bitmap, int degree) {
    if (degree != 0 && bitmap != null) {
      Matrix matrix = new Matrix();
      matrix.setRotate(degree, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);

      try {
        Bitmap tmpBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        if (bitmap != tmpBitmap) {
          bitmap.recycle();
          bitmap = tmpBitmap;
        }
      } catch (OutOfMemoryError e) {
        e.printStackTrace();
      }
    }
    return bitmap;
  }
}
