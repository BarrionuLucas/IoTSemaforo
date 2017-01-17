package iot.trupidamari.statussemaforo;

import iot.trupidamari.statussemaforo.models.Semaforo;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by casa on 13/01/2017.
 */

public interface SemaforoService {
    public static final String BASE_URL="https://statussemaforo.herokuapp.com/api/";

    @POST("status")
    Call<Semaforo> getStatus(@Body Semaforo semaforo);

}
