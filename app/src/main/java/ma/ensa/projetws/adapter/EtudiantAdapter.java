package ma.ensa.projetws.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import ma.ensa.projetws.R;
import ma.ensa.projetws.beans.Etudiant;

public class EtudiantAdapter extends BaseAdapter {
    private Context context;
    private List<Etudiant> etudiants;
    private LayoutInflater inflater;

    public EtudiantAdapter(Context context, List<Etudiant> etudiants) {
        this.context = context;
        this.etudiants = etudiants;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return etudiants.size();
    }

    @Override
    public Object getItem(int position) {
        return etudiants.get(position);
    }

    @Override
    public long getItemId(int position) {
        return etudiants.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_etudiant, parent, false);
        }

        TextView nomPrenom = convertView.findViewById(R.id.nom_prenom);
        TextView villeSexe = convertView.findViewById(R.id.ville_sexe);

        Etudiant etudiant = etudiants.get(position);
        nomPrenom.setText(etudiant.getNom() + " " + etudiant.getPrenom());
        villeSexe.setText(etudiant.getVille() + " - " + etudiant.getSexe());

        return convertView;
    }
}