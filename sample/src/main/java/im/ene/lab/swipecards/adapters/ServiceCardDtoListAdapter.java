package im.ene.lab.swipecards.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import im.ene.lab.swipecards.R;

import java.util.List;

/**
 * Created by Dr. Michael Gorski on 02.09.15.
 */
public class ServiceCardDtoListAdapter extends BaseAdapter {

  private Context context;
  private LayoutInflater inflater;
  private List<ServiceCardDto> items;

  /**
   * The resource indicating what views to inflate to display the content of this
   * adapter.
   */
  private int mResource;



  public ServiceCardDtoListAdapter(Context context, int resource, List<ServiceCardDto> items) {
    this.context = context;
    this.mResource = resource;
    this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    this.items = items;
  }

  @Override
  public int getCount() {
    return items.size();
  }

  @Override
  public Object getItem(int position) {
    return items.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    return createViewFromResource(position, convertView, parent, mResource);
  }

  private View createViewFromResource(final int position, View convertView, ViewGroup parent,
                                      int resource) {

    RoundedImageView imageView = null;
    TextView title = null;
    TextView location = null;

    if (convertView == null) {
      convertView = inflater.inflate(resource, parent, false);
    }

    try {
      imageView = (RoundedImageView)convertView.findViewById(R.id.service_card_imageview);
      title = (TextView)convertView.findViewById(R.id.service_card_title);
      location = (TextView)convertView.findViewById(R.id.service_card_location);
    }
    catch (Exception e) {
      Log.e("Adapter", "Cannot create view resource IDs are missing", e);
    }

    // get the correct service_card
    ServiceCardDto item = items.get(position);

    // fill the view
    Picasso.with(context)
        .load(item.getImageUrls().get(0))
        .placeholder(R.drawable.card_image)
        .error(R.drawable.card_image)
//        .centerCrop()
//        .fit()
        .into(imageView, new Callback() {
          @Override public void onSuccess() {
            Log.d("onSuccess", "position: " + position);
          }

          @Override public void onError() {
            Log.d("onError", "position: " + position);
          }
        });

    title.setText(item.getTitle());
    location.setText(item.getCompanyName());

    return convertView;
  }

}
