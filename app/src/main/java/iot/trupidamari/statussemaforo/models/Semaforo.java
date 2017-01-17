package iot.trupidamari.statussemaforo.models;

/**
 * Created by casa on 13/01/2017.
 */

public class Semaforo {

    private int semaforoId;
    private boolean sucesso;
    private String mensagem;
    private int status;

    public int getSemaforoId() {
        return semaforoId;
    }

    public void setSemaforoId(int semaforoId) {
        this.semaforoId = semaforoId;
    }


    public boolean isSucesso() {
        return sucesso;
    }

    public void setSucesso(boolean sucesso) {
        this.sucesso = sucesso;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
