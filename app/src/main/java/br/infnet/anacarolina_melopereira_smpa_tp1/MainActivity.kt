package br.infnet.anacarolina_melopereira_smpa_tp1

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*
import java.util.*

class MainActivity : AppCompatActivity(), LocationListener {

    val FINE_REQUEST = 54321
    val REQUEST_PERMISSIONS_CODE = 128
    var nomeArquivo = Calendar.getInstance().time;

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnLocalizacao = btnLocalizacao
        btnLocalizacao.setOnClickListener {
            carregarCordenadas()
            criarAquivo()
            //lerArquivo()
        }

        btnListarArquivos.setOnClickListener {
            val intent = Intent(this, ListaArquivosActivity::class.java)
            var nomeArquivo = Calendar.getInstance().time;
            intent.putExtra("arquivo","$nomeArquivo.crd")

            startActivity(intent)
        }
    }

    private fun callDialog(mensagem: String,
                           permissions: Array<String>) {
        var mDialog = AlertDialog.Builder(this)
            .setTitle("Permissão")
            .setMessage(mensagem)
            .setPositiveButton("Ok")
            { dialog, id ->
                ActivityCompat.requestPermissions(
                    this@MainActivity, permissions,
                    REQUEST_PERMISSIONS_CODE)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel")
            { dialog, id ->
                dialog.dismiss()
            }
        mDialog.show()
    }

    fun criarAquivo(){
        val file = File(getExternalFilesDir(null), "$nomeArquivo.crd")

        Log.i("TP01-SMPA", "Carregou o nome do arquivo $nomeArquivo")

        if(file.exists()){
            file.delete()
            Log.i("TP01-SMPA", "O arquivo foi apagado")
        }
        else{
            try {
                val os: OutputStream = FileOutputStream(file)

                os.write("Teste".toByteArray())
                os.close()

                Toast.makeText(this, "Localização salva com sucesso!", Toast.LENGTH_LONG).show()
                Log.i("TP01-SMPA", "Arquivo criado")
            } catch (e: IOException) {
                Log.d("Permissao", "Erro de escrita em arquivo")
            }
        }
    }

    fun lerArquivo() {

        val file = File(getExternalFilesDir(null), "$nomeArquivo.crd")
        if(!file.exists()) {
            Toast.makeText(this@MainActivity,
                "Arquivo não encontrado",
                Toast.LENGTH_SHORT).show()
            return
        }
        val text = StringBuilder()
        try {
            val br = BufferedReader(FileReader(file))
            var line: String?
            while (br.readLine().also { line = it } != null) {
                text.append(line)
                text.append('\n')
            }
            br.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        Toast.makeText(this@MainActivity,
            text.toString(),
            Toast.LENGTH_SHORT).show()
    }

    fun callWriteOnSDCard(view: View?) {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.WRITE_EXTERNAL_STORAGE
                )) {
                callDialog(
                    "É preciso liberar WRITE_EXTERNAL_STORAGE",
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                )
            } else {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_PERMISSIONS_CODE)
            }
        } else {
            criarAquivo()
        }
    }

    private fun carregarCordenadas(){

        Log.i("TP01-SMPA", "Iniciando aplicacao...")
        try {
            val locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager
            val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

            var location: Location? = null

            if(isGpsEnabled){
                if (checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        2000L,
                        0f,
                        this
                    )

                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    val txtLatitude = this.findViewById<TextView>(R.id.txtLatitude)
                    val txtLongitude = this.findViewById<TextView>(R.id.txtLongitude)

                    txtLatitude.text = location?.latitude.toString()
                    txtLongitude.text = location?.latitude.toString()

                } else {
                    requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), FINE_REQUEST)
                }
            }


            if(isGpsEnabled){
                Log.i("TP01-SMPA", "Obtendo localizacao atraves do GPS")
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        2000L,
                        0F,
                        this
                    )
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    lblLatitude.text = location?.latitude.toString()
                    lblLongitude.text = location?.latitude.toString()
                }
            }else{
                Log.i("TP01-SMPA", "GPS desativado, ative para obter localizacao")
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), FINE_REQUEST)
            }

        }catch (ex: SecurityException){

            Log.i("TP01-SMPA", "Erro na localizacao ")
        }
        Log.i("TP01-SMPA", "Processamento das coordenadas concluido")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_PERMISSIONS_CODE -> {
                var i = 0
                while (i < permissions.size) {
                    if (permissions[i].equals(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            ignoreCase = true
                        )
                        && grantResults[i] == PackageManager.PERMISSION_GRANTED
                    ) {
                        carregarCordenadas()
                    } else if (permissions[i].equals(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            ignoreCase = true
                        )
                        && grantResults[i] == PackageManager.PERMISSION_GRANTED
                    ) {
                        criarAquivo()
                    } else if (permissions[i].equals(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            ignoreCase = true
                        )
                        && grantResults[i] == PackageManager.PERMISSION_GRANTED
                    ) {
                        lerArquivo()
                    }
                    i++
                }
            }
        }
        super.onRequestPermissionsResult(
            requestCode, permissions, grantResults
        )
    }

    override fun onLocationChanged(location: Location) {
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
    }

    override fun onProviderEnabled(provider: String) {
    }

    override fun onProviderDisabled(provider: String) {
    }



}