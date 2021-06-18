package cl.ucn.disc.dsm.contacts;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {
    //initialize variable
    Activity activity;
    ArrayList<Contact> arrayList;

    //create constructor
    public MainAdapter(Activity activity,ArrayList<Contact> arrayList){
        this.activity =activity;
        this.arrayList =arrayList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //initialize view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_list_item,parent,false);
        //return view
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //initialize constructor
        Contact contact = arrayList.get(position);

        //set name
        holder.txt_contact_name.setText(contact.getName());
        //set number
        holder.txt_contact_phone.setText(contact.getPhone());
    }

    @Override
    public int getItemCount() {
        //return array list size
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //initialize variable
        TextView txt_contact_name,txt_contact_phone;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //assign variable
            txt_contact_name = itemView.findViewById(R.id.txt_contact_name);
            txt_contact_phone = itemView.findViewById(R.id.txt_contact_phone);

        }
    }
}
