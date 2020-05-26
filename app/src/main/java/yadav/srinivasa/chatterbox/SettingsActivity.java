package yadav.srinivasa.chatterbox;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import yadav.srinivasa.chatterbox.Model.User;

public class SettingsActivity extends AppCompatActivity {

    CircleImageView image_profile;
    Button delUsr;
    TextView username;

    DatabaseReference reference;
    FirebaseUser fuser;

    StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        image_profile = findViewById(R.id.profile_image);
        delUsr = findViewById(R.id.delUsr);
        username = findViewById(R.id.username);

        storageReference = FirebaseStorage.getInstance().getReference("uploads");



        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // and this
                startActivity(new Intent(SettingsActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });


        //for deleting user
        delUsr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(SettingsActivity.this);
                dialog.setTitle("Are you sure?");
                dialog.setMessage("As soon as you click the send button you will be logged out of the app." +
                        "\n\nPlease do not login with same account details as we will be processing your request"+
                        "\n\nOur team will respond to your mail.");
                dialog.setPositiveButton("Send request", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        /*fuser.delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(SettingsActivity.this,"Account successfully deleted",Toast.LENGTH_LONG).show();
                                            //for redirecting
                                            FirebaseAuth.getInstance().signOut();
                                            //delUsrdb(reference);
                                            Intent intent = new Intent(SettingsActivity.this , logActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT | Intent.FLAG_ACTIVITY_NO_HISTORY);
                                            startActivity(intent);
                                            SettingsActivity.this.finish();

                                        }else{
                                            Toast.makeText(SettingsActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });*/
                        FirebaseAuth.getInstance().signOut();
                        final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT);
                        String[] recipients={"sriyadav98@gmail.com"};
                        intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                        intent.putExtra(Intent.EXTRA_SUBJECT,"Request for deleting my account");
                        intent.putExtra(Intent.EXTRA_TEXT,"My account details :-\n\n\nUsername:   \n\nemail-id:   ");
                        intent.setType("text/plain");
                        final PackageManager pm = getPackageManager();
                        final List<ResolveInfo> matches = pm.queryIntentActivities(intent, 0);
                        ResolveInfo best = null;
                        for (final ResolveInfo info : matches)
                            if (info.activityInfo.packageName.endsWith(".gm") ||
                                    info.activityInfo.name.toLowerCase().contains("gmail")) best = info;
                        if (best != null)
                            intent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
                        startActivity(intent);
                        SettingsActivity.this.finishAffinity();

                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(SettingsActivity.this, "Good choice!", Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog alertDialog = dialog.create();
                alertDialog.show();


            }
        });
        //till here is delete user


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                if (getApplicationContext() == null) {
                    return;
                }
                if (user.getImageURL().equals("default")){
                    image_profile.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(image_profile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImage();
            }
        });

    }

    /* private void delUsrdb(DatabaseReference reference) {

        DatabaseReference usrdb = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        DatabaseReference tkdb = FirebaseDatabase.getInstance().getReference("Tokens").child(fuser.getUid());
        DatabaseReference chdb = FirebaseDatabase.getInstance().getReference("Chats").child(fuser.getUid());
        DatabaseReference chldb = FirebaseDatabase.getInstance().getReference("Chatlist").child(fuser.getUid());
        chldb.removeValue();
        chdb.removeValue();
        tkdb.removeValue();
        usrdb.removeValue();

    } */

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = SettingsActivity.this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK) {
            imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(this);
        }
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    final Uri resultUri = result.getUri();

                    File bitfile = new File(resultUri.getPath());

                                final ProgressDialog pd = new ProgressDialog(SettingsActivity.this);
                                pd.setMessage("Uploading");
                                pd.show();

                                if (resultUri != null){
                                    fuser = FirebaseAuth.getInstance().getCurrentUser();
                                    final  StorageReference fileReference = storageReference.child(fuser +(".jpg"));

                                    uploadTask = fileReference.putFile(resultUri);
                                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                        @Override
                                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                            if (!task.isSuccessful()){
                                                throw  task.getException();
                                            }

                                            return  fileReference.getDownloadUrl();
                                        }
                                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            if (task.isSuccessful()){
                                                Uri downloadUri = task.getResult();
                                                String mUri = downloadUri.toString();

                                                reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
                                                HashMap<String, Object> map = new HashMap<>();
                                                map.put("imageURL", ""+mUri);
                                                reference.updateChildren(map);

                                                pd.dismiss();
                                            } else {
                                                Toast.makeText(SettingsActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                                pd.dismiss();
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(SettingsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            pd.dismiss();
                                        }
                                    });
                                } else {
                                    Toast.makeText(SettingsActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
                                }
                    }
                else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();

                }
            }

            /*if (uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(SettingsActivity.this, "Upload in preogress", Toast.LENGTH_SHORT).show();
            } else {
                uploadImage();
            }*/

    }
}
