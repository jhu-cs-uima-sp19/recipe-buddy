package com.example.recipebuddy;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AllergyFilterDialog extends AppCompatDialogFragment{

    ArrayList<String> list = new ArrayList<String>();
    private AllergyDialogListener listener;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String[] items = getResources().getStringArray(R.array.allergy_selection);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Filter Recipes").setMultiChoiceItems(R.array.allergy_selection, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                if(b){
                    list.add(items[i]);
                }
                else if(list.contains(items[i])){
                    list.remove(items[i]);
                }
            }
        }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String selections = "";
                for(String ms : list){
                    selections = selections + "\n" + ms;
                }
                Toast.makeText(getActivity(), "Selected: " + selections, Toast.LENGTH_SHORT).show();
                listener.submitted(list);
            }
        });
        return builder.create();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (AllergyDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement ExampleDialogListener");
        }
    }
    public interface AllergyDialogListener {
        void submitted(ArrayList<String> data);
    }


}
