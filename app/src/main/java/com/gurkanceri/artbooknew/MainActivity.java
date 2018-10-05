package com.gurkanceri.artbooknew;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //1- listView ı tanımladık
    ListView listView;

    // 3 şimdi oluşturduğumuz menü ile MainActivity1 i bağlıcaz
    // Yeni bir metoddun içinde yapmamız gerekiyor.
    // onCreateOptionsMenu oluşturucaz

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { //bu daha önceden oluşturulmuş metod otomatik çıkardı

        //4 - burda MenuInfalter eklicez
        //Menuyü kullanabilmemiz için gerekli olan bir obje
        MenuInflater menuInflater = getMenuInflater();

        //5- Menu add_art menüsünü kullanmak için yol veriyoruz
        menuInflater.inflate(R.menu.add_art,menu);
        //Böylelikle hangi menüyü çıkarıcağını mainaktivitye göstermiş olduk
        return super.onCreateOptionsMenu(menu);

    }

    //6- Menü şeçilirse ne olucağını yazıcaz
    //onOptionsItemSelected bu metod ile
    //Tahmin edilceği gibi başka sayfaya gidicek
    //Şimdi yeni bir aktivity oluşturucaz sonra İntent ile diğer sayfaya geçmesini sağlıcaz
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //İntenti başlatmadan önce bir kontrol yaptıralım
        if(item.getItemId() == R.id.add_art) { // Buradaki menu item ID si eşit iste add_art İD sine
            // 7-intenti burda göster böylelikle diğer ekrana geçmesini sağlarız
            Intent intent = new Intent(getApplicationContext(),Main2Activity.class);
            intent.putExtra("info","new");//yeni bir resim mi eski resimmi görmek için

            //8- intenti  başlatıyoruz
            startActivity(intent);

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Layout ekranına listView ekledik ve İD sini listView yaptık
        //listView nerede olduğu İd sinin ne olduğunu tanımladık
        listView = (ListView) findViewById(R.id.listView);

        //2- sol üst köşede çıkması için üç  nokta üst menüsü oluşturucaz
        //bunun için res-sağ tık-new-Dirictory-menu ismini verdik
        //Sonra üstten res-menu-sağtık-new menu resourcefile - add_art verdik menü ismine
        //add_art.xml e tıkladık text sayfasına ilk item eklicez.
       // Menü eklemek için bu kodu yazdık
        // <item android:id="@+id/add_art" android:title="Add Art"></item>

        //şimdi oluşturduğumuz menü ile MainActivity1 i bağlıcaz
        //Yeni bir metoddun içinde yapmamız gerekiyor.

        //MAİN2 de yazdığımız kodlar bitti şimdi array list oluşturucaz dataları çekmek için
        //2 tane array list oluşturucaz

        ArrayList<String> artName = new ArrayList<String>();
        ArrayList<Bitmap> artImage = new ArrayList<Bitmap>();

        ArrayAdapter arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,artName);
        listView.setAdapter(arrayAdapter);

        //Şimdi databasede kayıtlı birşey var mı yok mu bakmasını istiyoruz
        //try ve catch yapıcaz

        try{
            //databaseye ulaşalım
            Main2Activity.database = this.openOrCreateDatabase("Arts",MODE_PRIVATE,null);;
            Main2Activity.database.execSQL("CREATE TABLE IF NOT EXISTS arts (name VARCHAR ,image BLOB)");// bunu tekrar yazdık çünkü eğer böyle bir tablo varsa 1 kere oluşturucağı için garantiye aldık
             //datayı çekmek için Cursor oluşturcaz

            Cursor cursor = Main2Activity.database.rawQuery("SELECT * FROM arts",null);
            //şimdi Columİndexleri oluşturucaz

            int nameIx = cursor.getColumnIndex("name");
            int imageIx = cursor.getColumnIndex("image");

            cursor.moveToFirst();//ilk satıra aldık
            //şimdi bulduğumuz dataları ArrayListe eklicez
            while (cursor != null){

                artName.add(getString(nameIx));
                //cursorda bulduğu nameIx i ard name ye ekle dedik
                //image için
                byte[] byteArray = cursor.getBlob(imageIx);
                Bitmap image = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
                artImage.add(image);

                cursor.moveToNext();
                arrayAdapter.notifyDataSetChanged();

            }

        }
        catch (Exception e){

            e.printStackTrace();
        }

    }
}
