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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import yadav.srinivasa.chatterbox.R;
import yadav.srinivasa.chatterbox.logActivity;


public class RegistrationFragment extends Fragment {

    EditText username, email, password;
    Button btn_register;
    ProgressBar prgBr;

    private ProgressDialog progressDialog;

    FirebaseAuth auth;
    DatabaseReference reference;

    public RegistrationFragment() {
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
        View view = inflater.inflate(R.layout.fragment_registration, container, false);

        username = view.findViewById(R.id.username);
        prgBr = view.findViewById(R.id.prgBr);
        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        btn_register = view.findViewById(R.id.btn_register);

        auth = FirebaseAuth.getInstance();


        progressDialog = new ProgressDialog(getActivity());

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt_username = username.getText().toString();
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();
                if (TextUtils.isEmpty(txt_username) || TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)) {
                    Toast.makeText(getContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                } else if (txt_password.length() < 6) {
                    Toast.makeText(getContext(), "password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                } else {

                    prgBr.setVisibility(View.VISIBLE);
                    progressDialog.setTitle("Registering User");
                    progressDialog.setMessage("Please wait \nSending verification mail");
                    progressDialog.setCanceledOnTouchOutside(true);
                    progressDialog.show();


                    register(txt_username, txt_email, txt_password);
                }
            }
        });
        return view;
    }


        private void register ( final String username, String email, String password){
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser firebaseUser = auth.getCurrentUser();
                                assert firebaseUser != null;
                                String userid = firebaseUser.getUid();
                                reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

                                HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put("id", userid);
                                hashMap.put("username", username);
                                hashMap.put("imageURL", "default");
                                hashMap.put("status", "offline");
                                hashMap.put("search", username.toLowerCase());

                                reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {


                                                    FirebaseAuth.getInstance().signOut();

                                                    Intent intent = new Intent(getActivity(), logActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT);
                                                    startActivity(intent);
                                                    getActivity().finish();


                                                    // Method to hide/ dismiss Progress bar
                                                    //prgBr.setVisibility(View.INVISIBLE);
                                                    progressDialog.dismiss();


                                                    Toast.makeText(getContext(), "Verification email has been sent to your mail " +
                                                            "please verify and login!", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }
                                });
                            } else {

                                //prgBr.setVisibility(View.INVISIBLE);
                                progressDialog.dismiss();

                                Toast.makeText(getContext(), "You can't register with this email or password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }



}

