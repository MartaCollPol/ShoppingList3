package edu.upc.eseiaat.pma.shoppinglist3;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ShoppingListActivity extends AppCompatActivity {
    // static:és com una variable global, hi ha una sola copia d'aquesta dada per a tots els objectes de la classe.
    private static final String FILENAME = "shopping_list.txt";
    private static final int MAX_BYTES = 8000; // valor que suposem que mai excedirem

    private ArrayList<ShoppingItem> itemlist;
    private ShoppingListAdapter adapter;

    private ListView list;
    private Button btn_add;
    private EditText edit_item;

    //buscar la documentació com escriure en un fitxer.
    private void writeItemList(){

        try {
            FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            for(int i = 0; i < itemlist.size(); i++){
                ShoppingItem it = itemlist.get(i);
                String line = String.format("%s;%b\n", it.getText(), it.isChecked());
                fos.write(line.getBytes());
            }
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e("mcoll","writeItemList: FileNotFoundException");
            Toast.makeText(this,R.string.cannot_write,Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e("mcoll","writeItemList: IOException");
            Toast.makeText(this,R.string.cannot_write,Toast.LENGTH_SHORT).show();
        }
    }
    //Soluciona el problema de la rotació del dispositiu.
    private void readItemList(){
        itemlist = new ArrayList<>();
        try {
            FileInputStream fis = openFileInput(FILENAME);
            byte[] buffer = new byte[MAX_BYTES];
            int nread = fis.read(buffer);// numero de bytes llegits.
            if(nread>0){
                String content = new String(buffer,0,nread); //comencem en 0 fins al byte nread
                String[] lines = content.split("\n"); //Array d'strings del fitxer llegit seperades per un \n
                for (String line : lines) { //Aquest tipus de for passa per a cada linia de l'array de linies
                    // Cada linia (ShoppingItem) té 2 parts seperades per un ;, la primera part és el nom i la segona un boolean "true/false";
                    String[] parts = line.split(";");
                    itemlist.add(new ShoppingItem(parts[0], parts[1].equals("true"))); //Afegim al itemlist els ShoppingItems llegits.
                }
            }
            fis.close();

        } catch (FileNotFoundException e) {
            //tenir en compte que quan s'executi l'app per primer cop saltarà aquesta excepció per això només hi posem un Log.
            Log.e("mcoll","readItemList: FileNotFoundException");

        } catch (IOException e) {
            Log.e("mcoll","readItemList: IOException");
            Toast.makeText(this,R.string.cannot_read,Toast.LENGTH_SHORT).show();
        }
    }
    //Quan li donem al boto home o al de gestionar les apps del mòbil s'activa el mètode onStop, aqui és quan guardem la llista que tenim en els fitxers.
    @Override
    protected void onStop(){
        super.onStop();
        writeItemList();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        //Per crear variables privades directament un cop declarades com a locals
        //ens posem a sobre del nom d'aquesta i li donem a ctrl+alt+f i a current method.

        list = (ListView) findViewById(R.id.list);
        btn_add = (Button) findViewById(R.id.btn_add);
        edit_item = (EditText)findViewById(R.id.edit_item);

        readItemList();

        // Cada item de la llista té el seu layout en aquest cas per a un ArrayAdapter s'utilitza simple_list_item_1 layout.
        //Per a canviar a un layout personalitzat necesitem fer el nostre propi adaptador per a que controli el nou layout.
        adapter = new ShoppingListAdapter(this, android.R.layout.simple_list_item_1, itemlist);


        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });

        edit_item.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                addItem();
                return false;
            }
        });

        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id){
                //volem que al treure un Check es guardi que l'item de la posició pos no estar checked
                itemlist.get(pos).toggleChecked();// get invoca el getter de la classe shoping list, igual que togglecheck invoca aquest mètode.
                adapter.notifyDataSetChanged();
            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> list, View item, int pos, long id) {
                maybeRemoveItem(pos);
                return false;
            }
        });

    }

    private void maybeRemoveItem(final int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirm);
        String s = getResources().getString(R.string.confirm_message);
        builder.setMessage(String.format(s,itemlist.get(pos).getText()));
        builder.setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                itemlist.remove(pos);
                adapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.create().show();

    }

    private void addItem() {
        String item_text = edit_item.getText().toString();
        if(!item_text.isEmpty()){
            itemlist.add(new ShoppingItem(item_text));
            adapter.notifyDataSetChanged();
            edit_item.setText("");
        }
        //métode que fa que al afagir un item es fagi un autoscroll per a veure que s'ha afegit quelcom.
        list.smoothScrollToPosition(itemlist.size()-1);

        //TODO: solucionar que es guardin les coses al rotar el dispositiu i que els elements es guardin al apagar el dispositiu.

    }
    //Al crear un menú utilitzem l'inflador de menus per a crear els objectes necessàris "menu items" donada una descripció .xml
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.clear_checked:
                clearChecked();
                return true;
            case R.id.clear_all:
                clearAll();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void clearAll() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirm);
        builder.setMessage(R.string.confimr_clear_all);
        builder.setPositiveButton(R.string.clear_all, new DialogInterface.OnClickListener(){
            @Override
            public  void onClick(DialogInterface dialog, int which){
                itemlist.clear();
                adapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton(android.R.string.cancel,null);
        builder.create().show();
    }

    private void clearChecked() {
        int i =0;
        while( i< itemlist.size()){
            if(itemlist.get(i).isChecked()){
                itemlist.remove(i);
            } else{
                i++; //al borrar no fem i++ ja que sino ens saltem una posició.
            }
        }
        adapter.notifyDataSetChanged();
    }
}
