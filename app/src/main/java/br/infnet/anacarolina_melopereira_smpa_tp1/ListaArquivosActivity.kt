package br.infnet.anacarolina_melopereira_smpa_tp1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_lista_arquivos.*
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException

class ListaArquivosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_arquivos)

        readFile()

        val arquivo : String? = intent.getStringExtra("arquivo")

        lblListaArquivos.text = arquivo

    }


    private fun readFile() {
        val file = File(getExternalFilesDir(null), "DemoFile.txt")
        val state = Environment.getExternalStorageState()

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
        Toast.makeText(this@ListaArquivosActivity,
            text.toString(),
            Toast.LENGTH_SHORT).show()

        lblListaArquivos.text = text
    }


}