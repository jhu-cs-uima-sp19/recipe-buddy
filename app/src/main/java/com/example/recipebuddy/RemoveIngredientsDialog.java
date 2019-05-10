package com.example.recipebuddy;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.widget.Toast;

import java.util.ArrayList;

public class RemoveIngredientsDialog extends AppCompatDialogFragment {
    ArrayList<String> list = new ArrayList<String>();
    private RemoveIngredientsDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        KitchenDBHandler kitchenDBHandler = new KitchenDBHandler(getContext());
        SQLiteDatabase kitchenDB = kitchenDBHandler.getReadableDatabase();

        Bundle b = getArguments();

        String[] reqIngredients = b.getStringArray("ingredients");
        ArrayList<String> temp = new ArrayList<>();

        for (int i = 0; i < reqIngredients.length; i++) {
            String[] args = {reqIngredients[i]};
            Cursor c = kitchenDB.query(
                    DBConstants.KitchenColumns.TABLE_NAME,
                    null,
                    DBConstants.KitchenColumns.COLUMN_NAME + "=?",
                    args,
                    null,
                    null,
                    null
            );
            if (c.getCount() > 0) {
                temp.add(reqIngredients[i]);
            }
        }
        final String[] items = temp.toArray(new String[0]);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Remove from ingredients list?").setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                if(b){
                    list.add(items[i]);
                }
                else {
                    list.remove(items[i]);
                }
            }
        }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String selections = "";

                for(String ms : list){
                    if (selections == "") {
                        selections = ms.toLowerCase();
                    } else {
                        selections = selections + ", " + ms.toLowerCase();
                    }
                }
                if (list.size() > 0) {
                    Toast.makeText(getActivity(), "Removed " + selections + " from list", Toast.LENGTH_SHORT).show();
                }
                listener.submitted(list);
            }
        });
        return builder.create();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (RemoveIngredientsDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement ExampleDialogListener");
        }
    }

    public interface RemoveIngredientsDialogListener {
        void submitted(ArrayList<String> data);
    }


}
