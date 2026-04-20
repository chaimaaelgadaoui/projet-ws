package ma.ensa.projetws;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ma.ensa.projetws.adapter.EtudiantAdapter;
import ma.ensa.projetws.beans.Etudiant;

public class ListEtudiantActivity extends AppCompatActivity {

    private ListView listView;
    private List<Etudiant> etudiantList;
    private EtudiantAdapter adapter;
    private RequestQueue requestQueue;

    private static final String loadUrl = "http://10.0.2.2/projet/ws/loadEtudiant.php";
    private static final String deleteUrl = "http://10.0.2.2/projet/ws/deleteEtudiant.php";
    private static final String updateUrl = "http://10.0.2.2/projet/ws/updateEtudiant.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_etudiant);

        listView = findViewById(R.id.list_view);
        etudiantList = new ArrayList<>();
        adapter = new EtudiantAdapter(this, etudiantList);
        listView.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(this);

        loadEtudiants();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Etudiant etudiant = etudiantList.get(position);
            showOptionsDialog(etudiant);
        });
    }

    private void loadEtudiants() {
        StringRequest request = new StringRequest(Request.Method.POST, loadUrl,
                response -> {
                    Log.d("LOAD", response);
                    Type type = new TypeToken<List<Etudiant>>(){}.getType();
                    List<Etudiant> result = new Gson().fromJson(response, type);
                    etudiantList.clear();
                    if (result != null) {
                        etudiantList.addAll(result);
                    }
                    adapter.notifyDataSetChanged();
                },
                error -> Log.e("LOAD", "Erreur : " + error.getMessage()));
        requestQueue.add(request);
    }

    private void showOptionsDialog(Etudiant etudiant) {
        String[] options = {"Modifier", "Supprimer"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Options pour " + etudiant.getNom());
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                showEditDialog(etudiant);
            } else {
                confirmDelete(etudiant);
            }
        });
        builder.show();
    }

    private void showEditDialog(Etudiant etudiant) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_etudiant, null);
        builder.setView(view);

        EditText nom = view.findViewById(R.id.edit_nom);
        EditText prenom = view.findViewById(R.id.edit_prenom);
        Spinner ville = view.findViewById(R.id.edit_ville);
        RadioButton m = view.findViewById(R.id.edit_m);
        RadioButton f = view.findViewById(R.id.edit_f);

        nom.setText(etudiant.getNom());
        prenom.setText(etudiant.getPrenom());
        // Simple index matching for ville
        for (int i = 0; i < ville.getCount(); i++) {
            if (ville.getItemAtPosition(i).toString().equalsIgnoreCase(etudiant.getVille())) {
                ville.setSelection(i);
                break;
            }
        }
        if ("femme".equalsIgnoreCase(etudiant.getSexe())) f.setChecked(true);
        else m.setChecked(true);

        builder.setPositiveButton("Enregistrer", (dialog, which) -> {
            updateEtudiant(etudiant.getId(), nom.getText().toString(), prenom.getText().toString(),
                    ville.getSelectedItem().toString(), m.isChecked() ? "homme" : "femme");
        });
        builder.setNegativeButton("Annuler", null);
        builder.show();
    }

    private void updateEtudiant(int id, String nom, String prenom, String ville, String sexe) {
        StringRequest request = new StringRequest(Request.Method.POST, updateUrl,
                response -> {
                    Toast.makeText(this, "Modifié avec succès", Toast.LENGTH_SHORT).show();
                    loadEtudiants();
                },
                error -> Log.e("UPDATE", "Erreur : " + error.getMessage())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(id));
                params.put("nom", nom);
                params.put("prenom", prenom);
                params.put("ville", ville);
                params.put("sexe", sexe);
                return params;
            }
        };
        requestQueue.add(request);
    }

    private void confirmDelete(Etudiant etudiant) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage("Voulez-vous vraiment supprimer cet étudiant ?")
                .setPositiveButton("Oui", (dialog, which) -> deleteEtudiant(etudiant.getId()))
                .setNegativeButton("Non", null)
                .show();
    }

    private void deleteEtudiant(int id) {
        StringRequest request = new StringRequest(Request.Method.POST, deleteUrl,
                response -> {
                    Toast.makeText(this, "Supprimé avec succès", Toast.LENGTH_SHORT).show();
                    loadEtudiants();
                },
                error -> Log.e("DELETE", "Erreur : " + error.getMessage())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(id));
                return params;
            }
        };
        requestQueue.add(request);
    }
}