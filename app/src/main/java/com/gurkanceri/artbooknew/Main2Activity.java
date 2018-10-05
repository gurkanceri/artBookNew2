package com.gurkanceri.artbooknew;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Main2Activity extends AppCompatActivity {

    //imageVievi tanımladık
    ImageView imageView;
    EditText editText;
    Button buton;

    //10 - Database tanımlıcaz static olarak çünkü heryerden erişebilsin diye
    static SQLiteDatabase database;
    //save metodunda yazıcaz
    Bitmap selectedImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //imageView nerede olduğunun idsini tanımladık
        imageView=(ImageView) findViewById(R.id.imageView);
        editText =(EditText) findViewById(R.id.editText);
        buton = (Button) findViewById(R.id.button);

        //Eski resimsi birşey yap Yeni resimse başka birşey yap dicez altta
        Intent intent = getIntent();
        String info = intent.getStringExtra("info");

        if(info.equalsIgnoreCase("new")){

            //yeni eklenicek resim olduğunda arkaplan resmini resim_seç imagesi ile göster
            Bitmap backround = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.resimsec);
            imageView.setImageBitmap(backround);

            //burda eğer yeni bir resim eklenicek ise ne yapması gerektiğini yazıyoruz
            buton.setVisibility(View.VISIBLE);
            //yeni eklenicek ise butonu görünür yap

            //yeni resim eklenicek ise editTexti boş getir
            editText.setText("");

        }
        else{
            buton.setVisibility(View.INVISIBLE);
            //eklenmeyecek ise görünmez yap ve kayıt et
        }


    }
    // Main2Activty e İmageview falan ekledik şimdi burada tanımlamasını yapıcaz

    public void select(View view){ //imageView OnClik metoduna select yazdık

        //telfonlarından resim seçiceğimiz için kullanıcı izinleri gerekli bunun için intent yazıcaz
        // bu intenti başlatmadan önce kullanıcının izni var mı yokmu kontrol etmemiz lazım
        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            //Şimdi bunu izin verilmediysa aşşağıdaki kodu yazıcaz
            //izin isticez
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},2);

        }
        //Eğer izin varsa resmi seç
        else{
            Intent intent=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            //2- ForResult yapmamızı sebebi bir resim datası alıp o resmi kullanıcağımız için forresult yazdık
            startActivityForResult(intent,1);
            }
    }
    //şimdi yoksa ve kullanıcı izin verirse ne yapıcak onu yazalım
    // onRequestPermissionsResult yazıcaz

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //yani kulalnıcı biz ilk izin verdiğinde ne yapıcaz

        if(requestCode == 2)// 2 yazmamızın sebebi yukaıda request 2 yazdığımız için
        {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) // eğer bu şartlar oluşur ise aşşağıdaki kodu yaz

            {
                Intent intent=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //2- ForResult yapmamızı sebebi bir resim datası alıp o resmi kullanıcağımız için forresult yazdık
                startActivityForResult(intent,1);

                //Böylece bütün izinlerle ilgili herşeyi yapmış olduk

            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    // 3-Bu yazmış olduğumuz startActivityForResult u almak içinde aşşağıdaki kodu yazıcaz

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        //requestCode - resultCode -Intent data 3 tane kod verdi bize
        //requestCode = 1 Yukarıda yazdığımız değer

        if(requestCode == 1 && resultCode == RESULT_OK && data != null)
            //Burada requestCode 1 ise ve resultCode Ok ise ve data boş değilse aşşağıdakini yaptıysa demekki kullanıcı bir resim seçmiş demekti
        {
            // 4-Bu seçilen resim için URİ oluşturucaz
            Uri image = data.getData();
            // Sonra bir Bitmap oluşturucaz
            //bunu try ve cathc ile yapmamız lazım çünkü bu çalışmayabilir

            try {
                selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(),image);
                //5 -Bitmapı imageView tanımlıcaz
                imageView.setImageBitmap(selectedImage);
                //Böylece seçilen resmi al ve imageViewe koy dedik.
                //6 Kullanıcıdan izin almamız gerekiyor artık
                //kullanıcı izinlerini almak için sol menüden manifests tıkla - xmli aç -   <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"></uses-permission> ve bu kodu yaz


            }

            catch (IOException e) {
                e.printStackTrace();
            }


        }
            super.onActivityResult(requestCode, resultCode, data);


    }

    public void save(View view){ //Button onClik metoduna save yazmıştık

        //şimdi kaydetmeye geldi sıra
        //11 edittextteki yazıyı almak için
        String artName = editText.getText().toString();
        //12 - resmi kayıt etmek için
        // image aslında bitmap şeklinde kayıt etmicez ByteArray olarak kayıt edicez
        // image yi alıp sıkıştırıp ve sonra byteına ayırarrak dizinin çine kayıt et veri olarak kaydet
        //sonra o verileri alıp kayıt et dicez.

        //seçilen image diye bitmap oluşturucaz
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        selectedImage.compress(Bitmap.CompressFormat.PNG,50,outputStream);//böyleece zipledik
        //sonra byte dizisi oluşturmamız laızm
        byte[]  byteArray = outputStream.toByteArray(); //çevirmiş olduk

        //değişkenler oluşturuılmış oldu

        try{
            //oluşturuduğumz databaseyi hayta geçiricez
             database = this.openOrCreateDatabase("Arts",MODE_PRIVATE,null);
             database.execSQL("CREATE TABLE IF NOT EXISTS arts (name VARCHAR ,image BLOB)");

             //Girilen verileri almak için yeni bşey tanımlıyoruz
            String sqlString = "INSERT INTO arts(name,image) VALUES (?,?)";
            SQLiteStatement statement = database.compileStatement(sqlString);
            statement.bindString(1,artName);// edittextekini aldıkdık
            statement.bindBlob(2,byteArray);
            statement.execute();
            //Böyledce kullanıcıkın yazdığı ve seçtiiği resmi kayıt etmiş olduk
            //ve sonra bir intent daha oluşturduktan sonra anasayfaya geri döndürmek için


        }
        catch (Exception e){

            e.printStackTrace();
        }


        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);


    }



}
