package com.example.spendless;

import androidx.lifecycle.ViewModelProviders;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.spendless.model.MBAddTransaction;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddIncome extends Fragment {

    private AddIncomeViewModel mViewModel;

    public static AddIncome newInstance() {
        return new AddIncome();
    }


    private int mYear, mMonth, mDay;
    String date="";
    TextView txtDate;
    EditText edamt,eddescription;
    Spinner sp;
    String node;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference(Constants.TBL_TRANSACTIONS);
    Double amount;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.add_income_fragment, container, false);

        txtDate = root.findViewById(R.id.aifetdate);
        edamt = root.findViewById(R.id.aifTiAmount);
        eddescription = root.findViewById(R.id.aifTiDescription);
        sp = root.findViewById(R.id.spCategory);
        String c[] = {"Salary","Allowance","Gift","Interest","Business","Other"};
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(Objects.requireNonNull(getActivity()),   android.R.layout.simple_spinner_dropdown_item, c);
        sp.setAdapter(spinnerArrayAdapter);
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();



        root.findViewById(R.id.aifBtnSelectDate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(Objects.requireNonNull(getActivity()), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        date = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                        txtDate.setText(date);
                        node=(monthOfYear + 1) + "-" + year;
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();

            }
        });

        root.findViewById(R.id.aifBtnSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edamt.getText().toString().isEmpty())
                    edamt.setError("Enter Amount");
                else if (txtDate.getText().toString().isEmpty())
                    new ShowToast(getActivity(),"Please Select a date");
                else
                {
                    String date = txtDate.getText().toString();
                    amount=Double.parseDouble( edamt.getText().toString());
                    MBAddTransaction mb = new MBAddTransaction(sp.getSelectedItem().toString(),txtDate.getText().toString(),eddescription.getText().toString(),"Income",Double.parseDouble( edamt.getText().toString()));
                    myRef.child(Constants.uid).child(node).child(""+date).push().setValue(mb);

                    new ShowToast(getActivity(),"Added");

                    txtDate.setText("");
                    edamt.setText("");
                    eddescription.setText("");
                }
            }
        });

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(AddIncomeViewModel.class);
        // TODO: Use the ViewModel
    }

}
