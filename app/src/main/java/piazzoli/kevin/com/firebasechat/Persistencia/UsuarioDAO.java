package piazzoli.kevin.com.firebasechat.Persistencia;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import piazzoli.kevin.com.firebasechat.Entidades.Firebase.Usuario;
import piazzoli.kevin.com.firebasechat.Entidades.Logica.LUsuario;
import piazzoli.kevin.com.firebasechat.Utilidades.Constantes;

/**
 * Created by user on 28/08/2018. 28
 */
public class UsuarioDAO {

    public interface IDevolverUsuario{
        public void devolverUsuario(LUsuario lUsuario);
        public void devolverError(String error);
    }

    public interface IDevolverUrlFoto{
        public void devolerUrlString(String url);
    }

    private static UsuarioDAO usuarioDAO;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private DatabaseReference referenceUsuarios;
    private StorageReference referenceFotoDePerfil;

    public static UsuarioDAO getInstancia(){
        if(usuarioDAO==null) usuarioDAO = new UsuarioDAO();
        return usuarioDAO;
    }

    private UsuarioDAO(){
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        referenceUsuarios = database.getReference(Constantes.NODO_USUARIOS);
        referenceFotoDePerfil = storage.getReference("Fotos/FotoPerfil/"+getKeyUsuario());
    }

    public String getKeyUsuario(){
        return FirebaseAuth.getInstance().getUid();
    }

    public boolean isUsuarioLogeado(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        return firebaseUser!=null;
    }

    public long fechaDeCreacionLong(){
        return FirebaseAuth.getInstance().getCurrentUser().getMetadata().getCreationTimestamp();
    }

    public long fechaDeUltimaVezQueSeLogeoLong(){
        return FirebaseAuth.getInstance().getCurrentUser().getMetadata().getLastSignInTimestamp();
    }

    public void obtenerInformacionDeUsuarioPorLLave(final String key, final IDevolverUsuario iDevolverUsuario){
        referenceUsuarios.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                LUsuario lUsuario = new LUsuario(key,usuario);
                iDevolverUsuario.devolverUsuario(lUsuario);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                iDevolverUsuario.devolverError(databaseError.getMessage());
            }
        });

    }

    public void añadirFotoDePerfilALosUsuariosQueNoTienenFoto(){
        referenceUsuarios.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<LUsuario> lUsuariosLista = new ArrayList<>();
                for(DataSnapshot childDataSnapShot : dataSnapshot.getChildren()){
                    Usuario usuario = childDataSnapShot.getValue(Usuario.class);
                    LUsuario lUsuario = new LUsuario(childDataSnapShot.getKey(),usuario);
                    lUsuariosLista.add(lUsuario);
                }

                for(LUsuario lUsuario : lUsuariosLista){
                    if(lUsuario.getUsuario().getFotoPerfilURL()==null){
                        referenceUsuarios.child(lUsuario.getKey()).child("fotoPerfilURL").setValue(Constantes.URL_FOTO_POR_DEFECTO_USUARIOS);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void subirFotoUri(Uri uri, final IDevolverUrlFoto iDevolverUrlFoto){
        String nombreFoto = "";
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("SSS.ss-mm-hh-dd-MM-yyyy", Locale.getDefault());
        nombreFoto = simpleDateFormat.format(date);
        final StorageReference fotoReferencia = referenceFotoDePerfil.child(nombreFoto);
        fotoReferencia.putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if(!task.isSuccessful()){
                    throw task.getException();
                }
                return fotoReferencia.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()){
                    Uri uri = task.getResult();
                    iDevolverUrlFoto.devolerUrlString(uri.toString());
                }
            }
        });
    }

}
