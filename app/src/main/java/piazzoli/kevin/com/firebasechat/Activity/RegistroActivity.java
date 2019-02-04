package piazzoli.kevin.com.firebasechat.Activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kbeanie.multipicker.api.CacheLocation;
import com.kbeanie.multipicker.api.CameraImagePicker;
import com.kbeanie.multipicker.api.ImagePicker;
import com.kbeanie.multipicker.api.Picker;
import com.kbeanie.multipicker.api.callbacks.ImagePickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenImage;

import java.io.File;
import java.security.acl.Group;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import piazzoli.kevin.com.firebasechat.Entidades.Firebase.Usuario;
import piazzoli.kevin.com.firebasechat.Persistencia.UsuarioDAO;
import piazzoli.kevin.com.firebasechat.R;
import piazzoli.kevin.com.firebasechat.Utilidades.Constantes;

/**
 * Created by user on 19/02/2018. 19
 */

public class RegistroActivity extends AppCompatActivity {

    private CircleImageView fotoPerfil;
    private EditText txtNombre;
    private EditText txtCorreo;
    private EditText txtContraseña;
    private EditText txtContraseñaRepetida;
    private EditText txtFechaDeNacimiento;
    private RadioButton rdHombre;
    private RadioButton rdMujer;
    private Button btnRegistrar;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;

    private ImagePicker imagePicker;
    private CameraImagePicker cameraPicker;

    private String pickerPath;
    private Uri fotoPerfilUri;
    private long fechaDeNacimiento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        fotoPerfil = findViewById(R.id.fotoPerfil);
        txtNombre = findViewById(R.id.idRegistroNombre);
        txtCorreo = findViewById(R.id.idRegistroCorreo);
        txtContraseña = findViewById(R.id.idRegistroContraseña);
        txtContraseñaRepetida = findViewById(R.id.idRegistroContraseñaRepetida);
        txtFechaDeNacimiento = findViewById(R.id.txtFechaDeNacimiento);
        rdHombre = findViewById(R.id.rdHombre);
        rdMujer = findViewById(R.id.rdMujer);
        btnRegistrar = findViewById(R.id.idRegistroRegistrar);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        imagePicker = new ImagePicker(this);
        cameraPicker = new CameraImagePicker(this);

        cameraPicker.setCacheLocation(CacheLocation.EXTERNAL_STORAGE_APP_DIR);

        imagePicker.setImagePickerCallback(new ImagePickerCallback() {
            @Override
            public void onImagesChosen(List<ChosenImage> list) {
                if(!list.isEmpty()){
                    String path = list.get(0).getOriginalPath();
                    fotoPerfilUri = Uri.parse(path);
                    fotoPerfil.setImageURI(fotoPerfilUri);
                }
            }

            @Override
            public void onError(String s) {
                Toast.makeText(RegistroActivity.this, "Error: "+s, Toast.LENGTH_SHORT).show();
            }
        });

        cameraPicker.setImagePickerCallback(new ImagePickerCallback() {
            @Override
            public void onImagesChosen(List<ChosenImage> list) {
                String path = list.get(0).getOriginalPath();
                fotoPerfilUri = Uri.fromFile(new File(path));
                fotoPerfil.setImageURI(fotoPerfilUri);
            }

            @Override
            public void onError(String s) {
                Toast.makeText(RegistroActivity.this, "Error: "+s, Toast.LENGTH_SHORT).show();
            }
        });

        fotoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(RegistroActivity.this);
                dialog.setTitle("Foto de perfil");

                String[] items = {"Galeria","Camara"};

                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                imagePicker.pickImage();
                                break;
                            case 1:
                                pickerPath = cameraPicker.pickImage();
                                break;
                        }
                    }
                });

                AlertDialog dialogConstruido = dialog.create();
                dialogConstruido.show();

            }
        });

        txtFechaDeNacimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(RegistroActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int mes, int dia) {
                        Calendar calendarResultado = Calendar.getInstance();
                        calendar.set(Calendar.YEAR,year);
                        calendar.set(Calendar.MONTH,mes);
                        calendar.set(Calendar.DAY_OF_MONTH,dia);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        Date date = calendarResultado.getTime();
                        String fechaDeNacimientoTexto = simpleDateFormat.format(date);
                        fechaDeNacimiento = date.getTime();
                        txtFechaDeNacimiento.setText(fechaDeNacimientoTexto);
                    }
                },calendar.get(Calendar.YEAR)-18,calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String correo = txtCorreo.getText().toString();
                final String nombre = txtNombre.getText().toString();
                if(isValidEmail(correo) && validarContraseña() && validarNombre(nombre)){
                    String contraseña = txtContraseña.getText().toString();
                    mAuth.createUserWithEmailAndPassword(correo, contraseña)
                            .addOnCompleteListener(RegistroActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        final String genero;

                                        if(rdHombre.isChecked()){
                                            genero = "Hombre";
                                        }else{
                                            genero = "Mujer";
                                        }

                                        if(fotoPerfilUri!=null) {

                                            UsuarioDAO.getInstancia().subirFotoUri(fotoPerfilUri, new UsuarioDAO.IDevolverUrlFoto() {
                                                @Override
                                                public void devolerUrlString(String url) {
                                                    Toast.makeText(RegistroActivity.this, "Se registro correctamente.", Toast.LENGTH_SHORT).show();
                                                    Usuario usuario = new Usuario();
                                                    usuario.setCorreo(correo);
                                                    usuario.setNombre(nombre);
                                                    usuario.setFechaDeNacimiento(fechaDeNacimiento);
                                                    usuario.setGenero(genero);
                                                    usuario.setFotoPerfilURL(url);
                                                    FirebaseUser currentUser = mAuth.getCurrentUser();
                                                    DatabaseReference reference = database.getReference("Usuarios/" + currentUser.getUid());
                                                    reference.setValue(usuario);
                                                    finish();
                                                }
                                            });

                                        }else{
                                            Toast.makeText(RegistroActivity.this, "Se registro correctamente.", Toast.LENGTH_SHORT).show();
                                            Usuario usuario = new Usuario();
                                            usuario.setCorreo(correo);
                                            usuario.setNombre(nombre);
                                            usuario.setFechaDeNacimiento(fechaDeNacimiento);
                                            usuario.setGenero(genero);
                                            usuario.setFotoPerfilURL(Constantes.URL_FOTO_POR_DEFECTO_USUARIOS);
                                            FirebaseUser currentUser = mAuth.getCurrentUser();
                                            DatabaseReference reference = database.getReference("Usuarios/"+currentUser.getUid());
                                            reference.setValue(usuario);
                                            finish();
                                        }

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(RegistroActivity.this, "Error al registrarse.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }else{
                    Toast.makeText(RegistroActivity.this, "Validaciones funcionando.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Glide.with(this).load(Constantes.URL_FOTO_POR_DEFECTO_USUARIOS).into(fotoPerfil);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Picker.PICK_IMAGE_DEVICE && resultCode == RESULT_OK){
            imagePicker.submit(data);
        }else if(requestCode == Picker.PICK_IMAGE_CAMERA && resultCode == RESULT_OK){
            cameraPicker.reinitialize(pickerPath);
            cameraPicker.submit(data);
        }
    }

    private boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public boolean validarContraseña(){
        String contraseña,contraseñaRepetida;
        contraseña = txtContraseña.getText().toString();
        contraseñaRepetida = txtContraseñaRepetida.getText().toString();
        if(contraseña.equals(contraseñaRepetida)){
            if(contraseña.length()>=6 && contraseña.length()<=16){
                return true;
            }else return false;
        }else return false;
    }

    public boolean validarNombre(String nombre){
        return !nombre.isEmpty();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // You have to save path in case your activity is killed.
        // In such a scenario, you will need to re-initialize the CameraImagePicker
        outState.putString("picker_path", pickerPath);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // After Activity recreate, you need to re-intialize these
        // two values to be able to re-intialize CameraImagePicker
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("picker_path")) {
                pickerPath = savedInstanceState.getString("picker_path");
            }
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

}
