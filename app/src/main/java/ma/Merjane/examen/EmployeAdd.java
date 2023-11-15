package ma.Merjane.examen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EmployeAdd extends AppCompatActivity implements View.OnClickListener {

    private EditText nom;
    private EditText prenom;
    private EditText dateNaissance;
    private Spinner spinnerService;
    private Button bnAdd;
    private Button btnListeEmployes;
    private RequestQueue requestQueue;
    private ArrayAdapter<String> spinnerAdapter;
    private List<String> serviceList;

    private String insertUrl = "http://10.0.2.2:8082/api/employe";
    private String baseUrl = "http://10.0.2.2:8082/api/services";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employe_add);

        nom = findViewById(R.id.nom);
        prenom = findViewById(R.id.prenom);
        dateNaissance = findViewById(R.id.dateNaissance);

        bnAdd = findViewById(R.id.bnAdd);
        bnAdd.setOnClickListener(this);

        btnListeEmployes = findViewById(R.id.btnListeEmployes);
        btnListeEmployes.setOnClickListener(this);

        spinnerService = findViewById(R.id.spinnerService);
        serviceList = new ArrayList<>();
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, serviceList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerService.setAdapter(spinnerAdapter);

        loadServiceList();
    }

    private void loadServiceList() {
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, baseUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        serviceList.clear();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject serviceObject = response.getJSONObject(i);
                                String libelle = serviceObject.getString("libelle");
                                serviceList.add(libelle);
                            }
                            spinnerAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("Erreur", "Erreur lors de l'analyse JSON : " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Erreur", "Erreur de demande : " + error.toString());
                    }
                });

        requestQueue.add(jsonArrayRequest);
    }

    @Override
    public void onClick(View view) {
        if (view == bnAdd) {
            String selectedService = spinnerService.getSelectedItem().toString();

            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("Nom", nom.getText().toString());
                jsonBody.put("Email", prenom.getText().toString());
                jsonBody.put("date de naissance", dateNaissance.getText().toString());
                jsonBody.put("Service", selectedService);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            requestQueue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                    insertUrl, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("resultat", response + "");
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Erreur", error.toString());
                }
            });
            requestQueue.add(request);
        } else if (view == btnListeEmployes) {
            Intent intent = new Intent(EmployeAdd.this, EmployeGestion.class);
            startActivity(intent);
        }
    }
}
