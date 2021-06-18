package cl.ucn.disc.dsm.contacts;

import android.graphics.Bitmap;

public class Contact {
    //inicialize variable
    String name;
    String phone;
    Bitmap image = null;

    // generete getter and setters


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
