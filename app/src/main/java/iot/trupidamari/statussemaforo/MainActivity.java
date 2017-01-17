package iot.trupidamari.statussemaforo;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import iot.trupidamari.statussemaforo.models.Semaforo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String TAG ="getStatus";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);





    }

    public void atravessar(View view) {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SemaforoService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        SemaforoService service = retrofit.create(SemaforoService.class);

        Semaforo semaforo = new Semaforo();
        semaforo.setSemaforoId(1);


        Call<Semaforo> requestStatus = service.getStatus(semaforo);

        requestStatus.enqueue(new Callback<Semaforo>() {
            @Override
            public void onResponse(Call<Semaforo> call, Response<Semaforo> response) {

                if(!response.isSuccess()){
                    Log.i("TAG", "Erro!!!!!!!!!: " + response.code() );
                }else{
                    Semaforo s = response.body();
                    Log.i(TAG,Integer.toString(s.getStatus()));

                    Context context = getApplicationContext();
                    CharSequence text = Integer.toString(s.getStatus());

                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }

            }

            @Override
            public void onFailure(Call<Semaforo> call, Throwable t) {
                Log.e(TAG,"Erro: " + t.getMessage());
            }
        });
    }
}
