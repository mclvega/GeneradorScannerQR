package com.example.qr

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.net.toUri
import com.example.qr.databinding.ActivityMainBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if(lbl_resultado.text.toString()=="") {

           ocultarBotones()
        }

        btn_copiar.setOnClickListener {
            copiarScanner()
        }
        btn_abrir.setOnClickListener {

            val intent=Intent(Intent.ACTION_VIEW,Uri.parse(lbl_resultado.text.toString()))
            startActivity(intent)



        }


        btn_generar.setOnClickListener {
            if (txt_texto.text.toString()!=""){
                generarQr()
            }else{
                Toast.makeText(this,"Ingresa algún texto para generar QR",Toast.LENGTH_SHORT).show()
            }


        }
        btn_limpiar.setOnClickListener {
            if(txt_texto.text.toString()!=""){
                txt_texto.setText("")
                img.setImageBitmap(null)
                Toast.makeText(this,"Texto y Codigo QR limpiados",Toast.LENGTH_SHORT).show()

            }
            else{
                Toast.makeText(this,"No hay nada que limpiar",Toast.LENGTH_SHORT).show()
            }

            lbl_resultado.setText("")
            ocultarBotones()
        }

        btn_scanner.setOnClickListener {

            scanner()
        }

    }

    private fun vereficarLink(){
        var link=lbl_resultado.text.toString()

        if(link.length>12 && link.substring(0,12)=="https://www."){

            btn_abrir.visibility=View.VISIBLE
        }else{
            btn_abrir.visibility=View.INVISIBLE
        }
    }
    private fun scanner(){

        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
        integrator.setPrompt("McL")
        integrator.setBeepEnabled(true)
        integrator.initiateScan()


    }

    private fun ocultarBotones(){
        btn_copiar.visibility = View.INVISIBLE
        btn_abrir.visibility = View.INVISIBLE
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        val result =IntentIntegrator.parseActivityResult(requestCode,resultCode,data)
        if(result!=null){

            if(result.contents==null){
                Toast.makeText(this,"Cancelado",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this,"Resultado : ${result.contents}",Toast.LENGTH_SHORT).show()
                lbl_resultado.setText(result.contents)

                btn_copiar.visibility = View.VISIBLE
                vereficarLink()


            }
        }
        else{

            super.onActivityResult(requestCode, resultCode, data)

        }


    }

    private fun copiarScanner(){
        var clipBoard:ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val c = lbl_resultado.text.toString()
        val clip = ClipData.newPlainText("Texto Copiado",c)
        clipBoard.setPrimaryClip(clip)
        Toast.makeText(this,"Resultado Copiado",Toast.LENGTH_SHORT).show()
    }
    private fun generarQr(){
        val multiFormatWriter = MultiFormatWriter()
        try{
            val bitMatrix:BitMatrix=multiFormatWriter.encode(txt_texto.text.toString(),BarcodeFormat.QR_CODE,300,300)
            val barcodeEncoder=BarcodeEncoder()
            val bitmap:Bitmap=barcodeEncoder.createBitmap(bitMatrix)
            img.setImageBitmap(bitmap)
        }catch (e:WriterException){
            e.printStackTrace()
        }
    }
}