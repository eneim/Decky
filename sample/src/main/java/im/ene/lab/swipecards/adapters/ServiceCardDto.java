package im.ene.lab.swipecards.adapters;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Dr. Michael Gorski on 01.09.15.
 */
public class ServiceCardDto implements Serializable {

  private static final long serialVersionUID = -437580048503173599L;

  private String title;
  private String description;
  private String priceDescription;
  private List<String> imageUrls;
  private double latitude;
  private double longitude;
  private String imageCopyright;
  private String bookingUrl;
  private String bookingEmail;
  private String bookingPhone;
  private long lastUpdatedTime;
  private String companyName;
  private String companyId;


  public ServiceCardDto() {
  }


  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getPriceDescription() {
    return priceDescription;
  }

  public void setPriceDescription(String priceDescription) {
    this.priceDescription = priceDescription;
  }

  public List<String> getImageUrls() {
    return imageUrls;
  }

  public void setImageUrls(List<String> imageUrls) {
    this.imageUrls = imageUrls;
  }

  public double getLatitude() {
    return latitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public String getImageCopyright() {
    return imageCopyright;
  }

  public void setImageCopyright(String imageCopyright) {
    this.imageCopyright = imageCopyright;
  }

  public String getBookingUrl() {
    return bookingUrl;
  }

  public void setBookingUrl(String bookingUrl) {
    this.bookingUrl = bookingUrl;
  }

  public String getBookingEmail() {
    return bookingEmail;
  }

  public void setBookingEmail(String bookingEmail) {
    this.bookingEmail = bookingEmail;
  }

  public String getBookingPhone() {
    return bookingPhone;
  }

  public void setBookingPhone(String bookingPhone) {
    this.bookingPhone = bookingPhone;
  }

  public long getLastUpdatedTime() {
    return lastUpdatedTime;
  }

  public void setLastUpdatedTime(long lastUpdatedTime) {
    this.lastUpdatedTime = lastUpdatedTime;
  }

  public String getCompanyName() {
    return companyName;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  public String getCompanyId() {
    return companyId;
  }

  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }

}
