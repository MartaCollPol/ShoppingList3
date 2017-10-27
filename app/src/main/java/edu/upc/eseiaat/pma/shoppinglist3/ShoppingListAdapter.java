package edu.upc.eseiaat.pma.shoppinglist3;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import java.util.List;

/**
 * Created by Marta on 23/10/2017.
 */

public class ShoppingListAdapter extends ArrayAdapter<ShoppingItem> {
    public ShoppingListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View result = convertView; // convertview = item reciclat
        if(result == null){ // si no hi ha cap item reciclat, creem nosaltres l'item següent
            // layoutinflater retorna un view
            LayoutInflater inflater = LayoutInflater.from(getContext()); //obtenim el context, és a dir l'activitat en la que treballem
            result = inflater.inflate(R.layout.shopping_item, null);
        }
        // Busquem el checkbox dins de l'item de la llista que hem trobat, result és un view.
        CheckBox checkbox = (CheckBox) result.findViewById(R.id.shopping_item);
        ShoppingItem item = getItem(position); // (String) és necessàri per a passar getItem a string en cas que no heredem l'adaptador d'ArrayAdapter<String>
        //Assignem al checkbox el text extret
        checkbox.setText(item.getText());
        checkbox.setChecked(item.isChecked()); //comprova si l'item esta chequejat
        return result;
    }

}
