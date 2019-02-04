package piazzoli.kevin.com.firebasechat.Entidades.Logica;

import com.google.firebase.database.Exclude;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import piazzoli.kevin.com.firebasechat.Entidades.Firebase.Usuario;
import piazzoli.kevin.com.firebasechat.Persistencia.UsuarioDAO;

/**
 * Created by user on 28/08/2018. 28
 */
public class LUsuario {

    private String key;
    private Usuario usuario;

    public LUsuario(String key, Usuario usuario) {
        this.key = key;
        this.usuario = usuario;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String obtenerFechaDeCreacion(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date date = new Date(UsuarioDAO.getInstancia().fechaDeCreacionLong());
        return simpleDateFormat.format(date);
    }

    public String obtenerFechaDeUltimaVezQueSeLogeo(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date date = new Date(UsuarioDAO.getInstancia().fechaDeUltimaVezQueSeLogeoLong());
        return simpleDateFormat.format(date);
    }

}
