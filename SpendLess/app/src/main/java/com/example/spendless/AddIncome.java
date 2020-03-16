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
    DatabaseReference myref = database.getReference(Constants.TBL_USER_DATA);
    Double income,expense,amount;



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
                        if (dayOfMonth>mDay || monthOfYear>mMonth || year > mYear)
                            txtDate.setText(date);
                        else {
                            txtDate.setText(mDay + "-" + (mMonth + 1) + "-" + mYear);
                            node=(mMonth + 1) + "-" + mYear;
                        }
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
                else
                {
                    amount=Double.parseDouble( edamt.getText().toString());
                    MBAddTransaction mb = new MBAddTransaction(sp.getSelectedItem().toString(),txtDate.getText().toString(),eddescription.getText().toString(),"Income",Double.parseDouble( edamt.getText().toString()));
                    myRef.child(Constants.uid).child(node).child(txtDate.getText().toString()).push().setValue(mb);



                    final Query getTotalQuery = FirebaseDatabase.getInstance().getReference(Constants.TBL_USER_DATA).child(Constants.uid);
                    getTotalQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        Map<String, Object> updates = new HashMap<String, Object>();
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                           income = Double.parseDouble( ""+dataSnapshot.child("totalincome").getValue());
                           expense = Double.parseDouble( ""+dataSnapshot.child("totalexpense").getValue());
                            updates.put("rating", Math.round(income/expense));
                            updates.put("totalincome", Math.round(income+amount));
                            myref.child(Constants.uid).updateChildren(updates);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


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
