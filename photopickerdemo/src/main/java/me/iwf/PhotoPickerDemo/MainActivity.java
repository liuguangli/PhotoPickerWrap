package me.iwf.PhotoPickerDemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

import commonview.AddPicLayout;
import commonview.OnPreviewListener;
import commonview.ShowPicLayout;
import me.iwf.photopicker.PhotoPagerActivity;
import me.iwf.photopicker.PhotoPickerActivity;
import me.iwf.photopicker.utils.PhotoPickerIntent;

public class MainActivity extends AppCompatActivity implements OnPreviewListener{



  private AddPicLayout addPicLayout;
  ShowPicLayout showPicLayout;
  ArrayList<String> selectedPhotos = new ArrayList<>();
  ArrayList<String> urls = new ArrayList<>();
  public final static int REQUEST_CODE = 1;


  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    addPicLayout = (AddPicLayout) findViewById(R.id.addPicLayout);
    addPicLayout.setOnPreviewListener(this);
    showPicLayout = (ShowPicLayout) findViewById(R.id.showPicLayout);
    showPicLayout.setOnPreviewListener(this);
    urls = new ArrayList<>();
    urls.add("http://img0.bdstatic.com/img/image/2043d07892fc42f2350bebb36c4b72ce1409212020.jpg");
    showPicLayout.setPaths(urls);
    findViewById(R.id.addUrl).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

        urls.add("http://img0.bdstatic.com/img/image/2043d07892fc42f2350bebb36c4b72ce1409212020.jpg");
        showPicLayout.setPaths(urls);
      }
    });
  }





  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    List<String> photos = null;
    if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
      if (data != null) {
        photos = data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
      }
      selectedPhotos.clear();

      if (photos != null) {
        selectedPhotos.addAll(photos);
      }
      addPicLayout.setPaths(selectedPhotos);
    }
  }


  @Override
  public void onPreview(int pos,boolean delete) {

    Intent intent = new Intent(this, PhotoPagerActivity.class);
    intent.putExtra(PhotoPagerActivity.EXTRA_CURRENT_ITEM, pos);
    intent.putExtra(PhotoPagerActivity.EXTRA_SHOW_DELETE,delete);
    if (delete){
      intent.putExtra(PhotoPagerActivity.EXTRA_PHOTOS, selectedPhotos);
    } else {
      intent.putExtra(PhotoPagerActivity.EXTRA_PHOTOS, urls);
    }
    startActivityForResult(intent, REQUEST_CODE);
  }

  @Override
  public void onPick() {
    PhotoPickerIntent intent = new PhotoPickerIntent(MainActivity.this);
    intent.setPhotoCount(9);
    intent.setShowCamera(true);
    intent.setShowGif(false);
    startActivityForResult(intent, REQUEST_CODE);
  }
}
