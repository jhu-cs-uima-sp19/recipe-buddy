package com.example.recipebuddy;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
        boolean[] selectedArray = getArguments().getBooleanArray("selected");
        System.out.println(selectedArray.length);
        if (selectedArray.length < items.length) {
            selectedArray = null;
        } else {
            for (int i = 0; i < selectedArray.length; i++) {
                if (selectedArray[i]) {
                    list.add(items[i]);
                }
            }
        }
        builder.setTitle("Filter Recipes").setMultiChoiceItems(R.array.allergy_selection, selectedArray, new DialogInterface.OnMultiChoiceClickListener() {
            SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();

            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                if(b){
                    list.add(items[i]);
                    editor.putInt(Integer.toString(i), 1);
                    editor.commit();
                }
                else {
                    list.remove(items[i]);
                    editor.putInt(Integer.toString(i), 0);
                    editor.commit();
                }
            }
        }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();

                String selections = "";

                for(String ms : list){
                    selections = selections + "\n" + ms;
                }
                Toast.makeText(getActivity(), "Selected: " + selections, Toast.LENGTH_SHORT).show();
                listener.submitted(list);

                editor.putInt("size", items.length);
                editor.commit();
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
