package im.ene.lab.swipecards;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import im.ene.lab.swipecards.adapters.ServiceCardDto;
import im.ene.lab.swipecards.adapters.ServiceCardDtoListAdapter;
import im.ene.lab.swipecards.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eneim on 10/7/15.
 */
public class ImagesTabActivity extends AppCompatActivity {

  @Bind(R.id.service_selection_flingswipe)
  SwipeFlingAdapterView mFlingView;
  @Bind(R.id.likedButton)
  ImageButton likedButton;
  @Bind(R.id.nopeButton)
  ImageButton nopeButton;
  private List<ServiceCardDto> serviceCardDtos = new ArrayList<>();
  //    private ArrayAdapter<ServiceCardDto> arrayAdapter;
  private ServiceCardDtoListAdapter arrayAdapter;

  //    private int i;
  private List<ServiceCardDto> serviceCardDtos1 = new ArrayList<>();
  private List<ServiceCardDto> serviceCardDtos2 = new ArrayList<>();

  static void makeToast(Context ctx, String s) {
    Toast.makeText(ctx, s, Toast.LENGTH_SHORT).show();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_my);
    ButterKnife.bind(this);

    generateData();

    Toolbar toolbar = (Toolbar) findViewById(R.id.servce_selection_toolbar);
    toolbar.setTitle("ServiceSelection");
    setSupportActionBar(toolbar);

    TabLayout tabLayout = (TabLayout) findViewById(R.id.service_selection_tabbar);
    tabLayout.addTab(tabLayout.newTab().setText("Tab1"));
    tabLayout.addTab(tabLayout.newTab().setText("Tab2"));
    tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
      @Override
      public void onTabSelected(TabLayout.Tab tab) {
        Log.d("TEST", "tab selected: " + tab.getPosition());
        serviceCardDtos.clear();
        if (tab.getPosition() == 0) {
          serviceCardDtos.addAll(serviceCardDtos1);
        } else {
          serviceCardDtos.addAll(serviceCardDtos2);
        }

        // FIXME
        mFlingView.refresh();
//        arrayAdapter.notifyDataSetChanged();

//          arrayAdapter = new ServiceCardDtoListAdapter(MyActivity.this, R.layout.service_card,
// serviceCardDtos);
//          mFlingView.setAdapter(arrayAdapter);
//          mFlingView.refresh();

      }

      @Override
      public void onTabUnselected(TabLayout.Tab tab) {

      }

      @Override
      public void onTabReselected(TabLayout.Tab tab) {

      }
    });


    serviceCardDtos.addAll(serviceCardDtos1);


//        arrayAdapter = new ArrayAdapter<>(this, R.layout.service_card, R.id.helloText,
// serviceCardDtos);
    arrayAdapter = new ServiceCardDtoListAdapter(this, R.layout.service_card, serviceCardDtos);

    mFlingView.setAdapter(arrayAdapter);
    mFlingView.setFlingListener(new SwipeFlingAdapterView.OnSwipeListener() {

      float damper = 8f;
      float nopeStartScale = nopeButton.getScaleX();
      float likedStartScale = likedButton.getScaleX();
      int initColor = Color.rgb(12, 12, 12);
      int nopeTargetColor = getResources().getColor(R.color.unnamedColor5);
      int likedTargetColor = getResources().getColor(R.color.actionColor);

      @Override
      public Pair<Boolean, Boolean> isEnabled() {
//              Log.d("LIST", "isEnabled");
        return new Pair<>(true, true);
      }

      @Override
      public void onTopExited() {
        // this is the simplest way to delete an object from the Adapter (/AdapterView)
        Log.d("LIST", "***removed object! serviceCardDtos: " + serviceCardDtos.size());
        serviceCardDtos.remove(0);
        arrayAdapter.notifyDataSetChanged();

        // reset action service_selection_buttons
        likedButton.getBackground().clearColorFilter();
        likedButton.setScaleX(likedStartScale);
        likedButton.setScaleY(likedStartScale);

        nopeButton.getBackground().clearColorFilter();
        nopeButton.setScaleX(nopeStartScale);
        nopeButton.setScaleY(nopeStartScale);
      }

      @Override
      public void onExitToLeft(View dataObject) {
        //Do something on the left!
        //You also have access to the original object.
        //If you want to use it just cast it (String) dataObject
        Log.d("LIST", "***onExitToLeft nope");
//        makeToast(MyActivity.this, "Left!");
      }

      @Override
      public void onExitToRight(View dataObject) {
        Log.d("LIST", "***onExitToRight liked");
//        makeToast(MyActivity.this, "Right!");
      }

      @Override
      public void onAdapterAboutToEmpty(int itemsInAdapter) {
        // Ask for more data here
        //serviceCardDtos.add((i + 1) + "");
//                arrayAdapter.notifyDataSetChanged();
        Log.d("LIST", "notified");
//                i++;
      }


      @Override
      public void onFlingTopView(float offset) {

//              Log.d("LIST", "**********************onFlingTopView offset: "+offset);

        // e.g. change color of indicators
        View view = mFlingView.getSelectedView();
        view.findViewById(R.id.swipe_card_nope_indicator).setAlpha(offset < 0 ? -offset + 0.22f :
            0);
        view.findViewById(R.id.swipe_card_liked_indicator).setAlpha(offset > 0 ? offset + 0.22f :
            0);

        // animate action service_selection_buttons
        if (offset < 0) {
          float nopeScale = (-offset / damper) + nopeStartScale;
          nopeButton.setScaleX(nopeScale);
          nopeButton.setScaleY(nopeScale);
//                nopeButton.setColorFilter(getColorByOffset(-offset, initColor, nopeTargetColor));
          nopeButton.getBackground().setColorFilter(getColorByOffset(-offset, initColor,
              nopeTargetColor), PorterDuff.Mode.SRC_ATOP);

          likedButton.setScaleX(likedStartScale);
          likedButton.setScaleY(likedStartScale);
        } else if (offset > 0) {
          float likedScale = (offset / damper) + likedStartScale;
          likedButton.setScaleX(likedScale);
          likedButton.setScaleY(likedScale);
//                likedButton.setColorFilter(getColorByOffset(offset, initColor, likedTargetColor));
          likedButton.getBackground().setColorFilter(getColorByOffset(offset, initColor,
              likedTargetColor), PorterDuff.Mode.SRC_ATOP);

          nopeButton.getBackground().clearColorFilter();
          nopeButton.setScaleX(nopeStartScale);
          nopeButton.setScaleY(nopeStartScale);
        } else {
          nopeButton.getBackground().clearColorFilter();
          likedButton.getBackground().clearColorFilter();
        }

      }
    });

    // Optionally add an OnItemClickListener
    mFlingView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                makeToast(MyActivity.this, "Clicked!");

        ImageView serviceCardImageView = (ImageView) view.findViewById(R.id.service_card_imageview);

        ServiceCardDto dto = serviceCardDtos.get(position);

//        Intent intent = new Intent(MyActivity.this, ServiceDetailsActivity.class);
//        intent.putExtra(ServiceDetailsActivity.DTO, (Serializable) dto);
//        // Pass data object in the bundle and populate details activity.
//        ActivityOptionsCompat options = ActivityOptionsCompat
//            .makeSceneTransitionAnimation(MyActivity.this, (View) serviceCardImageView,
//                "transition_service_card");
//
//        startActivity(intent, options.toBundle());

      }
    });

  }

  private void generateData() {
    ServiceCardDto dto1 = new ServiceCardDto();
    dto1.setTitle("Card1 Hamburg aus Nordosten. Luftaufnahme 2007. Blick elbabwärts; links das ");
    dto1.setCompanyName("Company1 Company1 Company1 Company1 Company1 Company1");
    List<String> urls1 = new ArrayList<>();
    urls1.add("http://www.blumen-salzmann.de/images/index/blumen-salzmann.jpg");
    dto1.setImageUrls(urls1);
    serviceCardDtos1.add(dto1);

    ServiceCardDto dto2 = new ServiceCardDto();
    dto2.setTitle("Card2 Hamburg aus Nordosten. Luftaufnahme 2007. Blick elbabwärts; links das ");
    dto2.setCompanyName("Company1");
    List<String> urls2 = new ArrayList<>();
    urls2.add("https://hottelling.files.wordpress" +
        ".com/2014/04/hafengeburtstag-in-hamburg-mit-queen-mary-ii-foto-christian-spahrbier.jpg");
    dto2.setImageUrls(urls2);
    serviceCardDtos1.add(dto2);

    serviceCardDtos1.add(dto1);
    serviceCardDtos1.add(dto2);
    serviceCardDtos1.add(dto1);
    serviceCardDtos1.add(dto2);
    serviceCardDtos1.add(dto1);
    serviceCardDtos1.add(dto2);
    serviceCardDtos1.add(dto1);
    serviceCardDtos1.add(dto2);
    serviceCardDtos1.add(dto2);
    serviceCardDtos1.add(dto2);
    serviceCardDtos1.add(dto2);
    serviceCardDtos1.add(dto2);
    serviceCardDtos1.add(dto2);
    serviceCardDtos1.add(dto2);
    serviceCardDtos1.add(dto2);
    serviceCardDtos1.add(dto2);
    serviceCardDtos1.add(dto2);

    for (int i = 0; i < 300; i++) {
      serviceCardDtos1.add(dto2);
      serviceCardDtos1.add(dto1);
    }

    // list2

    ServiceCardDto dto3 = new ServiceCardDto();
    dto3.setTitle("2 Melbourne ");
    dto3.setCompanyName("Company2");
    List<String> urls3 = new ArrayList<>();
    urls3.add("http://accorhotels.com.au/files/MelbourneBridge_2400x1350_0.jpg");
    dto3.setImageUrls(urls3);
    serviceCardDtos2.add(dto3);

    ServiceCardDto dto4 = new ServiceCardDto();
    dto4.setTitle("2 Card2 Melbourne 2");
    dto4.setCompanyName("Company2");
    List<String> urls4 = new ArrayList<>();
    urls4.add("http://www.calicultural.com.br/wp-content/uploads/2014/10/Melbourne1.jpg");
    dto4.setImageUrls(urls4);
    serviceCardDtos2.add(dto4);

    serviceCardDtos2.add(dto3);
    serviceCardDtos2.add(dto4);
    serviceCardDtos2.add(dto3);
    serviceCardDtos2.add(dto4);
    serviceCardDtos2.add(dto3);
    serviceCardDtos2.add(dto4);
    serviceCardDtos2.add(dto3);
    serviceCardDtos2.add(dto4);
    serviceCardDtos2.add(dto3);
    serviceCardDtos2.add(dto3);
    serviceCardDtos2.add(dto3);
    serviceCardDtos2.add(dto3);
    serviceCardDtos2.add(dto3);
    serviceCardDtos2.add(dto3);
    serviceCardDtos2.add(dto3);
    serviceCardDtos2.add(dto3);
    serviceCardDtos2.add(dto3);
    serviceCardDtos2.add(dto3);
    serviceCardDtos2.add(dto3);
    serviceCardDtos2.add(dto3);
    serviceCardDtos2.add(dto3);
    serviceCardDtos2.add(dto3);
    serviceCardDtos2.add(dto3);
    serviceCardDtos2.add(dto3);
    serviceCardDtos2.add(dto3);
    serviceCardDtos2.add(dto3);


  }

  private int getColorByOffset(float percent, int initColor, int targetColor) {
    float t = percent;

    int r = (int) (Color.red(initColor) + t * (Color.red(targetColor) - Color.red(initColor)));
    int g = (int) (Color.green(initColor) + t * (Color.green(targetColor) - Color.green
        (initColor)));
    int b = (int) (Color.blue(initColor) + t * (Color.blue(targetColor) - Color.blue(initColor)));

    return Color.rgb(r, g, b);
  }

  @OnClick(R.id.nopeButton)
  public void nopeButtonTapped() {
    mFlingView.trigerSwipeLeft();
  }

  @OnClick(R.id.likedButton)
  public void likedButtonTapped() {
    mFlingView.trigerSwipeRight();
  }
}
