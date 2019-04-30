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

public class RecipeFilterActivity extends AppCompatDialogFragment{
        private EditText editTextUsername;
        private EditText editTextPassword;
        private ExampleDialogListener listener;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.filter_popup, null);

            builder.setView(view)
                    .setTitle("Filter Recipes")
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String username = editTextUsername.getText().toString();
                            String password = editTextPassword.getText().toString();
                            listener.applyTexts(username, password);
                        }
                    });

            editTextUsername = view.findViewById(R.id.edit_username);
            editTextPassword = view.findViewById(R.id.edit_password);

            return builder.create();
        }

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);

            try {
                listener = (ExampleDialogListener) context;
            } catch (ClassCastException e) {
                throw new ClassCastException(context.toString() +
                        "must implement ExampleDialogListener");
            }
        }

        public interface ExampleDialogListener {
            void applyTexts(String username, String password);
        }
}
