package Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import yadav.srinivasa.chatterbox.MainActivity;
import yadav.srinivasa.chatterbox.R;
import yadav.srinivasa.chatterbox.ResetPasswordActivity;

public class LoginFragment extends Fragment {

    EditText email, password;
    Button btn_login;
    ProgressBar prgBr;

    FirebaseAuth auth;
    TextView forgot_password;
   // ProgressDialog progressDialog;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);


        prgBr = view.findViewById(R.id.prgBr);


        auth = FirebaseAuth.getInstance();

        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        btn_login = view.findViewById(R.id.btn_login);
        forgot_password = view.findViewById(R.id.forgot_password);


        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ResetPasswordActivity.class));
            }
        });


        btn_login.setOnClickListener(new View.OnClickListener() {
            ProgressDialog progressDialog = new ProgressDialog(getActivity());
            @Override
            public void onClick(View v) {
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();

                if (TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)) {
                    Toast.makeText(getContext(), "All fileds are required", Toast.LENGTH_SHORT).show();
                } else {


                    //prgBr.setVisibility(View.VISIBLE);
                    progressDialog.setTitle("Verifying user");
                    progressDialog.setMessage("Please wait");
                    progressDialog.setCanceledOnTouchOutside(true);
                    progressDialog.show();



                    auth.signInWithEmailAndPassword(txt_email, txt_password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        if (auth.getCurrentUser().isEmailVerified()) {
                                            progressDialog.dismiss();
                                            Intent intent = new Intent(getActivity(), MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            getActivity().finish();
                                        } else {
                                            //prgBr.setVisibility(View.INVISIBLE);
                                            progressDialog.dismiss();
                                            Toast.makeText(getContext(), "Email not verified!", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        progressDialog.dismiss();
                                        //prgBr.setVisibility(View.INVISIBLE);
                                        Toast.makeText(getContext(), "Authentication failed!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });


        return view;
    }

}