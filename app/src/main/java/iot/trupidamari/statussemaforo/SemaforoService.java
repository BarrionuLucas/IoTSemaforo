package iot.trupidamari.statussemaforo;

import iot.trupidamari.statussemaforo.models.Semaforo;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by casa on 13/01/2017.
 */

public interface SemaforoService {
    public static final String BASE_URL="http://10.0.2.2:8090/";

    @POST("api/status")
    Call<Semaforo> getStatus(@Body Semaforo semaforo);

}
