package piazzoli.kevin.com.firebasechat.Holder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import piazzoli.kevin.com.firebasechat.R;

public class UsuarioViewHolder extends RecyclerView.ViewHolder {

    private CircleImageView civFotoPerfil;
    private TextView txtNombreUsuario;
    private LinearLayout layoutPrincipal;

    public UsuarioViewHolder(@NonNull View itemView) {
        super(itemView);
        civFotoPerfil = itemView.findViewById(R.id.civFotoPerfil);
        txtNombreUsuario =itemView.findViewById(R.id.txtNombreUsuario);
        layoutPrincipal = itemView.findViewById(R.id.layoutPrincipal);
    }

    public CircleImageView getCivFotoPerfil() {
        return civFotoPerfil;
    }

    public void setCivFotoPerfil(CircleImageView civFotoPerfil) {
        this.civFotoPerfil = civFotoPerfil;
    }

    public TextView getTxtNombreUsuario() {
        return txtNombreUsuario;
    }

    public void setTxtNombreUsuario(TextView txtNombreUsuario) {
        this.txtNombreUsuario = txtNombreUsuario;
    }

    public LinearLayout getLayoutPrincipal() {
        return layoutPrincipal;
    }

    public void setLayoutPrincipal(LinearLayout layoutPrincipal) {
        this.layoutPrincipal = layoutPrincipal;
    }
}
