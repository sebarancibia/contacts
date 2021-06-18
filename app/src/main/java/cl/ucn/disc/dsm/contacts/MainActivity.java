package cl.ucn.disc.dsm.contacts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ArrayList<Contact> contacts = new ArrayList<>();
    MainAdapter adapter;
    RecyclerView list_view_contacts;
    RecyclerView recyclerView;




    int REQUEST_READ_CONTACTS = 29;


    Cursor cursor = null;

    TextView tv_phonebook;
    //to store the phonebook
    ArrayList<Contact> arrayList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView=findViewById(R.id.list_view_contacts);

        // assign variable
        list_view_contacts = findViewById(R.id.list_view_contacts);
        //check permission
        checkPermission();


        tv_phonebook=findViewById(R.id.txt_contact_name);
        //to initialize the memory to arraylist
        arrayList= new ArrayList<Contact>();
        //give runtime permission for read contact

        //list_view_contacts = findViewById(R.id.list_view_contacts);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            getcontact();
        }
        else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }
    }

    private void checkPermission() {
        if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_CONTACTS)!=PackageManager.PERMISSION_GRANTED){
            //when permission is not granted
            //request permission
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_CONTACTS},100);
        }else {
            //when permission is granted
            //create method
            getContactList();
        }
    }

    private void getContactList() {
        //initialize
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        //sort by ascending
        String sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC";
        //initialize cursor
        Cursor cursor = getContentResolver().query(uri,null,null,null,sort);
        //check Condition
        if(cursor.getCount() > 0){
            //when count is greater than 0
            // use while loop
            while(cursor.moveToNext()){
                //cursor move to next
                //get contact id
                String id = cursor.getString(cursor.getColumnIndex(
                        ContactsContract.Contacts._ID
                ));
                //get contact name
                String name = cursor.getString(cursor.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME
                ));
                //initialize phone uri
                Uri uriPhone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                //initialize selection
                String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " =?";
                //initialize phone cursor
                Cursor phoneCursor =getContentResolver().query(uriPhone,null,selection,new String[]{id},null);
                //check condition
                if(phoneCursor.moveToNext()){
                    //when phone cursor move to next
                    String number= phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    //initialize contact model
                    Contact contact = new Contact();
                    //set name
                    contact.setName(name);
                    //set number
                    contact.setPhone(number);
                    //add model in array list
                    arrayList.add(contact);
                    //close cursor
                    phoneCursor.close();
                }
            }
            //close cursor
            cursor.close();
        }
        //set layout manager

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //initialize adapter
        adapter = new MainAdapter(this,arrayList);
        //set adapter
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //check contition
        if(requestCode== 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            //when permission is grated
            //call method
            getContactList();
        }else {
            //when permission is denied
            //display toast
            Toast.makeText(MainActivity.this,"Permission Denied.",Toast.LENGTH_SHORT).show();
            //call check permission method
            checkPermission();
        }
    }

    private void getcontact(){
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                ,null
                ,null
                ,null
                ,null);
        ContentResolver contentResolver = getContentResolver();
        try {
            cursor = contentResolver.query(
                    ContactsContract.Contacts.CONTENT_URI
                    , null
                    , null
                    , null
                    , null);
        } catch (Exception ignored) { }
        if (cursor != null && cursor.getCount() > 0) {

            while (cursor.moveToNext()) {
                Contact contact = new Contact();
                String contact_id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                contact.name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0) {
                    Cursor phoneCursor = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                            , null
                            , ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?"
                            , new String[]{contact_id}
                            , null);
                    if (phoneCursor != null) {
                        phoneCursor.moveToNext();
                        contact.phone = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }
                    if (phoneCursor != null) phoneCursor.close();
                }
                contact.image = ContactPhoto(contact_id);
                contacts.add(contact);
            }
            Adapter adapter = new Adapter(this, contacts);
            //list_view_contacts.setAdapter(adapter);
        }

    }

    public Bitmap ContactPhoto(String contactId) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.valueOf(contactId));
        Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = getContentResolver().query(
                photoUri,
                new String[]{ContactsContract.Contacts.Photo.PHOTO}
                , null
                , null
                , null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToNext();
            byte[] data = cursor.getBlob(0);
            if (data != null)
                return BitmapFactory.decodeStream(new ByteArrayInputStream(data));
            else
                return null;
        }
        if (cursor != null) cursor.close();
        return null;
    }




    public class Adapter extends BaseAdapter {

        Context context;
        List<Contact> contactList;

        Adapter(Context context, List<Contact> contactList) {
            this.context = context;
            this.contactList = contactList;
        }

        @Override
        public int getCount() {
            return contactList.size();
        }

        @Override
        public Object getItem(int position) {
            return contactList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            @SuppressLint("ViewHolder") View view = View.inflate(context, R.layout.contact_list_item, null);
            TextView txt_contact_name = view.findViewById(R.id.txt_contact_name);
            TextView txt_contact_phone = view.findViewById(R.id.txt_contact_phone);
            ImageView imageView = view.findViewById(R.id.imageView);

            txt_contact_name.setText(contactList.get(position).name);
            txt_contact_phone.setText(contactList.get(position).phone);
            if (contactList.get(position).image != null)
                imageView.setImageBitmap(contactList.get(position).image);
            else {
                imageView.setImageResource(R.drawable.ic_launcher_foreground);
            }
            view.setTag(contactList.get(position).name);
            return view;
        }
    }

    
}