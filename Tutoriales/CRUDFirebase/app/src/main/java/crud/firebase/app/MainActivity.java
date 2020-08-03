package crud.firebase.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.UUID;

import crud.firebase.app.model.Persona;

public class MainActivity extends AppCompatActivity {

    EditText input_nombre, input_apellidos, input_correo, input_contraseña;
    ListView lista_personas;

    FirebaseDatabase firebaseDB;
    DatabaseReference DBref;

    Persona personaSelected;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initFirebase();
        listData();

        lista_personas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                personaSelected = (Persona) parent.getItemAtPosition(position);
                input_nombre.setText(personaSelected.getNombre());
                input_apellidos.setText(personaSelected.getApellidos());
                input_correo.setText(personaSelected.getCorreo());
                input_contraseña.setText(personaSelected.getContraseña());
            }
        });
    }

    private void initView() {
        input_nombre = findViewById(R.id.textoNombrePersona);
        input_apellidos = findViewById(R.id.textoApellidosPersona);
        input_correo = findViewById(R.id.textoCorreoPersona);
        input_contraseña = findViewById(R.id.textoPasswordPersona);
        lista_personas = findViewById(R.id.lv_personas);
    }

    private void initFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDB = FirebaseDatabase.getInstance();
//        firebaseDB.setPersistenceEnabled(true);
        DBref = firebaseDB.getReference();
    }

    private void listData() {
        DBref.child("Usuarios").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Persona> data = new ArrayList<>();
                ArrayAdapter<Persona> arrayAdapter;

                for (DataSnapshot obj : dataSnapshot.getChildren()) {
                    Persona p = obj.getValue(Persona.class);
                    data.add(p);

                    arrayAdapter = new ArrayAdapter<Persona>(MainActivity.this, android.R.layout.simple_list_item_1, data);
                    lista_personas.setAdapter(arrayAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Persona p = new Persona();

        switch (item.getItemId()) {
            case R.id.icon_add:

                if ( (input_nombre.length() == 0) || (input_apellidos.length() == 0) ||
                     (input_correo.length() == 0) || (input_contraseña.length() == 0) ) {
                    validate();
                }
                else {
                    p.setUid(UUID.randomUUID().toString());
                    uploadUser(p);

                    Toast.makeText(this, "Usuario añadido", Toast.LENGTH_LONG).show();
                }

                break;

            case R.id.icon_save:
                p.setUid(personaSelected.getUid());
                uploadUser(p);

                Toast.makeText(this, "Usuario actualizado", Toast.LENGTH_LONG).show();
                break;

            case R.id.icon_remove:
                p = personaSelected;
                DBref.child("Usuarios").child(p.getUid()).removeValue();
                clearForm();

                Toast.makeText(this, "Usuario eliminado", Toast.LENGTH_LONG).show();
                break;
        }

        return true;
    }

    private void uploadUser(Persona p) {
        p.setNombre(input_nombre.getText().toString().trim());
        p.setApellidos(input_apellidos.getText().toString().trim());
        p.setCorreo(input_correo.getText().toString().trim());
        p.setContraseña(input_contraseña.getText().toString().trim());

        DBref.child("Usuarios").child(p.getUid()).setValue(p);

        clearForm();
    }

    private void clearForm() {
        input_nombre.setText("");
        input_apellidos.setText("");
        input_correo.setText("");
        input_contraseña.setText("");
    }

    private void validate() {
        if (input_nombre.getText().toString().equals(""))
            input_nombre.setError("Campo obligatorio");

        if (input_apellidos.getText().toString().equals(""))
            input_apellidos.setError("Campo obligatorio");

        if (input_correo.getText().toString().equals(""))
            input_correo.setError("Campo obligatorio");

        if (input_contraseña.getText().toString().equals(""))
            input_contraseña.setError("Campo obligatorio");
    }
}