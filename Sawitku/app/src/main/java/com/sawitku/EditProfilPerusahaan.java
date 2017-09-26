package com.sawitku;

import android.app.ProgressDialog;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import fr.ganfra.materialspinner.MaterialSpinner;

public class EditProfilPerusahaan extends AppCompatActivity {

    private FirebaseAuth auth;


    String[] SPINNERLIST = {"Produsen Benih","Produsen Sarana Produksi", "Pabrik Kelapa Sawit"};

    ArrayList<String> worldprovincelist;
    ArrayList<String> idprovincelist;


    ArrayList<String> worldkecamatanlist;
    ArrayList<String> idkecamatanlist;



    ArrayList<String> worldkabupatenlist;
    ArrayList<String> idkabupatenlist;


    String provinsi = "0";
    String nama_provinsi = "0";
    String id_provinsi_db = "0";


    String kabupaten = "0";
    String nama_kabupaten = "0";
    String id_kabupaten_db = "0";




    String kecamatan = "0";
    String nama_kecamatan = "0";
    String id_kecamatan_db = "0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profil_perusahaan);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        //get firebase auth instance
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();




        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, SPINNERLIST);
        MaterialSpinner materialDesignSpinner = (MaterialSpinner)
                findViewById(R.id.android_material_design_spinner);
        materialDesignSpinner.setAdapter(arrayAdapter);




        DatabaseReference qry = FirebaseDatabase.getInstance().getReference().child("user").child(user.getUid()).getRef();

        final ProgressDialog Dialog = new ProgressDialog(EditProfilPerusahaan.this);
        Dialog.setMessage("Please Wait...");
        Dialog.show();

        qry.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                EditText input_nama = (EditText)findViewById(R.id.input_nama);
                MaterialSpinner materialDesignSpinner = (MaterialSpinner)
                        findViewById(R.id.android_material_design_spinner);;

                if(snapshot.child("namaDisplay").exists()){
                    String nama =  snapshot.child("nama").getValue().toString();
                    input_nama.setText(nama);

                }




                if(snapshot.child("id_provinsi_db").exists()){
                    String id_provinsi =  snapshot.child("id_provinsi_db").getValue().toString();
                    id_provinsi_db = id_provinsi;

                }


                if(snapshot.child("id_kabupaten_db").exists()){
                    String id_kabupaten =  snapshot.child("id_kabupaten_db").getValue().toString();
                    id_kabupaten_db = id_kabupaten;

                }



                if(snapshot.child("id_kecamatan_db").exists()){
                    String id_kecamatan =  snapshot.child("id_kecamatan_db").getValue().toString();
                    id_kecamatan_db = id_kecamatan;

                }


                if(snapshot.child("jenis_perusahaan").exists()){
                    String jenis_perusahaan =  snapshot.child("jenis_perusahaan").getValue().toString();
                    materialDesignSpinner.setSelection(Integer.parseInt(jenis_perusahaan));

                }






                Dialog.hide();

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Log.e("Chat", "The read failed: " + error.getText());
            }
        });





        new ProvinsiAsyncTask().execute("http://188.166.177.2/indonesia/provinsi.php");





    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }





    /* Spinner Load Data Provinsi */
    class ProvinsiAsyncTask extends AsyncTask<String, Void, Boolean> {


        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(EditProfilPerusahaan.this);
            dialog.setMessage("Sedang Mengambil Data...");
            dialog.setTitle("Connecting server");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            try {

                //------------------>>
                HttpGet httppost = new HttpGet(urls[0]);
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = httpclient.execute(httppost);

                // StatusLine stat = response.getStatusLine();
                int status = response.getStatusLine().getStatusCode();

                worldprovincelist = new ArrayList<String>();
                idprovincelist = new ArrayList<String>();




                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);




                    JSONObject jsono = new JSONObject(data);
                    JSONArray jarray = jsono.getJSONArray("provinces");



                    for (int i = 0; i < jarray.length(); i++) {
                        JSONObject object = jarray.getJSONObject(i);

                        idprovincelist.add(object.getString("id"));
                        worldprovincelist.add(object.getString("name"));
                        //idlist.add(object.getString("nama"));

                    }
                    return true;
                }

                //------------------>>

            } catch (ParseException e1) {
                e1.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }

        protected void onPostExecute(Boolean result) {
            dialog.cancel();
            // adapter.notifyDataSetChanged();

            provinsi = idprovincelist.get(0);
            nama_provinsi = worldprovincelist.get(0);
            Spinner mySpinner = (Spinner) findViewById(R.id.provinsi_spinner);




            // Spinner adapter
            mySpinner
                    .setAdapter(new ArrayAdapter<String>( EditProfilPerusahaan.this,
                            android.R.layout.simple_spinner_dropdown_item,
                            worldprovincelist));


            mySpinner.setSelection(Integer.parseInt(id_provinsi_db));





            // Spinner on item click listener
            mySpinner
                    .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                        @Override
                        public void onItemSelected(AdapterView<?> arg0,
                                                   View arg1, int position, long arg3) {
                            // TODO Auto-generated method stub
                            // Locate the textviews in activity_main.xml
                            System.out.println("ID KATEGORI"+idprovincelist.get(position));

                            provinsi = idprovincelist.get(position);
                            nama_provinsi = worldprovincelist.get(position);


                            new  KabupatenAsyncTask().execute("http://188.166.177.2/indonesia/kabupaten.php?provinsi="+provinsi);





                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> arg0) {
                            // TODO Auto-generated method stub
                        }
                    });



            if(result == false)
                Toast.makeText( EditProfilPerusahaan.this, "Unable to fetch data from server", Toast.LENGTH_LONG).show();

        }
    }

     /* Spinner Load Data Provinsi */








    /* Spinner Load Data Kabupaten */
    class KabupatenAsyncTask extends AsyncTask<String, Void, Boolean> {


        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog( EditProfilPerusahaan.this);
            dialog.setMessage("Sedang Mengambil Data...");
            dialog.setTitle("Connecting server");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            try {

                //------------------>>
                HttpGet httppost = new HttpGet(urls[0]);
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = httpclient.execute(httppost);

                // StatusLine stat = response.getStatusLine();
                int status = response.getStatusLine().getStatusCode();

                worldkabupatenlist = new ArrayList<String>();
                idkabupatenlist = new ArrayList<String>();




                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);




                    JSONObject jsono = new JSONObject(data);
                    JSONArray jarray = jsono.getJSONArray("kabupaten");



                    for (int i = 0; i < jarray.length(); i++) {
                        JSONObject object = jarray.getJSONObject(i);

                        idkabupatenlist.add(object.getString("id"));
                        worldkabupatenlist.add(object.getString("name"));
                        //idlist.add(object.getString("nama"));

                    }
                    return true;
                }

                //------------------>>

            } catch (ParseException e1) {
                e1.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }

        protected void onPostExecute(Boolean result) {
            dialog.cancel();
            // adapter.notifyDataSetChanged();

            kabupaten = idkabupatenlist.get(0);
            nama_kabupaten = worldkabupatenlist.get(0);
            Spinner mySpinner = (Spinner) findViewById(R.id.kabupaten_spinner);


            // Spinner adapter
            mySpinner
                    .setAdapter(new ArrayAdapter<String>( EditProfilPerusahaan.this,
                            android.R.layout.simple_spinner_dropdown_item,
                            worldkabupatenlist));


            mySpinner.setSelection(Integer.parseInt(id_kabupaten_db));



            // Spinner on item click listener
            mySpinner
                    .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                        @Override
                        public void onItemSelected(AdapterView<?> arg0,
                                                   View arg1, int position, long arg3) {
                            // TODO Auto-generated method stub
                            // Locate the textviews in activity_main.xml
                            System.out.println("ID KATEGORI"+idkabupatenlist.get(position));

                            kabupaten = idkabupatenlist.get(position);
                            nama_kabupaten = worldkabupatenlist.get(position);

                            new  KecamatanAsyncTask().execute("http://188.166.177.2/indonesia/kecamatan.php?kabupaten="+kabupaten);




                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> arg0) {
                            // TODO Auto-generated method stub
                        }
                    });



            if(result == false)
                Toast.makeText( EditProfilPerusahaan.this, "Unable to fetch data from server", Toast.LENGTH_LONG).show();

        }
    }

     /* Spinner Load Data Kecamatan */

    class KecamatanAsyncTask extends AsyncTask<String, Void, Boolean> {


        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog( EditProfilPerusahaan.this);
            dialog.setMessage("Sedang Mengambil Data...");
            dialog.setTitle("Connecting server");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            try {

                //------------------>>
                HttpGet httppost = new HttpGet(urls[0]);
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = httpclient.execute(httppost);

                // StatusLine stat = response.getStatusLine();
                int status = response.getStatusLine().getStatusCode();

                worldkecamatanlist = new ArrayList<String>();
                idkecamatanlist = new ArrayList<String>();




                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);




                    JSONObject jsono = new JSONObject(data);
                    JSONArray jarray = jsono.getJSONArray("kecamatan");



                    for (int i = 0; i < jarray.length(); i++) {
                        JSONObject object = jarray.getJSONObject(i);

                        idkecamatanlist.add(object.getString("id"));
                        worldkecamatanlist.add(object.getString("name"));

                    }
                    return true;
                }

                //------------------>>

            } catch (ParseException e1) {
                e1.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }

        protected void onPostExecute(Boolean result) {
            dialog.cancel();
            // adapter.notifyDataSetChanged();

            kecamatan = idkecamatanlist.get(0);
            nama_kecamatan = worldkecamatanlist.get(0);
            Spinner mySpinner = (Spinner) findViewById(R.id.kecamatan_spinner);


            // Spinner adapter
            mySpinner
                    .setAdapter(new ArrayAdapter<String>( EditProfilPerusahaan.this,
                            android.R.layout.simple_spinner_dropdown_item,
                            worldkecamatanlist));


            mySpinner.setSelection(Integer.parseInt(id_kecamatan_db));



            // Spinner on item click listener
            mySpinner
                    .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                        @Override
                        public void onItemSelected(AdapterView<?> arg0,
                                                   View arg1, int position, long arg3) {
                            // TODO Auto-generated method stub
                            // Locate the textviews in activity_main.xml
                            System.out.println("ID KATEGORI"+idkecamatanlist.get(position));

                            kecamatan = idkecamatanlist.get(position);
                            nama_kecamatan = worldkecamatanlist.get(position);

                            id_provinsi_db = String.valueOf(position);

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> arg0) {
                            // TODO Auto-generated method stub
                        }
                    });










            if(result == false)
                Toast.makeText( EditProfilPerusahaan.this, "Unable to fetch data from server", Toast.LENGTH_LONG).show();

        }
    }


     /* Spinner Load Data Kecamatan */



    public void Simpan(View view) {




        final FirebaseUser user = auth.getCurrentUser();
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("user").getRef();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                MaterialSpinner materialDesignSpinner = (MaterialSpinner) findViewById(R.id.android_material_design_spinner);
                int posisi = materialDesignSpinner.getSelectedItemPosition();
                EditText input_nama = (EditText)findViewById(R.id.input_nama);
                Spinner mySpinnerProvinsi = (Spinner) findViewById(R.id.provinsi_spinner);
                Spinner mySpinnerKabupaten = (Spinner) findViewById(R.id.kabupaten_spinner);
                Spinner mySpinnerKecamatan = (Spinner) findViewById(R.id.kecamatan_spinner);
                int posisiProvinsi = mySpinnerProvinsi.getSelectedItemPosition();
                int posisiKabupaten = mySpinnerKabupaten.getSelectedItemPosition();
                int posisiKecamatan = mySpinnerKecamatan.getSelectedItemPosition();

                rootRef.child(user.getUid()).child("namaDisplay").setValue(input_nama.getText().toString());

                rootRef.child(user.getUid()).child("provinsi").setValue(nama_provinsi);
                rootRef.child(user.getUid()).child("kota").setValue(nama_kabupaten);
                rootRef.child(user.getUid()).child("kabupaten").setValue(nama_kabupaten);
                rootRef.child(user.getUid()).child("kecamatan").setValue(nama_kecamatan);


                rootRef.child(user.getUid()).child("id_provinsi").setValue(provinsi);
                rootRef.child(user.getUid()).child("id_kota").setValue(kabupaten);
                rootRef.child(user.getUid()).child("id_kabupaten").setValue(kabupaten);
                rootRef.child(user.getUid()).child("id_kecamatan").setValue(kecamatan);

                rootRef.child(user.getUid()).child("id_provinsi_db").setValue(String.valueOf(posisiProvinsi));
                rootRef.child(user.getUid()).child("id_kabupaten_db").setValue(String.valueOf(posisiKabupaten));
                rootRef.child(user.getUid()).child("id_kecamatan_db").setValue(String.valueOf(posisiKecamatan));




                rootRef.child(user.getUid()).child("jenis_perusahaan").setValue(""+posisi);
//                rootRef.child(user.getUid()).child("foto").setValue(urlFotoFirebase);
//                rootRef.child(user.getUid()).child("lat").setValue(lati);
//                rootRef.child(user.getUid()).child("log").setValue(logi);



                finish();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

}
